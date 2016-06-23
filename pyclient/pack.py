#!/usr/bin/python3
import pyaes
import zlib
from proto import *


class Pack(object):

    AES_KEY = b'rdsdf32_222jss2u'
    MAGIC_KEY = 382923
    QPID_MSGTYPE = 8201

    @staticmethod
    def pack(head, body, encrypt=False, compress=False, checksum=False):
        # head & body in bytes
        buff = head + body
        msgflag = 0
        # encrypt
        if encrypt:
            msgflag |= 0x02
            aes = pyaes.AESModeOfOperationCBC(Pack.AES_KEY)
            encrypted_buffer = b''
            floor = len(buff) & ~15
            for i in range(0, floor, 16):
                encrypted_buffer += aes.encrypt(buff[i: i+16])
            encrypted_buffer += buff[floor:]
        else:
            encrypted_buffer = buff
        # compress
        if compress:
            msgflag |= 0x04
            compressed_buffer = zlib.compress(encrypted_buffer, zlib.Z_DEFAULT_COMPRESSION)
        else:
            compressed_buffer = encrypted_buffer
        # checksum
        if checksum:
            msgflag |= 0x01
            crc = zlib.crc32(compressed_buffer)
            compressed_buffer += struct.pack('!I', crc)
        # meta
        meta = struct.pack('!IIB', Pack.MAGIC_KEY, len(compressed_buffer)+1, msgflag)
        return meta + compressed_buffer

    @staticmethod
    def unpack(buff):
        array_meta_buff = buff[:9]
        magic, msglen, msgflag = struct.unpack('!IIB', array_meta_buff)
        if magic != Pack.MAGIC_KEY:
            return
        # checksum
        if msgflag & 0x01:
            compressed_buffer = buff[9:-4]
            crc, = struct.unpack('!I', buff[-4:])
            if zlib.crc32(compressed_buffer) != crc:
                return
        else:
            compressed_buffer = buff[9:]
        # compress
        if msgflag & 0x04:
            try:
                encrypted_buffer = zlib.decompress(compressed_buffer)
            except zlib.error:
                return
        else:
            encrypted_buffer = compressed_buffer
        # encrypt
        if msgflag & 0x02:
            aes = pyaes.AESModeOfOperationCBC(Pack.AES_KEY)
            raw_buffer = b''
            floor = len(encrypted_buffer) & ~15
            for i in range(0, floor, 16):
                raw_buffer += aes.decrypt(encrypted_buffer[i: i+16])
            raw_buffer += encrypted_buffer[floor:]
        else:
            raw_buffer = encrypted_buffer
        # return
        return raw_buffer


class RequestHead(object):

    def __init__(self, _msgtype):
        self.msgtype = _msgtype

    def __len__(self):
        return 4

    def __repr__(self):
        return '<RequestHead: {0}>'.format(self.msgtype)

    def __str__(self):
        return '<RequestHead: {0}>'.format(self.msgtype)

    def pack(self):
        return struct.pack('!i', self.msgtype)


class ResponseHead(object):

    def __init__(self, _msgtype=0, _retcode=0, _desc='', _num=0, _idx=0, _records=0):
        self.msgtype = _msgtype
        self.retcode = _retcode
        self.desc = _desc
        self.num = _num
        self.idx = _idx
        self.records = _records

    def __len__(self):
        return 32

    def __repr__(self):
        return '<ResponseHead: {0}>'.format(self.msgtype)

    def __str__(self):
        return '<ResponseHead: {0}>'.format(self.msgtype)

    def unpack(self, buff):
        self.msgtype, self.retcode, self.desc, self.num, self.idx, self.records = struct.unpack('!ih20shhh', buff)
        self.desc = self.desc.decode('utf-8').rstrip('\x00')


if __name__ == '__main__':

    def test_pack():
        print('----- pack -----')
        body_array_buff = array.array('B', b'\0' * Message.MAX_BUFFER_SIZE)
        # head
        head = RequestHead(Pack.QPID_MSGTYPE)
        head_byte_buff = head.pack()
        # body
        msg = Message()
        msg.init('55008', '', '11194')
        values_map = VariantMap([
            ('xHyBondPushReq', Variant(VType.map, VariantMap([
                ('List', Variant(VType.list, VariantList([
                    Variant(VType.map, VariantMap([
                        ('BondKey', Variant(VType.str, 'G0001012016NCD005')),
                        ('ListedMarket', Variant(VType.str, 'CIB'))
                    ]))
                ]))),
                ('IsPushed', Variant(VType.i32, 1))
            ]))),
            ('OperatorID', Variant(VType.str, '109')),
            ('UnderwriterID', Variant(VType.str, 'ff80818146a41f860146d7b0edca1df6'))
        ])
        msg.set_values(values_map)
        offset = msg.encode(body_array_buff)
        body_byte_buff = body_array_buff.tobytes()[:offset]
        # pack
        packaged_buff = Pack.pack(head_byte_buff, body_byte_buff, True, True, True)
        Message._debug_buffer(packaged_buff)
        # unpack
        print()
        print('----- unpack -----')
        head2 = RequestHead(Pack.QPID_MSGTYPE)
        raw_buff = Pack.unpack(packaged_buff)
        Message._debug_buffer(raw_buff)
        body_array_buff2 = array.array('B', raw_buff[len(head2):])
        msg2 = Message()
        msg2.decode(body_array_buff2)
        print(head2)
        print(msg2)

    test_pack()
