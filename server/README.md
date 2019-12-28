# Server

## API

### User

> 没有注册，学生数据和老师数据都应该是导入的

#### 1.(学生/教师)登录

POST ```/user/signIn```

ContentType: Application Form Url Encoded

##### Body

```account=xxxxx&password=xxxxx```

##### Response

```json
{
    "identified": true,
    "role": "Teacher" // or "Student"
}
```

### Teacher

#### 1.获取请假信息

GET ```/user/teacher/leaveSlips```

##### Response

```json
[
    {
        "id": <Long>,
        "courseID": <Long>,
        "courseName": <String>,
        "studentID": <String>,
        "studentName": <String>,
        "state": <String>,
        "createTime": <Long>,
        "date": <String>,
        "content": <String>,
        "attachmentUrl": <String>
    }
]
```

#### 2.处理假条

PUT ```/user/teacher/leaveSlip:{leaveSlipID}```

##### Body

```json
{
	"approved": <Boolean>,
	"signInID": <Long>,
	"comment": <String>
}
```

### Course

#### 1.创建课程

POST ```/user/teacher/course```

##### Body

```json
{
    "name": <String>,
    "studentIDList": <String List>
}
```

#### 2.获取课程信息

GET ```/user/teacher/courses```

##### Response

```json
[
    {
        "id": <Long>,
        "name": <String>
    }
]
```

#### 3.获取课程学生

GET ```user/teacher/course:{courseID}/students```

##### Response

```json
[
	{
		"id": <String>,
        "name": <String>,
        "hasFaceData": <Boolean>
	}
]
```

#### 4.修改课程信息

PUT ```/user/teacher/course:{courseID}```

##### Body

```json
{
    "name": <String>
}
```

#### 5.为课程添加学生

PUT ```/user/teacher/course:{courseID}/student:{studentID}```

#### 5.从课程中移除学生

DELETE ```/user/teacher/course:{courseID}/student:{studentID}```

#### 6.删除课程

DELETE ```/user/teacher/course:{courseID}```

### SignIn

#### 1.开始签到

POST ```/user/teacher/course:{courseID}/signIn/start```

##### Response

返回SignInID

>  PS: 如果有学生没有脸部数据，使用额外信息通知前台

#### 2.标记某个学生已签到（可以由程序识别人脸调用，也可以有老师手动调用）

POST ```/user/teacher/course:{courseID}/signIn:{signInID}/student:{studentID}?beLate={T/F}```

#### 3.结束签到

POST ```/user/teacher/course:{courseID}/signIn:{signInID}/stop```

### SignIn Record

#### 1.获取课程签到过的时间（获取签到开始时间、结束时间、签到统计）集合

GET ```/user/teacher/course:{courseID}/signInList```

##### Response

```json
[
    {
        "id": <Long>,
        "startTime": <Long>,
        "endTime": <Long>
    }
]
```

#### 2.获取某一次课程的签到记录

GET ```/user/teacher/course:{courseID}/signIn:{signInID}```

##### Response

```json
[
	{
        "id": <Long>,
        "createTime": <Long>,
        "student": <Student>,
        "beLate": <Boolean>,
        "leaveSlip": <LeaveSlip>
    }
]
```

#### 3.删除某个学生的签到记录

DELETE ```/user/teacher/course:{courseID}/signIn:{signInID}/signInRecord:{signInRecordID}```

#### 4.删除某次签到

DELETE ```/user/teacher/course:{courseID}/signIn:{signInID}```

### Student

#### 1.获取个人信息

GET ```/user/student/profile```

##### Response

```json
{
	"name": <String>
}
```

#### 2.录入人脸数据

POST ```/user/student/faceData```

ContentType: Multipart/form

Body: 人脸模型数据

#### 3.获取学生的课程

GET ```/user/student/courses```

##### Response

```json
[
	{
        "courseID": <Long>,
        "coruseName": <String>,
        "teacherName": <String>
    }
]
```

#### 4.获取学生某门课签到记录

GET ```/user/student/course:{courseID}/signInRecords```

##### Response

```json
[
	{
		"startTime": <Long>,
        "endTime": <Long>,
        "recordTime": <Long>,
        "beLate": <Boolean>,
        "hasLeaveSlip": <Boolean>
	}
]
```

#### 5.请假

POST ```/user/student/course:{courseID}/leaveSlip```

###### Body

multipart files:

* date: ```<String>```
* content: ```<String>```
* attachment: ```<File>```

#### 6.获取提交的请假信息

GET ```/user/student/leaveSlips```

##### Response

```json
[
    {
        "courseName": <String>,
        "state": <String>,
        "createTime": <Long>,
        "date": <String>,
        "content": <String>,
        "attachmentUrl": <String>
    }
]
```

### Resource

#### 1.获取请假条附件

GET ```/resource/leaveSlipAttachment/{leaveSlipID}-{attachmentName}```

> PS: 这个链接由后端构建