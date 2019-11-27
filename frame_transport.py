import smipc
import ctypes
import numpy as np


class FrameTransport(smipc.Channel):
    def send_frame(self, frame):
        if not isinstance(frame, np.ndarray):
            raise Exception('Invalid param, frame must be instance of np.ndarray.')
        if not len(frame.shape) is 2:
            raise Exception('Invalid param, frame must be 2 dimensional np.ndarray.')
        s = frame.shape
        # write shape
        buf = bytearray(8)
        self.int_bytes(s[0], buf, 0, 4)
        self.int_bytes(s[1], buf, 4, 4)
        self.write(buf)
        # write data
        data = frame.reshape(s[0] * s[1]).tostring()
        self.write(data)

    def __exit__(self, exc_type, exc_val, exc_tb):
        # send stop signal: (0, 0)
        self.write(bytearray(8))
        super().__exit__(exc_type, exc_val, exc_tb)

    def read_frame(self):
        # read shape
        buf = ctypes.c_buffer(8)
        self.read(buf, 8, True)
        s = (self.bytes_int(buf, 0, 4), self.bytes_int(buf, 4, 4))
        # read data
        sz = s[0] * s[1]
        buf = ctypes.create_string_buffer(sz)
        n = self.read(buf, sz, True)
        if n != sz:
            raise Exception('%d bytes expected, only read %d.' % (sz, n))
        frame = np.frombuffer(buf, dtype=np.uint8).reshape(s[0], s[1])
        return frame

    @staticmethod
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

    @staticmethod
    def bytes_int(buf, start_pos, sz):
        v = 0
        for i in range(sz):
            v += (ord(buf[start_pos]) << (i * 8))
            start_pos += 1
        return v
