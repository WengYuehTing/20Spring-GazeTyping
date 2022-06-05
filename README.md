# android-camera-streamer
The final project of the 2020 Spring Semester HCIT course, implementing real-time camera streaming to Android devices with third-party h264 encoder and RTSP streaming dependencies. The project is a lack of refactoring, containing some unnecessary code like gesture manipulations and UI controls etc.

# Backend Decoder
```python
import cv2
streaming_url = 'rtsp://{your ip}:{your port}/ch0'
cap = cv2.VideoCapture(streaming_url, cv2.CAP_FFMPEG)
while cap.isOpened():
  ret, frame = cap.read()
  # process frame here
```
