import cv2
import smipc
from frame_transport import FrameTransport
import json

with open('smipc_conf.json', 'rb') as f:
    conf = f.read().decode('utf8')
conf = json.loads(conf)

smipc.init_library(smipc.LOG_ALL)
with FrameTransport(conf['cid'], smipc.CHAN_R, conf['chanSz']) as ft:
    count = 200
    while count > 0:
        frame = ft.read_frame()
        count -= 1
        cv2.imshow("Frame", frame)
        if cv2.waitKey(50) & 0xff == 'q':
            break
smipc.clean_library()
cv2.destroyAllWindows()
