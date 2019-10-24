import cv2
import time

# 图像名编号
count = 1
# 记录循环次数
i = 1
# 控制图片保存频率，间隔10次保存一次图像
FREQUENCY = 5
# 拟制备样本数量
MAX = 20


# 图片识别方法封装
def discern(img):
    global count
    global i
    global FREQUENCY
    # 将图img转为灰度图，因opencv中的人脸检测是基于灰度的色彩空间
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    # 实例化级联分类器
    face_cascade = cv2.CascadeClassifier('../cascades/haarcascade_frontalface_default.xml')
    eye_cascade = cv2.CascadeClassifier('../cascades/haarcascade_eye.xml')
    # 调用图像中的对象，并返回矢量矩形
    faceRects = face_cascade.detectMultiScale(gray, scaleFactor=1.3, minNeighbors=5)
    # 只要视频存在，就输出人脸识别框
    if len(faceRects):
        # 框出人脸
        for (x, y, w, h) in faceRects:
            # 在原彩色图img上绘制识别的人脸矩形
            cv2.rectangle(img, (x, y), (x + h, y + w), (255, 0, 0), 2)
            # 裁剪灰度帧的区域，将其调整为200*200像素
            f = cv2.resize(gray[y:y + h, x:x + w], (200, 200))

            # 控制输出频率，如果没有控制，则输出的非常快
            i += 1
            if i % FREQUENCY == 0:
                # 将裁剪区域保存到指定文件夹中，文件后缀名为.pgm
                cv2.imwrite('./cuinan/%s.pgm' % str(count), f)
                cv2.imwrite('./cuinan/%s.pgm' % str(count), f)
                count += 1

        # 将绘制有矩形框的img显示输出
        # cv2.imshow("camera", img)


# 打开一个VideoCapture目标（初始化摄像头）
# 参数为摄像头ID，0表获取第一个摄像头
cap = cv2.VideoCapture(0)
while (True):
    # 将视频逐帧显示
    ret, img = cap.read()
    # 调用discern函数
    discern(img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    # 如果大于所需样本数量，则推出循环程序
    if count > MAX:
        break

# 释放摄像头
cap.release()
# 释放窗口资源
cv2.destroyAllWindows()
