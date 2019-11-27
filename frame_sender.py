import cv2
import smipc
from frame_transport import FrameTransport
import json
import time

# smipc.init_library()
# t = FrameTransport("frame-transport", smipc.CHAN_W, 1920 * 1080 * 3)
# t.open()
# source = cv2.VideoCapture("demo.mp4")
# try:
#     while source.isOpened():
#         ret, f = source.read()
#         gray = cv2.cvtColor(f, cv2.COLOR_BGR2GRAY)
#         t.send_frame(gray)
# except Exception as e:
#     if isinstance(e, KeyboardInterrupt):
#         print("Exit gracefully.")
#     else:
#         raise e
# finally:
#     t.close()
#     source.release()
#     smipc.clean_library()

with open('smipc_conf.json', 'rb') as f:
    conf = f.read().decode('utf8')
conf = json.loads(conf)

smipc.init_library()
source = cv2.VideoCapture('demo.mp4')
with FrameTransport(conf['cid'], smipc.CHAN_W, conf['chanSz']) as ft:
    count = 1
    while count > 0:
        _, f = source.read()
        gray = cv2.cvtColor(f, cv2.COLOR_BGR2GRAY)
        time.sleep(0.010)
        ft.send_frame(gray)
        count -= 1
source.release()
smipc.clean_library()
