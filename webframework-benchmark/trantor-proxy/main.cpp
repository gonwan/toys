// Tested with trantor 1.5.7
// ~30w/s, utilize ~1000% of total 2400% cpu.
#include <atomic>
#include <trantor/net/TcpClient.h>
#include <trantor/net/TcpServer.h>

using namespace std;
using namespace trantor;

const InetAddress upstreamAddrs[] = { InetAddress("172.16.56.137", 8099), InetAddress("172.16.56.138", 8099), InetAddress("172.16.56.139", 8099) };
const int upstreamAddrSize = sizeof(upstreamAddrs) / sizeof (upstreamAddrs[0]);
atomic<int> upstreamIndex(0);

void OnServerRecvMessage(const TcpConnectionPtr &connPtr, MsgBuffer *buf) {
    LOG_TRACE << "22222";
    shared_ptr<TcpClient> client = connPtr->getContext<TcpClient>();
    TcpConnectionPtr conn = client->connection();
    /* FIXME: partial workaround!!! */
    if (conn == nullptr) {
        connPtr->getLoop()->runAfter(0.001, [connPtr, buf]() {
            OnServerRecvMessage(connPtr, buf);
        });
        return;
    }
    if (conn->disconnected()) {
        return;
    }
    LOG_TRACE << "33333";
    conn->send(buf->peek(), buf->readableBytes());
    buf->retrieveAll();
}

/*
 *
 */
int main() {
    EventLoopThread loopThread; /* acceptor thread */
    Logger::setLogLevel(Logger::kTrace);
    loopThread.run();
    InetAddress serverAddr(8092);
    TcpServer server(loopThread.getLoop(), serverAddr, "test");
    server.setRecvMessageCallback(OnServerRecvMessage);
    server.setConnectionCallback([](const TcpConnectionPtr &connPtr) {
        if (connPtr->connected()) {
            LOG_DEBUG << "New connection: " << connPtr->peerAddr().toIpPort();
            int idx = upstreamIndex.fetch_add(1) % upstreamAddrSize;
            shared_ptr<TcpClient> client = make_shared<TcpClient>(connPtr->getLoop(), upstreamAddrs[idx], "");
            connPtr->setContext(client);
            client->setConnectionCallback([connPtr](const TcpConnectionPtr &clientConnPtr) {
                if (clientConnPtr->connected()) {
                    LOG_TRACE << "11111";
                } else if (clientConnPtr->disconnected()) {
                    connPtr->shutdown();
                }
            });
            client->setMessageCallback([connPtr](const TcpConnectionPtr &clientConnPtr, MsgBuffer *buf) {
                connPtr->send(buf->peek(), buf->readableBytes());
                buf->retrieveAll();
            });
            client->connect();
        } else if (connPtr->disconnected()) {
            LOG_DEBUG << "Closed connection: " << connPtr->peerAddr().toIpPort();
            shared_ptr<TcpClient> client = connPtr->getContext<TcpClient>();
            if (client != nullptr) {
                client->disconnect();
            }
        }
    });
    server.setIoLoopNum(24);
    server.start();
    loopThread.wait();
    return 0;
}
