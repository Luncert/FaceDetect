from face_api import face_detect_opencv, face_detect_mtcnn
import cv2


camera = cv2.VideoCapture(0)

while camera.isOpened():
    ret, frame = camera.read()
    if not ret:
        print('????')
        break
    frame, detected_name_list = face_detect_mtcnn(frame)
    print(detected_name_list)
    cv2.imshow('img', frame)
    if cv2.waitKey(25) & 0xff == ord('q'):
        break

camera.release()
cv2.destroyAllWindows()
