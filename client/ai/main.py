from event_server import EventServer, on_event
from data_transport import DataTransport
from threading import Thread, Event
from face_api import face_detect_opencv, face_detect_mtcnn
import cv2
import smipc
import json
import time
import requests


E_HEALTH = 'service/health'
E_TRANSPORT_START = 'transport/start'
E_TRANSPORT_STOP = 'transport/stop'
E_SERVICE_STOP = 'service/stop'
API_SIGN_IN = '/user/signIn'
API_GET_STUDENTS = '/user/teacher/course:{}/students'

# 主线程设置该事件来通知子线程退出
t_event_1 = None
# 子线程设置该事件来通知主线程它已退出
t_event_2 = None


def auth(server_host, user_info):
    session = requests.session()
    rep = session.post(server_host + API_SIGN_IN, data=user_info)
    if rep.status_code != 200:
        raise Exception('Auth failed, status code=%d.' % rep.status_code)
    return session


def get_students(session, server_host, course_id):
    # get course's students
    rep = session.get(server_host + API_GET_STUDENTS.format(course_id))
    if rep.status_code != 200:
        raise Exception('Failed to get student list, status code=%d.' % rep.status_code)
    data = rep.content.decode('utf-8')
    student_map = {}
    for student in json.loads(data):
        student_map[student['id']] = student
    return student_map


def transport(conf):
    global t_event_1
    global t_event_2

    frame_size = conf['frameSize']
    frame_width, frame_height = frame_size['width'], frame_size['height']

    source = cv2.VideoCapture(0)
    with DataTransport(conf['cid'], smipc.CHAN_W, conf['chanSz']) as dt:
        # auth with teacher's account
        server_host = conf['serverHost']
        session = auth(server_host, conf['userInfo'])
        students = get_students(session, server_host, conf['courseID'])
        processed_students = set()

        detect_algorithm = face_detect_opencv if conf['algorithm'] == 'opencv' else face_detect_mtcnn
        # detect_algorithm = face_detect_opencv

        # main process
        try:
            while not t_event_1.isSet():
                ok, frame = source.read()
                if not ok:
                    break
                # adjust frame
                frame = cv2.resize(frame, (frame_width, frame_height), interpolation=cv2.INTER_AREA)
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2BGRA)
                # detect
                frame, detected_ids = detect_algorithm(frame)
                # send frame
                dt.send_frame(frame)
                # do sign in
                detected_students = []
                if len(processed_students) < 2:
                    for sid in detected_ids:
                        if sid in students and sid not in processed_students:
                            detected_students.append(students[sid])
                            processed_students.add(sid)
                    if len(detected_students) > 0:
                        dt.send_text(json.dumps(detected_students))
                # time.sleep(0.025)
            t_event_2.set()
        finally:
            source.release()


@on_event(E_TRANSPORT_START)
def start_transport(msg):
    conf = json.loads(msg)

    global t_event_1, t_event_2
    if t_event_1 is None and t_event_2 is None:
        t_event_1, t_event_2 = Event(), Event()
    elif t_event_1.isSet() and t_event_2.isSet():
        t_event_1.clear()
        t_event_2.clear()
    else:
        print('[ERROR] Transport is already active.')
        return

    t_thread = Thread(target=transport, args=(conf,))
    t_thread.setDaemon(True)
    t_thread.start()


@on_event(E_TRANSPORT_STOP)
def stop_transport(msg):
    global t_event_1, t_event_2
    if t_event_1 is not None:
        t_event_1.set()
    else:
        print('[ERROR] Transport is not active.')


if __name__ == '__main__':
    smipc.init_library(smipc.LOG_BASIC)
    EventServer('localhost', 8901).start(E_SERVICE_STOP)
    smipc.clean_library()


