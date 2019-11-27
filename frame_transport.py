import smipc
import ctypes
import numpy as np

INT_SZ = 4


class FrameTransport(smipc.Channel):
    def send_frame(self, frame):
        if not isinstance(frame, np.ndarray):
            raise Exception('Invalid param, frame must be instance of np.ndarray.')
        s = frame.shape
        # write shape
        img_sz = 1
        shape_sz = len(s)
        buf = bytearray((shape_sz + 1) * INT_SZ)
        self.int_bytes(shape_sz, buf, 0, INT_SZ)
        for i in range(shape_sz):
            self.int_bytes(s[i], buf, INT_SZ * (i + 1), INT_SZ)
            img_sz *= s[i]
        self.write(buf)
        # write data
        data = frame.reshape(img_sz).tostring()
        self.write(data)

    def __exit__(self, exc_type, exc_val, exc_tb):
        # send stop signal: 0x00
        if self.mode == smipc.CHAN_W:
            self.write(bytearray(INT_SZ))
        super().__exit__(exc_type, exc_val, exc_tb)

    def read_frame(self):
        # read shape size
        buf = ctypes.c_buffer(INT_SZ)
        self.read(buf, INT_SZ, True)
        shape_sz = self.bytes_int(buf, 0, INT_SZ)
        if shape_sz == 0:
            # received stop signal
            return None
        # read shape to calculate image size
        img_sz = 1
        shape = []
        buf = ctypes.c_buffer(shape_sz * INT_SZ)
        self.read(buf, len(buf), True)
        for i in range(shape_sz):
            shape.append(self.bytes_int(buf, i * INT_SZ, INT_SZ))
            img_sz *= shape[i]
        # read data
        buf = ctypes.create_string_buffer(img_sz)
        n = self.read(buf, img_sz, True)
        if n != img_sz:
            raise Exception('%d bytes expected, only read %d.' % (img_sz, n))
        frame = np.frombuffer(buf, dtype=np.uint8).reshape(*shape)
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
