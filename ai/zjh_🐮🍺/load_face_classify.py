import cv2
import os
import numpy as np

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


# 基于Eigenfaces的模型训练
def face_model():
    # 使用label_num作为全局变量
    # 每当脚本识别出一个ID，就会将相应名称数组中的名字打印到人脸上
    global label_num
    # 获取文件所在文件夹的绝对路径
    # path = os.getcwd()
    # 调用图像读入函数，获取训练数据及标签
    images, labels = read_images('./pictures/')
    # 实例化人脸识别模型
    model = cv2.face.EigenFaceRecognizer_create()
    # 通过图像数组和标签来训练模型
    model.train(np.asarray(images), np.asarray(labels))
    return model


def face_rec():
    # 调用训练好的模型
    face_model_trained = face_model()
    # 初始化摄像头
    camera = cv2.VideoCapture(0)
    # 实例化人脸识别级联分类器
    face_cascade = cv2.CascadeClassifier('../cascades/haarcascade_frontalface_default.xml')
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


if __name__ == '__main__':
    face_rec()
