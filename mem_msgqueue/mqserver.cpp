#include <iostream>
#include <thread>
#include <boost/interprocess/ipc/message_queue.hpp>
using namespace std;
using namespace boost::interprocess;

int main() {
    try {
        message_queue::remove("message_queue");
        /* file based using shm_open(), can be found at /dev/shm. */
        message_queue mq(create_only, "message_queue", 10, sizeof(int));
        for (int i = 0; i < 100; i++) {
            this_thread::sleep_for(1s);
            mq.send(&i, sizeof(i), 0);
            cout << "sent: " << i << endl; /* blocking if queue is full */
        }
    } catch (interprocess_exception &e) {
        cout << e.what() << endl;
        return 1;
    }
    return 0;
}
