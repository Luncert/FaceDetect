# Server

## API

### User

> 没有注册，学生数据和老师数据都应该是导入的

1.(学生/教师)登录

POST /user/signIn

### Teacher

1.获取请假信息

GET /user/teacher/leaveApplications

2.处理假条

POST /user/teacher/{leaveApplicationID}

Body:批准 or 不批准，绑定到某次签到记录上

### Course

1.创建课程

POST /user/teacher/course

BODY: course info

2.获取课程信息

GET /user/teacher/courses

3.修改课程信息

PUT /user/teacher/{courseID}

BODY: course info

4.为课程添加学生

POST /user/teacher/{courseID}/{studentID}

5.从课程中移除学生

DELETE /user/teacher/{courseID}/{studentID}

6.删除课程

DELETE /user/teacher/{courseID}

### SignIn

1.开始签到

POST /user/teacher/{courseID}/signIn/start

Return: signInID

PS: 如果有学生没有脸部数据，使用额外信息通知前台

2.标记某个学生已签到（可以由程序识别人脸调用，也可以有老师手动调用）

POST /user/teacher/{courseID}/{signInID}/{studentID}?beLate=true

3.结束签到

POST /user/teacher/{courseID}/{signInID}/stop

### SignIn Record

1.获取课程签到过的时间（获取签到开始时间、结束时间、签到统计）集合

GET /user/teacher/{courseID}/signInRecords

2.获取某一次课程的签到记录

GET /user/teacher/{courseID}/{signInID}

3.删除某次签到记录

DELETE /user/teacher/{courseID}/{signInID}

### Student

1.获取个人信息

GET /user/student/profile

2.录入人脸数据

POST /user/student/faceData

Body: 人脸模型数据

3.获取学生的课程

GET /user/student/courses

4.获取学生某门课签到记录

GET /user/student/{courseID}/signInRecords

5.请假

POST /user/student/leaveApplication

Body:请假时间、附件

6.获取提交的请假信息

GET /user/student/leaveApplications
