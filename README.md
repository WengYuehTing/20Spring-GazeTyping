# GazeTyping-client 

Text entry using gaze-based interaction is a vital communication tool for people with motor impairments, it releases users' hand and provides higher accuracy (enough resolution is needed) on the small interface because of the fat finger problem. Given the rapid development of computing power and gaze estimates and typing technology, we recognize that gaze typing technology on an unmodified smartphone as a practical solution to augment text entry capability for motor impaired users.

This project included android implementation with h264 encoder and rtsp streaming, capturing users' face and streaming it in real-time with high resolution. Some simple touchscreen gestures (e.g., one-click, double-click) were also implemented and send to server to augment gaze typing capability.
![concept](app/src/main/res/drawable/concept.png "concept")

# Backend Decoder
Using ffmpeg framework that was defaultly embedded in opencv to decode video stream like common IPCamera:
```
import cv2
streaming_url = 'rtsp://{your ip}:{your port}/ch0'
cap = cv2.VideoCapture(streaming_url, cv2.CAP_FFMPEG)
while cap.isOpened():
  ret, frame = cap.read()
  # process your frame here
```
