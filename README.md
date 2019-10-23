# FaceDetect

This version uses web tech to access camera. In frontend, We invoke WebRTC APIs to get camera video stream, then substract a series of frames from it and convert frames to DataUrls, at last, send these DataUrls to server through websocket.

Problems:
- each frame is too big, amlost 2MB (PNG encoded), it's not possible to send at least 30 frames to server in one second.
