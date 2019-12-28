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


def process(frame):
    face_cascade = cv2.CascadeClassifier('./data/haarcascade_frontalface_default.xml')
    face_rects = face_cascade.detectMultiScale(frame, scaleFactor=1.2, minNeighbors=3, minSize=(50, 50))
    if len(face_rects) > 0:
        for (x, y, w, h) in face_rects:
            cv2.rectangle(frame, (x, y), (x + h, y + w), (0, 255, 0), 2)
            # predict emotion
            # face = frame[x:x + w, y:y + h]
            # face_gray = cv2.cvtColor(face, cv2.COLOR_BGR2GRAY)
            # resized_img = cv2.resize(face_gray, (48, 48), interpolation=cv2.INTER_AREA)
            # # cv2.imwrite(str(index)+'.png', resized_img)
            # image = resized_img.reshape(1, 1, 48, 48)
            # list_of_list = model.predict(image, batch_size=1, verbose=1)
            # res = [prob for lst in list_of_list for prob in lst]
            # emotion = ['愤怒', '恐惧', '高兴', '伤心', '惊喜', '平静']
            # idx = res.index(max(res))
            # return emotion[idx], res[idx]
    return frame


def transport(smipc_conf):
    # load basic info from server

    global t_event
    source = cv2.VideoCapture('demo.mp4')
    with FrameTransport(smipc_conf['cid'], smipc.CHAN_W, smipc_conf['chanSz']) as ft:
        try:
            while not t_event.isSet() and source.isOpened():
                # TODO: source.isOpened() 好像不起作用，会抛出异常
                _, frame = source.read()
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

