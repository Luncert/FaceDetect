import cv2

filename = './pic.jpg'


def detect(filename):
    # 声明一个变量，该变量为级联分类器CascadeClassifier对象，它负责人脸检测
    face_cascade = cv2.CascadeClassifier('../cascades/haarcascade_frontalface_default.xml')
    # 加载文件并将其转为灰度图，因人脸检测需要这样的色彩空间
    img = cv2.imread(filename)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    # detectMultiScale函数检测操作返回人脸矩形数组
    # scaleFactor=1.15 - 人脸检测过程中每次迭代时图像的压缩率
    # minNeighbors=5 - 人脸检测过程中每次迭代时每个人脸矩形保留近邻数目的最小值，
    # 调节这两个参数可以实现人脸的有效识别
    faces = face_cascade.detectMultiScale(gray, 1.15, 5)
    # print(faces.shape) # (16,4)
    # 这里面会出现 16 行 4 列，代表16个矩形
    # 每个矩形为（x,y,w,h)
    print(faces)
    # 通过依次提取faces变量中的值来找人脸，并在人脸周围绘制蓝色矩形(255,0,0)
    # cv2.rectangle通过坐标绘制矩形（x和y表示左上角，w和h表示人脸矩形的宽度和高度
    # 注意这是在原始图像而不是灰度图上进行绘制
    for (x, y, w, h) in faces:
        img = cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
    # 最后创建nameWindow的实例，并显示处理后的图像。
    # cv2.namedWindow('Detected')
    # cv2.imshow('Detected', img)
    cv2.imwrite('./Detected.jpg', img)
    # 加入waitKey函数，这样在按下任意键时才可关闭窗口
    # cv2.waitKey(0)

