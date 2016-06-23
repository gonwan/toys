#!/usr/bin/python3
import asyncio
import datetime
import hashlib
import logging
import os
import time
import types
from pack import *


class LoginLoginRequest(object):

    def __init__(self, _username, _password, _vcode=b'', _version=20160601.0, _buffer=b'', _reserved=b'V8'):
        self.username = _username.encode('utf-8')  # 32+1
        self.password = hashlib.md5(_password.encode('utf-8')).hexdigest().encode('utf-8')  # 32+1*2
        self.vcode = _vcode        # 32+1
        self.version = _version    # 8
        self.buffer = _buffer      # 512
        self.reserved = _reserved  # 32

    def type(self):
        return 6000

    def pack(self):
        return struct.pack('=33s34s33sd512s32s', self.username, self.password, self.vcode, self.version, self.buffer, self.reserved)


class LoginLoginResponse(object):

    def __init__(self):
        self.cookie = None          # 20+1
        self.main_ip = None         # 64
        self.main_port = 0          # 2
        self.backup_ip1 = None      # 64
        self.backup_port1 = 0       # 2
        self.backup_ip2 = None      # 64
        self.backup_port2 = 0       # 2
        self.systime = 0            # 4
        self.username = None        # 32+1
        self.security_level = 0     # 4
        self.reserved1 = None       # 20
        self.last_ip = None         # 64
        self.last_date = None       # 8+1
        self.last_time = None       # 6+1
        self.reserved2 = None       # 20
        self.valid_time = None      # 24
        self.vid = 0                # 4
        self.utid = 0               # 4
        self.update_flag = 0        # 4
        self.userid = None          # 32+1

    def __repr__(self):
        return '<LoginLoginResponse: ip={0} port={1} userid={2} username={3} cookie={4}>'.format(self.main_ip, self.main_port, self.userid, self.username, self.cookie)

    def __str__(self):
        return '<LoginLoginResponse: ip={0} port={1} userid={2} username={3} cookie={4}>'.format(self.main_ip, self.main_port, self.userid, self.username, self.cookie)

    def unpack(self, buff):
        self.cookie, self.main_ip, self.main_port, self.backup_ip1, self.backup_port1, self.backup_ip2, self.backup_port2, \
            self.systime, self.username, self.security_level, self.reserved1, self.last_ip, self.last_date, self.last_time, \
            self.reserved2, self.valid_time, self.vid, self.utid, self.update_flag, self.userid = struct.unpack('!21s64sh64sh64shi33si20s64s9s7s20s24siii33s', buff)
        self.main_ip = self.main_ip.decode('utf-8').rstrip('\x00')
        self.userid = self.userid.decode('utf-8').rstrip('\x00')
        self.username = self.username.decode('utf-8').rstrip('\x00')
        self.cookie = self.cookie.decode('utf-8').rstrip('\x00')


class GatewayLoginRequest(object):

    def __init__(self, _username, _cookie, _userid):
        self.username = _username.encode('utf-8')   # 32+1
        self.cookie = _cookie.encode('utf-8')       # 20+1
        self.userid = _userid.encode('utf-8')       # 32+1

    def type(self):
        return 1201

    def pack(self):
        return struct.pack('=33s21s33s', self.username, self.cookie, self.userid)


class GatewayLoginResponse(object):

    def __init__(self):
        pass

    def unpack(self, buff):
        pass


class Client(object):

    EMPTY = b'\x00' * (100 * 1000000)  # 100MB

    def __init__(self):
        self.reader = None
        self.writer = None
        self.username = None
        self.perf_stats = [0, -1, -1, -1]  # [ read bytes, login time, gateway time, message time ]

    @classmethod
    def get_test_names(cls):
        names = []
        for k, v in cls.__dict__.items():
            f = k.find('test_')
            if isinstance(v, types.FunctionType) and f == 0:
                names.append(k[5:])
        names.sort()
        return names

    @asyncio.coroutine
    def _send_raw_message(self, msgtype, msgbuff):
        head = RequestHead(msgtype)
        packaged_buffer = Pack.pack(head.pack(), msgbuff)
        logging.debug('[%s] sending raw message %s.' % (self.username, msgtype))
        self.writer.write(packaged_buffer)
        yield from self.writer.drain()

    @asyncio.coroutine
    def _recv_raw_message(self, log=True):
        meta = yield from self.reader.readexactly(8)
        magic, msglen = struct.unpack('!II', meta)
        msg_buff = yield from self.reader.readexactly(msglen)
        raw_buff = Pack.unpack(meta + msg_buff)
        head = ResponseHead()
        head.unpack(raw_buff[:len(head)])
        self.perf_stats[0] += 8 + msglen
        if log:
            logging.debug('[%s] received raw message %s.' % (self.username, head.msgtype))
        return head, raw_buff[len(head):]

    @asyncio.coroutine
    def _send_qpid_message(self, msg):
        head = RequestHead(Pack.QPID_MSGTYPE)
        head_byte_buff = head.pack()
        body_array_buff = array.array('B', b'\0' * Message.MAX_BUFFER_SIZE)
        offset = msg.encode(body_array_buff)
        body_byte_buff = body_array_buff.tobytes()[:offset]
        packaged_buffer = Pack.pack(head_byte_buff, body_byte_buff)
        logging.debug('[%s] sending qpid message %s.' % (self.username, msg.get_type()))
        self.writer.write(packaged_buffer)
        yield from self.writer.drain()

    @asyncio.coroutine
    def _recv_qpid_message(self):
        head, body_buff = yield from self._recv_raw_message(False)
        if head.msgtype != Pack.QPID_MSGTYPE:
            return
        msg = Message()
        msg.decode(body_buff, True)
        logging.debug('[%s] received qpid message %s.' % (self.username, msg.get_type()))
        return head, msg

    @asyncio.coroutine
    def _drain_qpid_messages(self, funcid):
        while True:
            ret = yield from self._recv_qpid_message()
            if ret is None:
                continue
            msg = ret[1]
            if msg.get_type() != funcid:
                continue
            ackhead = msg.get_value('AckMsgHead').value
            index = ackhead.get_value('idx').value
            total = ackhead.get_value('total').value
            logging.debug('[%s] drained qpid message %s: %d/%d.' % (self.username, funcid, index, total))
            if index == total - 1:
                break

    @asyncio.coroutine
    def connect_login(self, ip, port, username, password):
        self.username = username
        self.reader, self.writer = yield from asyncio.open_connection(ip, port)
        yield from self.reader.readexactly(20)  # welcome
        req = LoginLoginRequest(username, password)
        yield from self._send_raw_message(req.type(), req.pack())
        head, body_buff = yield from self._recv_raw_message()
        if head.retcode != 0 and head.retcode != 7 and head.retcode != 11:  # ignore ip/mac failure
            print('ERROR: [%s] [login] %d - %s.' % (self.username, head.retcode, head.desc))
            return
        res = LoginLoginResponse()
        res.unpack(body_buff)
        return res

    @asyncio.coroutine
    def connect_gateway(self, ip, port, username, userid, cookie):
        self.reader, self.writer = yield from asyncio.open_connection(ip, port)
        yield from self.reader.readexactly(20)  # welcome
        req = GatewayLoginRequest(username, cookie, userid)
        yield from self._send_raw_message(req.type(), req.pack())
        head, body_buff = yield from self._recv_raw_message()
        if head.retcode != 0:
            print('ERROR: [%s] [gateway] %d - %s.' % (self.username, head.retcode, head.desc))
            return False
        res = GatewayLoginResponse()
        res.unpack(body_buff)
        return True

    @asyncio.coroutine
    def run_secret(self, ip, port):
        self.reader, self.writer = yield from asyncio.open_connection(ip, port)
        yield from self._send_raw_message(1201, Client.EMPTY)

    @asyncio.coroutine
    def run_secret2(self, ip, port, count):
        self.reader, self.writer = yield from asyncio.open_connection(ip, port)
        msg = Message()
        msg.init('50121', '', '10000')
        values_map = VariantMap([
            ('IMQ_RequestType', Variant(VType.str, 'BONDOFFER')),
            ('IMQ_CompanyID', Variant(VType.list, VariantList([
                Variant(VType.map, VariantMap([
                    ('IMQ_CompanyID', Variant(VType.str, '1'))
                ]))
            ]))),
        ])
        msg.set_values(values_map)
        for i in range(count):
            yield from self._send_qpid_message(msg)

    @asyncio.coroutine
    def run_test(self, ip, port, username, password, funcid, finished_callback=None):  # all-in-one function
        while True:
            try:
                test_func = getattr(self, 'test_' + funcid)
            except AttributeError:
                print('ERROR: [%s] cannot find function id=%s.' % (self.username, funcid))
                break
            # do the job
            try:
                # login
                t0 = time.time()
                res = yield from self.connect_login(ip, port, username, password)
                self.writer.close()
                self.writer = None
                if res is None:
                    break
                self.perf_stats[1] = time.time() - t0
                # gateway
                yield from asyncio.sleep(0.05)  # workaround
                t0 = time.time()
                res = yield from self.connect_gateway(res.main_ip, res.main_port, res.username, res.userid, res.cookie)
                if not res:
                    break
                self.perf_stats[2] = time.time() - t0
                # test cases
                t0 = time.time()
                yield from test_func()
                self.perf_stats[3] = time.time() - t0
            except OSError as e:
                logging.info('[%s] %s.' % (self.username, str(e)))
            except EOFError as e:
                logging.info('[%s] %s.' % (self.username, str(e)))
            break
        if self.writer:
            self.writer.close()
        if finished_callback:
            finished_callback()
        logging.info('[%s] finished testing %s: %s.' % (self.username, funcid, str(self.perf_stats)))

    @asyncio.coroutine
    def test_50121(self):  # best price
        def _build_msg(_msg, _id):
            values_map = VariantMap([
                ('IMQ_RequestType', Variant(VType.str, 'BONDOFFER')),
                ('IMQ_CompanyID', Variant(VType.list, VariantList([
                    Variant(VType.map, VariantMap([
                        ('IMQ_CompanyID', Variant(VType.str, _id))
                    ]))
                ]))),
            ])
            _msg.set_values(values_map)
        msg = Message()
        msg.init('50121', '', '10000')
        _build_msg(msg, '1')
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('50121')
        _build_msg(msg, '2')
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('50121')
        _build_msg(msg, '3')
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('50121')

    @asyncio.coroutine
    def test_50124(self):  # market stream deal
        msg = Message()
        msg.init('50124', '', '10000')
        values_map = VariantMap([
            ('IMQ_RequestType', Variant(VType.str, 'BOND_DEAL')),
            ('IMQ_CompanyID', Variant(VType.list, VariantList([
                Variant(VType.map, VariantMap([
                    ('IMQ_CompanyID', Variant(VType.str, '1')),
                    ('IMQ_CompanyID', Variant(VType.str, '2')),
                    ('IMQ_CompanyID', Variant(VType.str, '3'))
                ]))
            ]))),
            ('IMQ_VersionFrom', Variant(VType.i32, 0)),
            ('IMQ_VersionTo', Variant(VType.i32, -1)),
            ('IMQ_BondKey', Variant(VType.str, '')),
            ('IMQ_ListedMarket', Variant(VType.str, ''))
        ])
        msg.set_values(values_map)
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('50124')

    @asyncio.coroutine
    def test_53014(self):
        msg = Message()
        msg.init('53014', '', '10000')
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('53014')

    def test_55020(self):
        def _build_msg(_msg, _id):
            values_map = VariantMap([
                ('UnderwriterID', Variant(VType.str, _id)),
                ('Version', Variant(VType.i32, 20160301))
            ])
            _msg.set_values(values_map)
        msg = Message()
        msg.init('55020', '', '10000')
        '''
        _build_msg(msg, '1')  # tp
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('55020')
        '''
        _build_msg(msg, '402880f034219aed0134219e10b00727')  # hy
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('55020')
        _build_msg(msg, 'ff808181359f49e601359f6dbb83031a')  # pa
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('55020')
        _build_msg(msg, '402880f034219aed0134219d4fbe0125')  # nb
        yield from self._send_qpid_message(msg)
        yield from self._drain_qpid_messages('55020')


@asyncio.coroutine
def run(ip, port, username, password, funcid):
    cli = Client()
    yield from cli.run_test(ip, port, username, password, funcid)


if __name__ == '__main__':

    def test_client():
        prog = os.path.basename(os.getcwd())
        logfile = datetime.datetime.now().strftime(prog + '_%Y%m%d_%H%M%S.log')
        logging.basicConfig(filename=logfile, format='%(asctime)-15s %(levelname)-8s %(message)s', level=logging.DEBUG)
        loop = asyncio.get_event_loop()
        loop.run_until_complete(run('172.16.8.85', 28899, 'binson.qian@sumscope.com', '123456', '50121'))
        loop.close()

    test_client()
