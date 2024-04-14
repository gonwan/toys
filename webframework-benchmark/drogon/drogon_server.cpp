// g++ -O2 drogon_server.cpp -o drogon_server -I/usr/include/boost169 -ldrogon -ltrantor -lcrypto -lssl -luuid -ldl -lz -lpthread /usr/lib64/libboost_filesystem.so.1.69.0 /usr/local/lib64/libjsoncpp.a
// ~33w/s, slightly slower than go-gin server. No difference after disabling server & date headers...
#include <ctime>
#include <unordered_map>
#include <apr_thread_pool.h>
#include <boost/asio.hpp>
#include <drogon/drogon.h>
#include <soci/soci.h>
#include <soci/mysql/soci-mysql.h>
using namespace std;
using namespace boost;
using namespace drogon;
using namespace soci;

string tm_to_string(const std::tm *tm) {
    char str[32] = { 0 };
    std::strftime(str, sizeof(str), "%Y-%m-%d %H:%M:%S", tm);
    return str;
}

std::shared_ptr<connection_pool> init_connection_pool(size_t size = 5) {
    std::shared_ptr<connection_pool> p = std::make_shared<connection_pool>(size);
    for (int i = 0; i < size; i++) {
        session &sql = p->at(i);
        try {
#ifdef WIN32
            /* static load, otherwise libmysql.dll is not copied automatically. */
            sql.open(soci::mysql, "host=127.0.0.1 user=root password='123456' db=mysql charset=utf8 reconnect=1");
#else
            sql.open("mysql://host=127.0.0.1 user=root password='123456' db=mysql charset=utf8 reconnect=1");
#endif
            sql << "set time_zone = '+08:00'"; /* Asia/Shanghai */
//            string tz;
//            sql << "select @@session.time_zone", into(tz);
//            cout << "i=" << i << ", tz=" << tz << endl;
        }
        catch (soci_error& e) {
            cerr << "SOCI error: " << e.what() << endl;
        }
    }
    return p;
}

typedef const HttpRequestPtr req_t;
typedef std::function<void(const HttpResponsePtr &)> callback_t;

struct apr_wrapper {
    apr_wrapper() { apr_initialize(); }
    ~apr_wrapper() { apr_terminate(); }
} g_apr_wrapper;

std::shared_ptr<apr_thread_pool_t> init_thread_pool(size_t init = 10, size_t max = 200) {
    apr_pool_t *pool;
    apr_status_t rv = apr_pool_create(&pool, nullptr);
    if (rv != APR_SUCCESS) {
        return nullptr;
    }
    apr_thread_pool_t *thread_pool;
    rv = apr_thread_pool_create(&thread_pool, init, max, pool);
    if (rv != APR_SUCCESS) {
        apr_pool_destroy(pool);
        return nullptr;
    }
    return std::shared_ptr<apr_thread_pool_t>(
            thread_pool,
            [pool](apr_thread_pool_t *tp) {
                apr_thread_pool_destroy(tp);
                apr_pool_destroy(pool);
            });
}

int main()
{
    std::shared_ptr<connection_pool> conn_pool = init_connection_pool(5);
    /*
     * also checked:
     * poco thread pool: not friendly to lambda(temporary) functions.
     * folly thread pool: too many dependencies...
     */
    std::shared_ptr<apr_thread_pool_t> apr_thread_pool = init_thread_pool(10, 200);
    boost::asio::thread_pool boost_thread_pool(200);
    std::unordered_map<intptr_t, drogon::ResponseStreamPtr> sse_map;
    std::thread sse_server_thread([&sse_map] {
        for (int i = 1; i <= 1000 * 1000; i++) {
            this_thread::sleep_for(1s);
            char buffer[32] = { 0 };
            for (auto it = sse_map.begin(); it != sse_map.end(); ) {
                std::snprintf(buffer, sizeof(buffer), "id: %d\ndata: datatata\n\n", i);
                if (it->second->send(buffer)) {
                    ++it;
                } else {
                    it->second->close();
                    it = sse_map.erase(it);
                }
            }
        }
    });
    app().registerPostHandlingAdvice(
        [](const drogon::HttpRequestPtr &req, const drogon::HttpResponsePtr &resp) {
            resp->addHeader("Access-Control-Allow-Origin", "*");
        });
    app().registerHandler(
        "/text",
        [](const HttpRequestPtr &,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            auto resp = HttpResponse::newHttpResponse();
            resp->setBody("Hello, World!");
            callback(resp);
        },
        {Get});
    app().registerHandler(
        "/json",
        [](const HttpRequestPtr &,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            Json::Value json;
            json["Message"] = "Hello, World!";
            auto resp = HttpResponse::newHttpJsonResponse(json);
            callback(resp);
        },
        {Get});
    app().registerHandler(
        "/proxy",
        [](const HttpRequestPtr &req,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            auto client = HttpClient::newHttpClient("http://www.baidu.com");
            auto creq = HttpRequest::newHttpRequest();
            creq->setMethod(drogon::Get);
            creq->setPassThrough(true);
            client->sendRequest(creq, [req, callback](ReqResult result, const HttpResponsePtr &response) {
                if (result != ReqResult::Ok) {
                    Json::Value json;
                    json["Code"] = 999;
                    json["Message"] = "Error";
                    auto resp = HttpResponse::newHttpJsonResponse(json);
                    callback(resp);
                    return;
                }
                response->setPassThrough(true);
                callback(response);
            }, 2.0);
        },
        {Get});
    app().registerHandler(
        "/sse",
        [&sse_map](const HttpRequestPtr &,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            /* drogon 1.9.2+ */
            auto resp = drogon::HttpResponse::newAsyncStreamResponse(
                [&sse_map](drogon::ResponseStreamPtr stream) {
                    sse_map[(intptr_t)stream.get()] = std::move(stream);
                });
            resp->setContentTypeCodeAndCustomString(CT_CUSTOM, "text/event-stream");
            callback(resp);
        },
        {Get});
    app().registerHandler(
        "/longtask",
        [&apr_thread_pool](const HttpRequestPtr &req,
                std::function<void(const HttpResponsePtr &)> &&callback) {
            /* pass in req to extend request lifecycle */
            auto tp = new tuple<req_t, callback_t>(req, std::move(callback));
            apr_thread_pool_push(apr_thread_pool.get(), [](apr_thread_t *att, void *param) -> void * {
                auto tp = (std::tuple<req_t, callback_t> *) param;
                std::shared_ptr<std::tuple<req_t, callback_t>> stp(tp);
                req_t req = std::get<0>(*tp);
                callback_t callback = std::move(std::get<1>(*tp));
                this_thread::sleep_for(10s);
                Json::Value json;
                time_t t = std::time(nullptr);
                std::tm *tm = std::localtime(&t);
                json["Message"] = tm_to_string(tm);
                auto resp = HttpResponse::newHttpJsonResponse(json);
                callback(resp);
                return nullptr;
            }, tp, 0, nullptr);
        },
        {Get});
    app().registerHandler(
        "/db",
        [&boost_thread_pool, &conn_pool](const HttpRequestPtr &req,
                std::function<void(const HttpResponsePtr &)> &&callback) {
            boost::asio::post(boost_thread_pool, [req, callback, &conn_pool]() {
                session sql(*conn_pool);
                //statement st = (sql.prepare << "select user, host, password_expired, password_last_changed from user");
                rowset<row> rs = (sql.prepare << "select user, host, password_expired, password_last_changed from user");
                Json::Value json;
                for (auto it = rs.begin(); it != rs.end(); ++it) {
                    std::tm tm = it->get<std::tm>(3);
                    Json::Value v;
                    v["user"] = it->get<string>(0);
                    v["host"] = it->get<string>(1);
                    v["password_expired"] = it->get<string>(2);
                    v["password_last_changed"] = tm_to_string(&tm);
                    json.append(v);
                }
                //cout << "thread=" << this_thread::get_id() << endl;
                auto resp = HttpResponse::newHttpJsonResponse(json);
                callback(resp);
            });
        },
        {Get});
    LOG_INFO << "Server running on 0.0.0.0:8099";
    app().addListener("0.0.0.0", 8099)
        .setThreadNum(0)
        .setMaxConnectionNum(10000)
        .enableServerHeader(false)
        .enableDateHeader(false)
        //.enableRunAsDaemon()
        .run();
}
