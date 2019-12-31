
def int_bytes(v, buf, start_pos, sz):
    if v < 0:
        raise Exception("Invalid int value, must be positive.")
    for i in range(sz):
        t = v & 0xff
        buf[start_pos] = t
        start_pos += 1
        v >>= 8
        if v == 0:
            break


def bytes_int(buf, start_pos, sz):
    v = 0
    for i in range(sz):
        v += (ord(buf[start_pos]) << (i * 8))
        start_pos += 1
    return v
