// g++ -O2 drogon_server.cpp -o drogon_server -I/usr/include/boost169 -ldrogon -ltrantor -lcrypto -lssl -luuid -ldl -lz -lpthread /usr/lib64/libboost_filesystem.so.1.69.0 /usr/local/lib64/libjsoncpp.a
// ~33w/s, slightly slow than go-gin server. No difference after disabling server & date headers...
#include <drogon/drogon.h>
using namespace drogon;

int main()
{
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
    LOG_INFO << "Server running on 0.0.0.0:8099";
    app().addListener("0.0.0.0", 8099)
         .setThreadNum(24)
         .enableServerHeader(false)
         .enableDateHeader(false)
         .enableRunAsDaemon()
         .run();
}
