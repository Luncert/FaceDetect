from event_server import EventServer, on_event
from frame_transport import FrameTransport
from threading import Thread, Event
import cv2
import smipc
import json
import time

E_HEALTH = 'service/health'
E_TRANSPORT_START = 'transport/start'
E_TRANSPORT_STOP = 'transport/stop'
E_SERVICE_STOP = 'service/stop'

t_thread = None
t_event = None


def transport(smipc_conf):
    global t_event
    source = cv2.VideoCapture('demo.mp4')
    with FrameTransport(smipc_conf['cid'], smipc.CHAN_W, smipc_conf['chanSz']) as ft:
        try:
            while not t_event.isSet() and source.isOpened():
                # TODO: source.isOpened() 好像不起作用，会抛出异常
                _, frame = source.read()
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2BGRA)
                time.sleep(0.025)
                ft.send_frame(frame)
        finally:
            source.release()


# @on_event(E_HEALTH)
# def health(msg):
#     return 'ok'


@on_event(E_TRANSPORT_START)
def start_transport(msg):
    global t_event
    global t_thread
    smipc_conf = json.loads(msg)
    if t_thread is None:
        t_event = Event()
        t_thread = Thread(target=transport, args=(smipc_conf,))
        t_thread.setDaemon(True)
        t_thread.start()
    else:
        print('[ERROR] Transport is already active.')


@on_event(E_TRANSPORT_STOP)
def stop_transport(msg):
    global t_thread
    global t_event
    if t_thread:
        t_event.set()
    else:
        print('[ERROR] Transport is not active.')


if __name__ == '__main__':
    smipc.init_library(smipc.LOG_BASIC)
    EventServer('localhost', 8901).start(E_SERVICE_STOP)
    smipc.clean_library()

