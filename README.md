# android-camera-streamer
The final project of the 2020 Spring Semester HCIT (Human-Computer Interaction Technology) class, implementing real-time camera streaming in Android devices with third-party h264 encoder and RTSP streaming dependencies. There is a lack of project refactoring, containing other unrelated code like gesture manipulations and UI controls. 

# Backend Decoder
```python
import cv2
streaming_url = 'rtsp://{your ip}:{your port}/ch0'
cap = cv2.VideoCapture(streaming_url, cv2.CAP_FFMPEG)
while cap.isOpened():
  ret, frame = cap.read()
  # process frame here
```
