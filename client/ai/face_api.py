from keras.models import model_from_json
import cv2
from PIL import ImageFont, Image, ImageDraw
import os
import numpy as np
import predict
import face_recognition
import tensorflow as tf

# load emotion model
graph = tf.get_default_graph()
emotion_list = [u'愤怒', u'恐惧', u'高兴', u'伤心', u'惊喜', u'平静']
weight = './data/model.h5'
prototxt = './data/model.json'
json_file = open(prototxt, 'r')
loaded_model_json = json_file.read()
json_file.close()
emotion_model = model_from_json(loaded_model_json)
emotion_model.load_weights(weight)

# load classifier
face_cascade = cv2.CascadeClassifier('./data/haarcascade_frontalface_default.xml')
fontStyle = ImageFont.truetype("simsun.ttc", 20, encoding="utf-8")

# mtcnn config
mtcnn_config = {
    'model_path': 'data/models/20180408-102900',
    'dataset_path': 'data/dataset/emb/faceEmbedding.npy',
    'filename': 'data/dataset/emb/name.txt'
}


def adjust_face_box(raw_shape, face_pos):
    if face_pos is None:
        return None

    x, y, w, h = face_pos

    if x >= raw_shape[0]:
        return None
    if y >= raw_shape[1]:
        return None
    m = x + w
    n = y + h
    x = max(x, 0)
    y = max(y, 0)
    m = min(raw_shape[0], m)
    n = min(raw_shape[1], n)
    return x, y, m, n


def render_frame(frame, face_pos):
    global graph
    global emotion_list
    global emotion_model
    if face_pos is not None:
        x, y, m, n = face_pos
        # draw rectangle
        cv2.rectangle(frame, (x, y), (m, n), (255, 255, 255), 2)
        # predict emotion
        face = frame[x:m, y:n]
        face_gray = cv2.cvtColor(face, cv2.COLOR_BGR2GRAY)
        resized_img = cv2.resize(face_gray, (48, 48), interpolation=cv2.INTER_AREA).reshape(1, 1, 48, 48)
        # predict
        with graph.as_default():
            list_of_list = emotion_model.predict(resized_img, batch_size=1, verbose=1)
            res = [prob for lst in list_of_list for prob in lst]
            idx = res.index(max(res))
            # TODO: 根据置信度设置阈值
            emotion = emotion_list[idx]
            # draw emotion text
            img = Image.fromarray(frame)
            draw = ImageDraw.Draw(img)
            draw.text((m, n), emotion, (255, 255, 255), font=fontStyle)
            frame = cv2.cvtColor(np.array(img), cv2.COLOR_BGR2BGRA)
    return frame


def load_face_model(path, sz=None):
    c = 0
    x, y = [], []
    names = []
    for dirname, dir_names, filenames in os.walk(path):
        #        print(dirname)#dirname是文件夹名字data,显示下面还有文件夹zx...
        #        print(dir_names)#显示下面的文件夹名字‘zx’...,并且下面没有文件夹了
        #        print(filenames)#filenames是每个图片的名字

        for subdir_name in dir_names:
            # print(subdir_name) #文件名，又或者说是类名
            names.append(subdir_name)
            subject_path = os.path.join(dirname, subdir_name)
            #            print(subject_path)#subject_path是吧data和子文件夹名字合在一起成data\zx这样了
            #            print(os.listdir(subject_path))#图片名组成的列表
            for filename in os.listdir(subject_path):
                #                print(filenames)

                filepath = os.path.join(subject_path, filename)
                #                print(filepath)#把文件名都连起来，data\zx\0.jpg
                im = cv2.imread(filepath, cv2.IMREAD_GRAYSCALE)

                # 读取的图片如果不是（200,200）改变大小
                if sz is not None:
                    im = cv2.resize(im, (200, 200))

                x.append(np.asarray(im, dtype=np.uint8))
                y.append(c)
            c = c + 1

    model = cv2.face.EigenFaceRecognizer_create()
    model.train(np.asarray(x), np.asarray(y))
    return model, names


# prepare opencv
face_model, face_names = load_face_model('./data/pictures')


def face_detect_opencv(frame):
    global face_cascade
    global face_model

    detected_name_list = []
    gray = cv2.cvtColor(frame, cv2.COLOR_RGB2GRAY)
    face_list = face_cascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=3, minSize=(50, 50))
    for face_pos in face_list:
        face_pos = adjust_face_box(frame.shape, face_pos)
        if face_pos is not None:
            x, y, m, n = face_pos
            face = gray[x:m, y:n]
            face = cv2.resize(face, (200, 200))
            params = face_model.predict(face)
            person_name = face_names[int(params[0])]
            detected_name_list.append(person_name)

            frame = render_frame(frame, face_pos)
    return frame, detected_name_list


# 加载数据库的数据
dataset_emb, names_list = predict.load_dataset(mtcnn_config['dataset_path'], mtcnn_config['filename'])
# 初始化mtcnn人脸检测
face_detect = face_recognition.Facedetection()
# 初始化facenet
face_net = face_recognition.facenetEmbedding(mtcnn_config['model_path'])


def face_detect_mtcnn(frame):
    global dataset_emb
    global names_list
    global face_detect
    global face_net

    gray = cv2.cvtColor(frame, cv2.COLOR_RGB2GRAY)
    pred_name, face_pos = predict.face_recognition_image_nn(
        dataset_emb, names_list, face_detect, face_net, gray)

    if pred_name is None:
        pred_name = []

    face_pos = adjust_face_box(frame.shape, face_pos)
    if face_pos is not None:
        frame = render_frame(frame, face_pos)
    return frame, pred_name
