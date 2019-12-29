from keras.models import model_from_json
import cv2
from PIL import ImageFont, Image, ImageDraw
import numpy as np

emotion_list = [u'愤怒', u'恐惧', u'高兴', u'伤心', u'惊喜', u'平静']
# load emotion model
weight = './data/model.h5'
prototxt = './data/model.json'
json_file = open(prototxt, 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)
# load weights into new model
model.load_weights(weight)

# load classifier
face_cascade = cv2.CascadeClassifier('./data/haarcascade_frontalface_default.xml')
fontStyle = ImageFont.truetype("simsun.ttc", 20, encoding="utf-8")


def process(frame):
    global emotion_list
    global model
    global face_cascade
    face_rects = face_cascade.detectMultiScale(frame, scaleFactor=1.2, minNeighbors=3, minSize=(50, 50))
    if len(face_rects) > 0:
        for (x, y, w, h) in face_rects:
            # predict emotion
            face = frame[y:y + h, x:x + w]
            face_gray = cv2.cvtColor(face, cv2.COLOR_BGR2GRAY)
            resized_img = cv2.resize(face_gray, (48, 48), interpolation=cv2.INTER_AREA).reshape(1, 1, 48, 48)
            list_of_list = model.predict(resized_img, batch_size=1, verbose=1)
            res = [prob for lst in list_of_list for prob in lst]
            idx = res.index(max(res))
            # TODO: 根据置信度设置阈值
            emotion = emotion_list[idx]
            # draw rectangle
            cv2.rectangle(frame, (x, y), (x + h, y + w), (255, 255, 255), 2)
            img = Image.fromarray(frame)
            draw = ImageDraw.Draw(img)
            draw.text((x + h, y + w), emotion, (255, 255, 255), font=fontStyle)
            # cv2.putText(frame, emotion.encode('utf-8'), (x + h, y + w), fontStyle, 1.2, (255, 255, 255), 2)
            frame = cv2.cvtColor(np.array(img), cv2.COLOR_RGB2BGR)
    return frame
