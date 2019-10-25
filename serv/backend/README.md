# Backend

## Issues

### 1.rtmp command ```FCSubscribe``` is not supported by livego-rtmp

rtmp module of livego couldn't recognize ```FCSubscribe```, which will be used by video.js to play rtmp stream. Then by default, rtmp module will mark this connection as a stream publisher, that why our video.js doesn't work.