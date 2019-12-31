import ctypes
import numpy as np
import smipc
from utils.int_byte import *

INT_SZ = 4
MSG_TYPE_SZ = 1
MSG_TYPE_TERMINATE = 0
MSG_TYPE_FRAME = 1
MSG_TYPE_TEXT = 2


class DataTransport(smipc.Channel):
    def send_text(self, text):
        if not isinstance(text, str):
            raise Exception('Invalid param, text must be instance of str.')
        # write message type
        buf = bytearray(MSG_TYPE_SZ)
        buf[0] = MSG_TYPE_TEXT
        self.write(buf)
        # write text size
        buf = bytearray(INT_SZ)
        int_bytes(len(text), buf, 0, INT_SZ)
        self.write(buf)
        # write text
        self.write(text.encode('utf-8'))
        
    def send_frame(self, frame):
        if not isinstance(frame, np.ndarray):
            raise Exception('Invalid param, frame must be instance of np.ndarray.')
        # write message type
        buf = bytearray(MSG_TYPE_SZ)
        buf[0] = MSG_TYPE_FRAME
        self.write(buf)
        # write shape
        s = frame.shape
        img_sz = 1
        shape_sz = len(s)
        buf = bytearray((shape_sz + 1) * INT_SZ)
        int_bytes(shape_sz, buf, 0, INT_SZ)
        for i in range(shape_sz):
            int_bytes(s[i], buf, INT_SZ * (i + 1), INT_SZ)
            img_sz *= s[i]
        self.write(buf)
        # write data
        data = frame.reshape(img_sz).tostring()
        self.write(data)

    def __exit__(self, exc_type, exc_val, exc_tb):
        # send stop signal: 0x00
        if self.mode == smipc.CHAN_W:
            buf = bytearray(MSG_TYPE_SZ)
            buf[0] = MSG_TYPE_TERMINATE
            self.write(buf)
        super().__exit__(exc_type, exc_val, exc_tb)

    def read_message(self):
        # read message type
        buf = ctypes.c_buffer(MSG_TYPE_SZ)
        self.read(buf, MSG_TYPE_SZ, True)
        if buf[0] is MSG_TYPE_FRAME:
            return MSG_TYPE_FRAME, self.read_frame()
        elif buf[0] is MSG_TYPE_TEXT:
            return MSG_TYPE_FRAME, self.read_text()
        else:
            raise Exception('Invalid message type.')
        
    def read_text(self):
        # read text size
        buf = ctypes.c_buffer(INT_SZ)
        self.read(buf, INT_SZ, True)
        text_sz = bytes_int(buf, 0, INT_SZ)
        # read text
        if text_sz == 0:
            return ''
        buf = ctypes.c_buffer(text_sz)
        n = self.read(buf, text_sz, True)
        if n != text_sz:
            raise Exception('%d bytes expected, only read %d.' % (text_sz, n))
        return buf

    def read_frame(self):
        # read shape size
        buf = ctypes.c_buffer(INT_SZ)
        self.read(buf, INT_SZ, True)
        shape_sz = bytes_int(buf, 0, INT_SZ)
        if shape_sz == 0:
            # received stop signal
            return None
        # read shape to calculate image size
        img_sz = 1
        shape = []
        buf = ctypes.c_buffer(shape_sz * INT_SZ)
        self.read(buf, len(buf), True)
        for i in range(shape_sz):
            shape.append(bytes_int(buf, i * INT_SZ, INT_SZ))
            img_sz *= shape[i]
        # read data
        buf = ctypes.create_string_buffer(img_sz)
        n = self.read(buf, img_sz, True)
        if n != img_sz:
            raise Exception('%d bytes expected, only read %d.' % (img_sz, n))
        frame = np.frombuffer(buf, dtype=np.uint8).reshape(*shape)
        return frame


if __name__ == '__main__':
    import sys

    conf = {
        "cid": "frame-transport",
        "chanSz": 10368000
    }

    if sys.argv[1].find('-s') == 0:
        print('Start as sender')

        import cv2
        import smipc
        import time

        count = 200
        smipc.init_library(smipc.LOG_ALL)
        source = cv2.VideoCapture(0)
        with DataTransport(conf['cid'], smipc.CHAN_W, conf['chanSz']) as ft:
            while count > 0:
                _, f = source.read()
                f = cv2.cvtColor(f, cv2.COLOR_BGR2BGRA)
                time.sleep(0.025)
                ft.send_frame(f)
                count -= 1
        source.release()
        smipc.clean_library()
    else:
        print('Start as receiver')

        import cv2
        import smipc

        smipc.init_library(smipc.LOG_BASIC)
        with DataTransport(conf['cid'], smipc.CHAN_R, conf['chanSz']) as ft:
            while True:
                f = ft.read_frame()
                if f is None:
                    break
                cv2.imshow("Frame", f)
                if cv2.waitKey(50) & 0xff == 'q':
                    break
        cv2.destroyAllWindows()
        smipc.clean_library()
