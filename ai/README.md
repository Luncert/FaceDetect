# 人脸检测+人脸识别+表情识别

## 需要的环境
 - python3
 - opencv-python
 - tensorflow
 - keras，依赖tensorflow

## 人脸检测
`face_api.py`中的`detect()`
```py
def detect(filename, config='./cascades/haarcascade_frontalface_default.xml'):
    """人脸检测api
    Args:
        filename: str, 待检测的图片
        config: str, 级联检测器的配置文件
    Returns:
        faces: List[List[float, float, float, float]]
            len(faces)为人脸的个数，face[0]中的四个元素为人脸的x y w h坐标
            (x, y), (x + w, y + h)为人脸的左上及右下坐标
    """
```

## 人脸识别
 - 训练并得到人脸识别模型: `face_api.py`中的`face_model(data_path)`
 - 识别视频流中的人脸: `face_api.py`中的`face_recognize_video(config)`
 - 识别检测得到的人脸: `face_api.py`中的`faceface_recognize_img(img, config, model)`

```py
def face_model(data_path='./pictures/'):
    """基于Eigenfaces的模型训练
    Args:
        data_path: str, 训练数据保存路径。
    Returns:
        model: EigenFaceRecognizer, 用来识别的分类器。
            先使用人脸检测器检测到人脸，然后把人脸抠出来输入model中
            recohnize_res = model(detect_res)
    """

def face_recognize_video(config='./cascades/haarcascade_frontalface_default.xml'):
    """视频流人脸识别api，读取视频流然后不断通过cv2.imshow()显示识别的结果。
    Args:
        config: str, 人脸分类器的配置文件
    """

def face_recognize_img(img, model, config='./cascades/haarcascade_frontalface_default.xml'):
    """静态图片人脸识别api，将检测并且抠下来的人脸输入该函数得到识别结果。
    Args:
        img: ndarray, 检测得到的人脸数据，要求为灰度图。
        config: str, 人脸分类器的配置文件
        model: EigenFaceRecognizer, 用来识别的分类器。
    Returns:
        person: str, 识别的结果是哪个人，为全局变量label_name中的一个值。
        prob: float, 识别结果的置信度
    """
```

## 表情识别
 - 加载训练好的表情识别网络: `face_api.py`中的`emotion_model(weight, prototxt)`
 - 进行表情识别：`face_api.py`中的`predict_emotion(face_image_gray, model)`

```py
def emotion_model(weight='model.h5', prototxt='model.json'):
    """加载训练好的表情识别模型
    Args:
        weight: str, 训练好的权重模型的路径
        prototxt: str, 描述网络结构的文件
    Returns:
        model: 训练好的表情识别
    """

def predict_emotion(face_image_gray, model):
    """将剪切得到的脸部灰度图输入模型进行检测
    Args:
        face_image_gray: ndarray, 脸部图片
    Returns:
        em: str, 识别出来的情绪
        prob: float, 该情绪的置信度
    """
```

## 使用例子
```py
img = cv2.imread(filename)
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
face_list = detect(gray)

det_model = face_model()
em_model = emotion_model()

for x, y, w, h in face_list:
    roi = gray[x:x + w, y:y + h]
    #roi = cv2.resize(roi, (200, 200), interpolation=cv2.INTER_LINEAR)
    person, p_prob = face_recognize_img(det_model, roi)
    emotion, e_prob = predict_emotion(rot, em_model)
    print('识别出来了:{} 他现在的心情:{}'.format(person, emotion))
```