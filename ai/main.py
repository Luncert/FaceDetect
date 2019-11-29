from event_server import EventServer, on_event
from frame_transport import FrameTransport
from threading import Thread, Event
import cv2
import smipc
import time
import json

E_TRANSPORT_START = 'transport/start'
E_TRANSPORT_STOP = 'transport/stop'
E_SERVICE_STOP = 'service/stop'

transport_thread = None
transport_event = None
with open('smipc_conf.json', 'rb') as f:
    smipc_conf = f.read().decode('utf8')
smipc_conf = json.loads(smipc_conf)


def transport():
    global smipc_conf
    global transport_event
    source = cv2.VideoCapture('demo.mp4')
    with FrameTransport(smipc_conf['cid'], smipc.CHAN_W, smipc_conf['chanSz']) as ft:
        while not transport_event.isSet() or source.isOpened():
            _, frame = source.read()
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2BGRA)
            # time.sleep(0.025)
            ft.send_frame(frame)
    source.release()


@on_event(E_TRANSPORT_START)
def start_transport(msg):
    global transport_event
    global transport_thread
    if transport_thread is None:
        transport_event = Event()
        transport_thread = Thread(target=transport)
        transport_thread.setDaemon(True)
        transport_thread.start()
    else:
        print('[ERROR] Transport is already active.')


@on_event(E_TRANSPORT_STOP)
def stop_transport(msg):
    global transport_thread
    global transport_event
    if transport_thread:
        transport_event.set()
    else:
        print('[ERROR] Transport is not active.')


if __name__ == '__main__':
    smipc.init_library(smipc.LOG_BASIC)
    EventServer('localhost', 8901).start(E_SERVICE_STOP)
    smipc.clean_library()

