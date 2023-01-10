// g++ -O2 drogon_server.cpp -o drogon_server -I/usr/include/boost169 -ldrogon -ltrantor -lcrypto -lssl -luuid -ldl -lz -lpthread /usr/lib64/libboost_filesystem.so.1.69.0 /usr/local/lib64/libjsoncpp.a
// ~33w/s, slightly slower than go-gin server. No difference after disabling server & date headers...
#include <ctime>
#include <chrono>
#include <thread>
#include <boost/asio.hpp>
#include <drogon/drogon.h>
#include <soci/soci.h>
using namespace std;
using namespace boost;
using namespace drogon;
using namespace soci;

std::shared_ptr<connection_pool> init_connection_pool(size_t size = 5) {
    std::shared_ptr<connection_pool> p = std::make_shared<connection_pool>(size);
    for (int i = 0; i < size; i++) {
        session &sql = p->at(i);
        sql.open("mysql://host=10.228.131.133 user=root password='1qaz@WSX' db=mysql charset=utf8 reconnect=1");
        sql << "SET time_zone = '+08:00'"; /* Asia/Shanghai */
//        string tz;
//        sql << "select @@session.time_zone", into(tz);
//        cout << "i=" << i << ", tz=" << tz << endl;
    }
    return p;
}

int main()
{
    std::shared_ptr<connection_pool> conn_pool = init_connection_pool(5);
    asio::thread_pool thread_pool(200);
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
        "/sse",
        [](const HttpRequestPtr &,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            int i = 0;
            auto cb = [&i](char *buffer, size_t size) -> size_t {
                this_thread::sleep_for(1s);
                int c = std::snprintf(buffer, size, "id: %d\ndata: datatata\n\n", ++i);
                return c;
            };
            auto resp = drogon::HttpResponse::newStreamResponse(cb, "", CT_CUSTOM, "text/event-stream");
            callback(resp);
        },
        {Get});
    app().registerHandler(
        "/longtask",
        [&thread_pool](const HttpRequestPtr &req,
           std::function<void(const HttpResponsePtr &)> &&callback) {
            /* pass in req to extend request lifecycle */
            boost::asio::post(thread_pool, [req, callback](){
                this_thread::sleep_for(10s);
                Json::Value json;
                json["Message"] = "Hello, World!";
                auto resp = HttpResponse::newHttpJsonResponse(json);
                callback(resp);
            });
        },
        {Get});
    app().registerHandler(
            "/db",
            [&conn_pool](const HttpRequestPtr &,
                std::function<void(const HttpResponsePtr &)> &&callback) {
            session sql(*conn_pool);
            //statement st = (sql.prepare << "select user, host, password_expired, password_last_changed from user");
            rowset<row> rs = (sql.prepare << "select user, host, password_expired, password_last_changed from user");
            Json::Value json;
            for (auto it = rs.begin(); it != rs.end(); ++it) {
                std::tm t = it->get<std::tm>(3);
                char str[32] = { 0 };
                snprintf(str, sizeof(str), "%04d-%02d-%02d %02d:%02d:%02d",
                         t.tm_year+1900, t.tm_mon+1, t.tm_mday, t.tm_hour, t.tm_min, t.tm_sec);
                Json::Value v;
                v["user"] = it->get<string>(0);
                v["host"] = it->get<string>(1);
                v["password_expired"] = it->get<string>(2);
                v["password_last_changed"] = str;
                json.append(v);
            }
            auto resp = HttpResponse::newHttpJsonResponse(json);
            callback(resp);
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
