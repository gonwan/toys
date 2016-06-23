#!/usr/bin/python3
import array
import enum
import struct


class VType(enum.Enum):
    """ align with qpid types """
    bool = 0x08
    i8 = 0x02
    u8 = 0x03
    i16 = 0x11
    u16 = 0x12
    i32 = 0x21
    u32 = 0x22
    i64 = 0x31
    u64 = 0x32
    f32 = 0x23
    f64 = 0x33
    str = 0x95
    map = 0xa8
    list = 0xa9


class VariantList(list):
    """ list of Variants """

    def add_value(self, t, v):
        list.append(self, Variant(t, v))


class VariantMap(list):
    """ list of (str, Variant) tuples """

    def set_value(self, t, k, v):
        list.append(self, (k, Variant(t, v)))

    def get_value(self, k):
        for (_k, _v) in self:
            if _k == k:
                return _v


class Variant(object):

    def __init__(self, t, v):
        self.type = t
        self.value = v

    def __repr__(self):
        return '<{0}: {1}>'.format(self.type, self.value)

    def __str__(self):
        return '<{0}: {1}>'.format(self.type, self.value)


class Message(object):

    # align with qb..
    MAX_BUFFER_SIZE = 1024 * 40

    def __init__(self):
        self.meta = VariantMap()
        self.values = VariantMap()

    def __repr__(self):
        return 'meta={0}\nvalues={1}\n'.format(self.meta, self.values)

    def __str__(self):
        return 'meta={0}\nvalues={1}\n'.format(self.meta, self.values)

    def init(self, t, sub, msgid):
        self.meta.append(('Type.Prop', Variant(VType.str, t)))
        self.meta.append(('Subject.Prop', Variant(VType.str, sub)))
        self.meta.append(('MessageID.Prop', Variant(VType.str, msgid)))

    def get_type(self):
        if len(self.meta) == 3:
            return self.meta[0][1].value

    def get_subject(self):
        if len(self.meta) == 3:
            return self.meta[1][1].value

    def get_message_id(self):
        if len(self.meta) == 3:
            return self.meta[2][1].value

    def set_values(self, values_map):
        self.values = values_map

    def set_value(self, t, k, v):
        self.values.append((k, Variant(t, v)))

    def get_value(self, k):
        for (_k, _v) in self.values:
            if _k == k:
                return _v

    def set_str(self, k, v):
        self.values.append((k, Variant(VType.str, v)))

    def set_list(self, k, v):
        self.values.append((k, Variant(VType.list, v)))

    def set_map(self, k, v):
        self.values.append((k, Variant(VType.map, v)))

    def encode(self, buffer):
        off = Message._encode_container(self.meta, buffer, 0)
        off = Message._encode_container(self.values, buffer, off)
        return off

    def decode(self, buffer, lite=False):
        # meta
        off = Message._decode_container(self.meta, buffer, 0)
        if off is None:
            return
        # values
        if lite:
            off = Message._decode_container_lite(self.values, buffer, off)
        else:
            off = Message._decode_container(self.values, buffer, off)
        if off is None:
            return
        return off

    @classmethod
    def _encode_container(cls, container, buffer, offset):
        off = offset
        # stack of (container, iterator of container, offset of size byte of container)
        parent_stack = [(container, container.__iter__(), off)]
        struct.pack_into('>II', buffer, off, 0, len(container))
        off += 8  # 4 + 4
        while len(parent_stack) != 0:
            (c, it, sz) = parent_stack[-1]
            try:
                curr = it.__next__()
                var = curr
                if isinstance(curr, tuple):
                    (k, var) = curr
                    # key
                    kbytes = k.encode('utf-8')
                    klen = len(kbytes)
                    struct.pack_into('>B'+str(klen)+'s', buffer, off, klen, kbytes)
                    off += 1 + klen  # len(1b) + data(nb)
                # value
                if var.type == VType.bool:
                    struct.pack_into('>B?', buffer, off, var.type.value, 1 if var.value else 0)
                    off += 2  # 1 + 1
                elif var.type == VType.i8:
                    struct.pack_into('>Bb', buffer, off, var.type.value, var.value)
                    off += 2  # 1 + 1
                elif var.type == VType.u8:
                    struct.pack_into('>BB', buffer, off, var.type.value, var.value)
                    off += 2  # 1 + 1
                elif var.type == VType.i16:
                    struct.pack_into('>Bh', buffer, off, var.type.value, var.value)
                    off += 3  # 1 + 2
                elif var.type == VType.u16:
                    struct.pack_into('>BH', buffer, off, var.type.value, var.value)
                    off += 3  # 1 + 2
                elif var.type == VType.i32:
                    struct.pack_into('>Bi', buffer, off, var.type.value, var.value)
                    off += 5  # 1 + 4
                elif var.type == VType.u32:
                    struct.pack_into('>BI', buffer, off, var.type.value, var.value)
                    off += 5  # 1 + 4
                elif var.type == VType.i64:
                    struct.pack_into('>Bl', buffer, off, var.type.value, var.value)
                    off += 9  # 1 + 8
                elif var.type == VType.u64:
                    struct.pack_into('>BL', buffer, off, var.type.value, var.value)
                    off += 9  # 1 + 8
                elif var.type == VType.f32:
                    struct.pack_into('>Bf', buffer, off, var.type.value, var.value)
                    off += 5  # 1 + 4
                elif var.type == VType.f64:
                    struct.pack_into('>Bd', buffer, off, var.type.value, var.value)
                    off += 9  # 1 + 8
                elif var.type == VType.str:
                    vbytes = var.value.encode('utf-8')
                    vlen = len(vbytes)
                    struct.pack_into('>BH'+str(vlen)+'s', buffer, off, var.type.value, vlen, vbytes)
                    off += 3 + vlen  # type(1b) + len(2b) + data(nb)
                elif var.type == VType.map:
                    # set dummy len to 0
                    struct.pack_into('>BII', buffer, off, var.type.value, 0, len(var.value))
                    off += 9  # 1 + 4 + 4
                    parent_stack.append((var.value, var.value.__iter__(), off-8))
                elif var.type == VType.list:
                    # set dummy len to 0
                    struct.pack_into('>BII', buffer, off, var.type.value, 0, len(var.value))
                    off += 9  # 1 + 4 + 4
                    parent_stack.append((var.value, var.value.__iter__(), off-8))
            except StopIteration:
                struct.pack_into('>I', buffer, sz, (off-sz-4))  # len in bytes is before len in elements
                parent_stack.pop()
        return off

    @classmethod
    def _decode_container(cls, container, buffer, offset):
        def _add_value(_c, t, k, v):
            if isinstance(_c, VariantMap):
                _c.set_value(t, k, v)
            else:
                _c.add_value(t, v)

        off = offset
        buff_count, elem_count = struct.unpack_from('>II', buffer, off)
        off += 8
        # stack of (container, element count of container)
        parent_stack = [(container, Variant(VType.u32, elem_count))]
        while len(parent_stack) != 0:
            (c, ec) = parent_stack[-1]
            # key
            if isinstance(c, VariantMap):
                klen, = struct.unpack_from('>B', buffer, off)
                off += 1
                kbytes, = struct.unpack_from('>'+str(klen)+'s', buffer, off)
                key = kbytes.decode('utf-8')
                off += klen
            # value
            vtype, = struct.unpack_from('>B', buffer, off)
            off += 1
            if vtype == VType.bool.value:
                value, = struct.unpack_from('>?', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 1
            elif vtype == VType.i8.value:
                value, = struct.unpack_from('>b', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 1
            elif vtype == VType.u8.value:
                value, = struct.unpack_from('>B', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 1
            elif vtype == VType.i16.value:
                value, = struct.unpack_from('>h', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 2
            elif vtype == VType.u16.value:
                value, = struct.unpack_from('>H', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 2
            elif vtype == VType.i32.value:
                value, = struct.unpack_from('>i', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 4
            elif vtype == VType.u32.value:
                value, = struct.unpack_from('>I', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 4
            elif vtype == VType.i64.value:
                value, = struct.unpack_from('>l', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 8
            elif vtype == VType.u64.value:
                value, = struct.unpack_from('>L', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 8
            elif vtype == VType.f32.value:
                value, = struct.unpack_from('>f', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 4
            elif vtype == VType.f64.value:
                value, = struct.unpack_from('>d', buffer, off)
                _add_value(c, VType(vtype), key, value)
                off += 8
            elif vtype == VType.str.value:
                vlen, = struct.unpack_from('>H', buffer, off)
                off += 2
                value, = struct.unpack_from('>'+str(vlen)+'s', buffer, off)
                _add_value(c, VType(vtype), key, value.decode('utf-8'))
                off += vlen
            elif vtype == VType.list.value:
                bcount, ecount = struct.unpack_from('>II', buffer, off)
                value = VariantList()
                _add_value(c, VType(vtype), key, value)
                parent_stack.append((value, Variant(VType.u32, ecount)))
                off += 8
            elif vtype == VType.map.value:
                bcount, ecount = struct.unpack_from('>II', buffer, off)
                value = VariantMap()
                _add_value(c, VType(vtype), key, value)
                parent_stack.append((value, Variant(VType.u32, ecount)))
                off += 8
            # check error
            if value is None:
                print('ERROR: no matching type: 0x%02x at offset 0x%04x.' % (vtype, off))
                return
            else:
                ec.value -= 1
                while len(parent_stack) > 0:
                    ec = parent_stack[-1][1]
                    if ec.value == 0:
                        parent_stack.pop()
                    else:
                        break
        assert off == (offset + buff_count + 4)
        return off

    @classmethod
    def _decode_container_lite(cls, container, buffer, offset):  # only decode AckMsgHead
        def _add_value(_c, t, k, v, count):
            if count.value > 0:
                _c.set_value(t, k, v)
                count.value -= 1

        off = offset
        buff_count, elem_count = struct.unpack_from('>II', buffer, off)
        off += 8
        head = VariantMap()
        head_count = Variant(VType.i32, 0)
        exp_off = offset + buff_count + 4
        while off < exp_off:
            # key
            klen, = struct.unpack_from('>B', buffer, off)
            off += 1
            kbytes, = struct.unpack_from('>'+str(klen)+'s', buffer, off)
            key = kbytes.decode('utf-8')
            off += klen
            # value
            vtype, = struct.unpack_from('>B', buffer, off)
            off += 1
            if vtype == VType.bool.value:
                value, = struct.unpack_from('>?', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 1
            elif vtype == VType.i8.value:
                value, = struct.unpack_from('>b', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 1
            elif vtype == VType.u8.value:
                value, = struct.unpack_from('>B', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 1
            elif vtype == VType.i16.value:
                value, = struct.unpack_from('>h', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 2
            elif vtype == VType.u16.value:
                value, = struct.unpack_from('>H', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 2
            elif vtype == VType.i32.value:
                value, = struct.unpack_from('>i', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 4
            elif vtype == VType.u32.value:
                value, = struct.unpack_from('>I', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 4
            elif vtype == VType.i64.value:
                value, = struct.unpack_from('>l', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 8
            elif vtype == VType.u64.value:
                value, = struct.unpack_from('>L', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 8
            elif vtype == VType.f32.value:
                value, = struct.unpack_from('>f', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 4
            elif vtype == VType.f64.value:
                value, = struct.unpack_from('>d', buffer, off)
                _add_value(head, VType(vtype), key, value, head_count)
                off += 8
            elif vtype == VType.str.value:
                vlen, = struct.unpack_from('>H', buffer, off)
                off += 2
                value, = struct.unpack_from('>'+str(vlen)+'s', buffer, off)
                _add_value(head, VType(vtype), key, value.decode('utf-8'), head_count)
                off += vlen
            elif vtype == VType.list.value:
                bcount, ecount = struct.unpack_from('>II', buffer, off)
                off += 4
                value = VariantList()
                off += bcount
            elif vtype == VType.map.value:
                bcount, ecount = struct.unpack_from('>II', buffer, off)
                off += 4
                value = VariantMap()
                if key != 'AckMsgHead':
                    off += bcount
                else:
                    off += 4
                    container.set_value(VType.map, key, head)
                    head_count.value = ecount
            # check error
            if value is None:
                print('ERROR: no matching type: 0x%02x at offset 0x%04x.' % (vtype, off))
                return

        assert off == exp_off
        return off

    @staticmethod
    def _debug_buffer(buff, count=None):
        out = []
        line = 0
        if not count:
            count = len(buff)
        for i in range(count):
            out.append('%02x' % (buff[i]))
            if (i+1) % 16 == 0:
                out.insert(0, '%04x  ' % line)
                print(' '.join(out))
                out.clear()
                line += 16
        if len(out) != 0:
            out.insert(0, '%04x  ' % line)
            print(' '.join(out))


if __name__ == '__main__':

    def test_encode():
        # encode
        print('----- encode -----')
        buff = array.array('B', b'\0' * Message.MAX_BUFFER_SIZE)
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
        offset = msg.encode(buff)
        Message._debug_buffer(buff, offset)
        # decode
        print()
        print('----- decode -----')
        msg2 = Message()
        offset = msg2.decode(buff)
        print('offset=0x%02x' % offset)
        print(msg2)

    test_encode()
