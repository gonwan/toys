#!/usr/bin/python3
import argparse
import sys
from client import *


if sys.version_info[0] < 3 or (sys.version_info[0] == 3 and sys.version_info[1] < 4):
    print("Python 3.4 or later is required.")
    exit(-1)


@asyncio.coroutine
def run(concurrency, ip, port, funcid, timeout):
    def _init_counter(all):
        def counter():
            counter.i += 1
            if counter.i % 100 == 0:
                print('%d clients finished.' % counter.i)
            if counter.i == all:
                print('all %d clients finished.' % all)
        counter.i = 0
        return counter

    t0 = time.time()
    finished_callback = _init_counter(concurrency)
    future_client_dict = dict()
    for i in range(1, concurrency+1, 1):
        cli = Client()
        # username = 'mt{0:03}'.format(i)
        username = 'optpst{0}'.format(i)
        password = '123456'
        fut = asyncio.async(cli.run_test(ip, port, username, password, funcid, finished_callback))
        future_client_dict[fut] = cli
    _, pending = yield from asyncio.wait(future_client_dict.keys(), timeout=timeout)
    # explicitly close clients before closing event loop, to prevent exceptions
    for cli in future_client_dict.values():
        yield from cli.close()
    yield from asyncio.wait(future_client_dict.keys())
    if len(pending) != 0:
        print('\nsome clients not finished in time (%d/%d):' % (len(pending), concurrency))
        pending_usernames = [future_client_dict[f].username for f in pending]
        pending_usernames.sort()
        print('\n'.join(pending_usernames))
        print('\n!!! increase timeout value with -t switch and run again. !!!')
    # statistics
    elapsed = time.time() - t0
    print('\nall %f seconds elapsed.' % elapsed)
    _sum_bytes = 0
    _fails, _5s, _10s, _30s, _60s, _others, _min, _max, _sum_time = 0, 0, 0, 0, 0, 0, timeout, 0, 0
    for cli in future_client_dict.values():
        stats = cli.perf_stats
        _sum_bytes += stats[0]
        if stats[-1] == -1:
            _fails += 1
        else:
            _sum = sum(stats[1:])
            _sum_time += _sum
            if _min > _sum:
                _min = _sum
            if _max < _sum:
                _max = _sum
            # range
            if _sum < 5:
                _5s += 1
            elif _sum < 10:
                _10s += 1
            elif _sum < 30:
                _30s += 1
            elif _sum < 60:
                _60s += 1
            else:
                _others += 1
    print('read_bytes=%dkb read_rate=%dkb/s' % (_sum_bytes/1024, _sum_bytes/1024/elapsed))
    print('fails=%d [0,5)=%d [5,10)=%d [10,30)=%d [30,60)=%d [60,~~)=%d' % (_fails, _5s, _10s, _30s, _60s, _others))
    _success = concurrency - _fails
    if _success > 0:
        print('min=%fs max=%fs avg=%fs avg/req=%fs' % (_min, _max, _sum_time/_success, elapsed/_success))


def main():
    function_ids = Client.get_function_ids()
    desc = 'Performance test for QB:'
    desc += '\n  # {0} -c 100 -h 172.16.8.85 -p 28899 -f 50121'.format(os.path.basename(sys.argv[0]))
    desc += '\n\nAvailable functions include: {0}.'.format(str(function_ids))
    parser = argparse.ArgumentParser(description=desc, add_help=False, formatter_class=argparse.RawTextHelpFormatter)
    group = parser.add_argument_group("Options")
    group.add_argument('-c', dest='concurrency', required=True, type=int, help='concurrency level')
    group.add_argument('-h', dest='ip', required=True, help='host ip of login server')
    group.add_argument('-p', dest='port', required=True, type=int, help='host port of login server')
    group.add_argument('-f', dest='function', required=True, help='function to test')
    group.add_argument('-t', dest='timeout', type=int, help='timeout which is automatically set if not provided')
    if len(sys.argv) == 1:
        parser.print_help()
        exit(0)
    args = vars(parser.parse_args())
    function = args['function']
    if not (function in function_ids):
        print('ERROR: cannot find function id=%s.' % function)
        exit(-1)
    timeout = args['timeout']
    if timeout is None:
        timeout = args['concurrency'] * 0.5
    if timeout < 5.0:  # do not close event loop too early
        timeout = 5.0
    # log
    prog = os.path.basename(os.getcwd())
    logfile = datetime.datetime.now().strftime(prog + '_%Y%m%d_%H%M%S.log')
    logging.basicConfig(filename=logfile, format='%(asctime)-15s %(levelname)-8s %(message)s', level=logging.DEBUG)
    # begin test
    if sys.platform == 'win32':
        loop = asyncio.ProactorEventLoop()
    else:
        loop = asyncio.SelectorEventLoop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(run(args['concurrency'], args['ip'], args['port'], function, timeout))
    loop.close()

main()
