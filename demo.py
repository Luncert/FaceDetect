import cv2

def processFrame(frame):
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    face_cascade = cv2.CascadeClassifier('./ai/cascades/haarcascade_frontalface_default.xml')
    faceRects = face_cascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=3, minSize=(50, 50))

    if len(faceRects):
        # 框出人脸，合并写
        for (x, y, w, h) in faceRects:
            cv2.rectangle(frame, (x, y), (x + h, y + w), (0, 255, 0), 2)
    cv2.imshow("image", frame)
    

source = cv2.VideoCapture("demo.mp4")
while source.isOpened():
    ret, frame = source.read()
    processFrame(frame)
    if cv2.waitKey(25) & 0xFF == ord('q'):
        break

source.release()
cv2.destroyAllWindows()