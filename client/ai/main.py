from event_server import EventServer, on_event
from frame_transport import FrameTransport
from threading import Thread, Event
from face_api import process
import cv2
import smipc
import json
import time
import requests


E_HEALTH = 'service/health'
E_TRANSPORT_START = 'transport/start'
E_TRANSPORT_STOP = 'transport/stop'
E_SERVICE_STOP = 'service/stop'
API_SIGN_IN = 'user/signIn'
API_GET_STUDENTS = '/user/teacher/students'

t_thread = None
t_event = None


def transport(conf):
    global t_event
    source = cv2.VideoCapture('demo.mp4')
    with FrameTransport(conf['cid'], smipc.CHAN_W, conf['chanSz']) as ft:
        # load basic info from server
        session = requests.session()
        rep = session.post(conf['server'] + API_SIGN_IN, data=conf['userInfo'])
        if rep.status_code != 200:
            raise Exception('Auth failed, status code=%d.' % rep.status_code)
        rep = session.get(conf['server'] + API_GET_STUDENTS)
        if rep.status_code != 200:
            raise Exception('Failed to get student list, status code=%d.' % rep.status_code)
        # id name
        student_list = json.loads(rep.content)
        # 识别出名字，告诉前端
        # main process
        try:
            while not t_event.isSet():
                ok, frame = source.read()
                if not ok:
                    break
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2BGRA)
                frame = process(frame)
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

