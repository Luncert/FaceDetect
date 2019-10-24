import cv2
import os
import json
import numpy as np
from keras.models import model_from_json

def detect(gray, config='./cascades/haarcascade_frontalface_default.xml'):
    """人脸检测api
    Args:
        gray: ndarray, 待检测的图片 要求为灰度图
        config: str, 级联检测器的配置文件
    Returns:
        faces: List[List[float, float, float, float]]
            len(faces)为人脸的个数，face[0]中的四个元素为人脸的x y w h坐标
            (x, y), (x + w, y + h)为人脸的左上及右下坐标
    """
    # 声明一个变量，该变量为级联分类器CascadeClassifier对象，它负责人脸检测
    face_cascade = cv2.CascadeClassifier(config)
    # 加载文件并将其转为灰度图，因人脸检测需要这样的色彩空间
    
    # detectMultiScale函数检测操作返回人脸矩形数组
    # scaleFactor=1.15 - 人脸检测过程中每次迭代时图像的压缩率
    # minNeighbors=5 - 人脸检测过程中每次迭代时每个人脸矩形保留近邻数目的最小值，
    # 调节这两个参数可以实现人脸的有效识别
    faces = face_cascade.detectMultiScale(gray, 1.15, 5)
    # print(faces.shape) # (16,4)
    # 这里面会出现 16 行 4 列，代表16个矩形
    # 每个矩形为（x,y,w,h)
    #print(faces)
    # 通过依次提取faces变量中的值来找人脸，并在人脸周围绘制蓝色矩形(255,0,0)
    # cv2.rectangle通过坐标绘制矩形（x和y表示左上角，w和h表示人脸矩形的宽度和高度
    # 注意这是在原始图像而不是灰度图上进行绘制
    #for (x, y, w, h) in faces:
    #    img = cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
    return faces


# 建立标签
label_num = [0, 1, 2]
label_name = ["xjp", "dxp", "jzm"]
images = []
labels = []


# 将图像数组和CSV文件加载到人脸识别的算法中
def read_images(path):
    # 定义数据和标签
    # 获取path文件下的文件及文件夹并返回名称列表
    for dir_item in os.listdir(path):
        # 返回path规范化的绝对路径
        path_abs = os.path.abspath(os.path.join(path, dir_item))
        # 判断path_abs是文件还是文件还是文件夹
        try:
            # str.endswith()是判断文件str后缀是否为指定格式
            # 本图像指定为.png格式
            if path_abs.endswith('.png') or path_abs.endswith('.jpg'):
                # print("try:", path_abs)
                # 读取训练数据
                img = cv2.imread(path_abs)
                # 统一输入文件的尺寸大小
                img = cv2.resize(np.asarray(img, dtype=np.uint8))
                # 统一图像文件的元素dtype,并将其加入images中
                images.append(np.asarray(img, dtype=np.uint8))
                # 先将path_abs分割,注意分割线\\,而不是//
                path_piece = path_abs.split('\\')
                if label_name[0] in path_piece:
                    labels.append(label_num[0])
                elif label_name[1] in path_piece:
                    labels.append(label_num[1])
                elif label_name[2] in path_piece:
                    labels.append(label_num[2])
                else:
                    # 没有对应标签则删除训练数据
                    images.pop()
            # 若为文件夹则递归调用，循环读取子子文件内容
            elif os.path.isdir(path_abs):
                read_images(path_abs)
            # 若为其他情况则循环运行
            else:
                continue
        # 当发生异常时则抛出异常信息e
        except Exception as e:
            print("REASON:", e)
    print('labels:', labels)
    print('images:', images)
    return images, labels


def face_model(data_path='./pictures/'):
    """基于Eigenfaces的模型训练
    Args:
        data_path: str, 训练数据保存路径。
    Returns:
        model: EigenFaceRecognizer, 用来识别的分类器。
            先使用人脸检测器检测到人脸，然后把人脸抠出来输入model中
            recohnize_res = model(detect_res)
    """
    # 使用label_num作为全局变量
    # 每当脚本识别出一个ID，就会将相应名称数组中的名字打印到人脸上
    global label_num
    # 获取文件所在文件夹的绝对路径
    # path = os.getcwd()
    # 调用图像读入函数，获取训练数据及标签
    images, labels = read_images()
    # 实例化人脸识别模型
    model = cv2.face.EigenFaceRecognizer_create()
    # 通过图像数组和标签来训练模型
    model.train(np.asarray(images), np.asarray(labels))
    return model

def face_recognize_img(img, model, config='./cascades/haarcascade_frontalface_default.xml'):
    """静态图片人脸识别api，将检测并且抠下来的人脸输入该函数得到识别结果。
    Args:
        img: ndarray, 检测得到的人脸数据，要求为灰度图。
        config: str, 
        model: EigenFaceRecognizer, 用来识别的分类器。
    Returns:
        person: str, 识别的结果是哪个人，为全局变量label_name中的一个值。
        prob: float, 识别结果的置信度
    """
    # 实例化人脸识别级联分类器
    face_cascade = cv2.CascadeClassifier(config)
    roi = cv2.resize(img, (200, 200), interpolation=cv2.INTER_LINEAR)
    # predict()预测函数，返回预测标签和置信度
    params = model.predict(roi)
    person, prob = label_name[params[0]], params[1]
    return person, prob

def face_recognize_video(config='./cascades/haarcascade_frontalface_default.xml'):
    """视频流人脸识别api，读取视频流然后不断通过cv2.imshow()显示识别的结果。
    Args:
        config: str, 人脸分类器的配置文件
    """
    # 调用训练好的模型
    face_model_trained = face_model()
    # 初始化摄像头
    camera = cv2.VideoCapture(0)
    # 实例化人脸识别级联分类器
    face_cascade = cv2.CascadeClassifier(config)
    while (True):
        read, img = camera.read()
        faces = face_cascade.detectMultiScale(img, 1.3, 5)
        for (x, y, w, h) in faces:
            img = cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            roi = gray[x:x + w, y:y + h]
            try:
                roi = cv2.resize(roi, (200, 200), interpolation=cv2.INTER_LINEAR)
                # predict()预测函数，返回预测标签和置信度
                params = face_model_trained.predict(roi)
                print("Label: %s, confidence: %0.2f" % (label_name[params[0]], params[1]))
                cv2.putText(img, label_name[params[0]], (x, y - 20), cv2.FONT_HERSHEY_SIMPLEX, 3, (255, 0, 0), 2)
            except Exception as e:
                print("face_rec_REASON:", e)

        cv2.imshow('camera', img)
        if cv2.waitKey(10) & 0xff == ord('q'):
            break

    cv2.destroyAllWindows()

def emotion_model(weight='model.h5', prototxt='model.json'):
    """加载训练好的表情识别模型
    Args:
        weight: str, 训练好的权重模型的路径
        prototxt: str, 描述网络结构的文件
    Returns:
        model: 训练好的表情识别
    """
    json_file = open(prototxt,'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
    # load weights into new model
    model.load_weights(weight)
    return model

def predict_emotion(face_image_gray, model):
    """将剪切得到的脸部灰度图输入模型进行检测
    Args:
        face_image_gray: ndarray, 脸部图片
    Returns:
        em: str, 识别出来的情绪
        prob: float, 该情绪的置信度
    """
    resized_img = cv2.resize(face_image_gray, (48,48), interpolation = cv2.INTER_AREA)
    # cv2.imwrite(str(index)+'.png', resized_img)
    image = resized_img.reshape(1, 1, 48, 48)
    list_of_list = model.predict(image, batch_size=1, verbose=1)
    res = [prob for lst in list_of_list for prob in lst]
    emotion = ['愤怒', '恐惧', '高兴', '伤心', '惊喜', '平静']
    idx = res.index(max(res))
    return  emotion[idx], res[idx]