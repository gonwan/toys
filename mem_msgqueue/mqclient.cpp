#include <iostream>
#include <boost/interprocess/ipc/message_queue.hpp>
using namespace std;
using namespace boost::interprocess;

int main() {
    try {
        message_queue mq(open_only, "message_queue");
        unsigned int priority;
        message_queue::size_type recv_size;
        for (int i = 0; i < 100; i++) {
            int number;
            mq.receive(&number, sizeof(int), recv_size, priority);
            cout << "recv: " << i << endl;
        }
    } catch (interprocess_exception &e) {
        message_queue::remove("message_queue");
        cout << e.what() << endl;
        return 1;
    }
    message_queue::remove("message_queue");
    return 0;
}
