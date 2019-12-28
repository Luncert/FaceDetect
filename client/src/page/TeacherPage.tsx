import React, { Component, ReactElement } from 'react'
import { Grid, Header, Segment, Image, Icon, List, Label, Button, Modal, Form, Input, Dropdown, Popup, Container, Divider, Accordion, Confirm, Select } from 'semantic-ui-react'
import './TeacherPage.css'
import Axios from '../Axios'
import API from '../API'
import { toast } from 'react-toastify'
import config from '../Config.json';

// TODO: time

interface LeaveSlip {
    id: number,
    courseID: number,
    courseName: string,
    studentID: string,
    studentName: string,
    state: string,
    createTime: number,
    date: string,
    content: string,
    attachmentUrl: string
}

interface Course {
    id: number,
    name: string
}

interface Student {
    id: string,
    name: string
}

interface CreateCourseDto {
    name: string,
    studentIDList: string[]
}

interface GetSignInDto {
    id: number,
    startTime: number,
    endTime: number
}

interface LeaveSlipProcessResultDto {
    approved: boolean,
    signInID: number,
    comment: string
}

interface State {
    leaveSlips: LeaveSlip[],
    courses: Course[],
    createCourse: boolean,
    deleteCourseIdx: number | null,
    leaveSlipID: number,
    signInList: GetSignInDto[]
}

export default class TeacherPage extends Component<any, State> {

    private signInID: number | null = null
    private comment: string

    constructor(props: any) {
        super(props)
        this.state = {
            leaveSlips: [
                {id: 0, courseID: 1, courseName: 'English', studentID: '2016220204015', studentName: '李经纬', state: 'UnProcess', createTime: 0, date: '周二下午', content: '病假', attachmentUrl: null},
                {id: 1, courseID: 1, courseName: 'English', studentID: '2016220204015', studentName: '李经纬', state: 'UnProcess', createTime: 0, date: '周二下午', content: '病假', attachmentUrl: 'https://www.baidu.com'},
            ],
            courses: [{id: 1, name: 'English'}],
            createCourse: false,
            deleteCourseIdx: null,
            leaveSlipID: -1,
            signInList: []
        }
    }

    getLeaveSlips() {
        Axios.get(API.user.teacher.getLeaveSlips)
            .then((rep) => {
                this.setState({leaveSlips: rep.data})
            })
            .catch(() => {
                toast('获取请假信息失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    getCourses() {
        Axios.get(API.user.teacher.getCourses)
            .then((rep) => {
                this.setState({courses: rep.data})
            })
            .catch(() => {
                toast('获取课程信息失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    componentDidMount() {
        this.getLeaveSlips()
        this.getCourses()
    }

    startSignIn(courseID: number) {
        window.location.href = `/user/course/signIn?courseID=${courseID}`
    }

    postLeaveSlipComment(leaveSlipID: number, approved: boolean, signInID?: number, comment?: string) {
        let data: LeaveSlipProcessResultDto = {
            approved: approved,
            signInID: signInID || -1,
            comment: comment
        }
        Axios.post(API.user.teacher.postLeaveSlipResult(leaveSlipID), data)
            .then(() => {
                toast('提交假条处理结果成功', { position: toast.POSITION.BOTTOM_LEFT, type: 'success' })
            })
            .catch(() => {
                toast('提交假条处理结果失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    getCourseSignInList(courseID: number) {
        Axios.get(API.user.teacher.course.getSignInList(courseID))
            .then((rep) => {
                this.setState({signInList: (rep.data as GetSignInDto[])})
            })
            .catch(() => {
                toast('获取课程签到记录失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    removeCourse(courseIdx: number) {
        let courseID = this.state.courses[courseIdx].id
        Axios.delete(API.user.teacher.course.removeCourse(courseID))
            .then(() => {
                toast('删除课程成功', { position: toast.POSITION.BOTTOM_LEFT, type: 'success' })
                delete this.state.courses[courseIdx]
            })
            .catch((error) => {
                toast(`删除课程失败：${error.response.data}`, { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
            .finally(() => {
                this.setState({deleteCourseIdx: null})
            })
    }

    parseTimestamp(timestamp: number) {
        let d = new Date(timestamp)
        return `${d.getFullYear()}年${d.getMonth()}月${d.getDate()}日 ${d.getHours() < 12 ? '上午' : '下午'}${d.getHours() % 12}点`
    }

    render() {
        const {leaveSlips, courses, createCourse, deleteCourseIdx,
            leaveSlipID, signInList} = this.state
        return (
            <div style={{width: '100%'}}>
                <Segment className='Header'>
                    <Grid>
                        <Grid.Row columns={2}>
                            <Grid.Column>
                                <Header as='h3' className='Title' style={{color: 'rgb(8, 89, 187)'}}>Face Detect</Header>
                            </Grid.Column>
                            <Grid.Column floated='right' textAlign='right'>
                                <Image src='https://react.semantic-ui.com/images/wireframe/square-image.png' avatar />
                                <span>Luncert</span>
                            </Grid.Column>
                        </Grid.Row>
                    </Grid>
                </Segment>
                <Grid style={{width: '80%', margin: '0px auto'}}>
                    <Grid.Row columns={2} centered>
                        {/* 课程列表 */}
                        <Grid.Column width={6}>
                            <Segment>
                                <List divided relaxed>
                                    <List.Header>
                                        <Label style={{backgroundColor: 'white'}}><h3>我的课程</h3></Label>
                                        
                                        <Button floated='right' primary size='mini' onClick={() => this.setState({createCourse: true})}>新建课程</Button>
                                        <Modal style={{width: 400}} closeOnDimmerClick={false} open={createCourse}>
                                            <Modal.Header>创建课程</Modal.Header>
                                            <Modal.Content>
                                                <CreateCoursePage onDone={() => {
                                                    this.setState({createCourse: false})
                                                    this.getCourses()
                                                }}></CreateCoursePage>
                                            </Modal.Content>
                                        </Modal>
                                        <Confirm
                                            open={deleteCourseIdx != null}
                                            header='操作确认'
                                            content={ deleteCourseIdx != null ? `确认要删除课程${courses[deleteCourseIdx].name}吗？` : '' }
                                            onCancel={() => this.setState({deleteCourseIdx: null})}
                                            onConfirm={() => {
                                                this.removeCourse(deleteCourseIdx)
                                            }}
                                            />
                                    </List.Header>

                                    {courses.map((course, idx) =>
                                        <List.Item key={course.id}>
                                            <List.Content>
                                            <Popup content='开始签到'
                                                trigger={
                                                <List.Content verticalAlign='middle'>
                                                    <List.Icon name='caret right' size='small' verticalAlign='middle' />
                                                    <span style={{color: 'teal', cursor: 'pointer'}} onClick={() => this.startSignIn(course.id)}>{course.name}</span>
                                                    <div style={{float: 'right'}} onClick={() => this.setState({deleteCourseIdx: idx})}><Icon name='close' color='red' /></div>
                                                </List.Content>
                                            } />
                                            </List.Content>
                                        </List.Item>
                                    )}
                                </List>
                            </Segment>
                        </Grid.Column>
                        {/* 未处理假条 */}
                        <Grid.Column width={6}>
                            <Segment>
                                <Accordion>
                                    <Label ribbon><h5>待处理假条</h5></Label>
                                    {leaveSlips.map((leaveSlip, idx) => (
                                        [
                                            <Accordion.Title
                                                key={`title-${idx}`}
                                                active={leaveSlipID === leaveSlip.id}
                                                onClick={() => {
                                                    this.signInID = null
                                                    this.comment = ''
                                                    this.setState({leaveSlipID: leaveSlipID === leaveSlip.id ? -1 : leaveSlip.id})}
                                                }>
                                                <Icon name='dropdown' />
                                                <span style={{color: 'teal'}}>
                                                    {`${leaveSlip.studentID} - ${leaveSlip.studentName}`}
                                                </span>
                                                <span style={{float: 'right'}}>{this.parseTimestamp(leaveSlip.createTime)}</span>
                                            </Accordion.Title>,
                                            <Accordion.Content
                                                key={`content-${idx}`}
                                                active={leaveSlipID === leaveSlip.id}
                                                style={{ padding: '5px 10px',
                                                    backgroundColor: 'rgb(100, 150, 220)',
                                                    color: 'white',
                                                    borderRadius: 5, boxShadow: '0px 0px 3px gray',}}>
                                                <div style={{position: 'relative', margin: '10px 0px'}}>
                                                    <Label color='blue'>
                                                        <Icon name='book' />课程名称<Label.Detail>{leaveSlip.courseName}</Label.Detail>
                                                    </Label>
                                                    <Button disabled={leaveSlip.attachmentUrl === null} as='a'
                                                        href={config.server.addr + leaveSlip.attachmentUrl} download='note.txt'
                                                        size='mini' color='facebook' floated='right' circular
                                                        icon='arrow circle down' content='附件'>
                                                    </Button>
                                                </div>
                                                <p>
                                                    <span>请假时间：</span>
                                                    <span style={{fontWeight: 'bold'}}>{leaveSlip.date}</span>
                                                </p>
                                                <p>
                                                    <span>请假理由：</span>
                                                    <span style={{fontWeight: 'bold'}}>{leaveSlip.content}</span>
                                                </p>
                                                <Divider />
                                                {/* TODO: limit length */}
                                                {/* 输入回复内容 */}
                                                <textarea rows={5} style={{width: '100%', resize: 'none',
                                                    backgroundColor: 'rgb(120, 180, 255)',
                                                    outline: 'none', border: 'none',
                                                    }} placeholder='回复内容（可选）'
                                                    onChange={(e) => {this.comment = e.target.value}}></textarea>
                                                {/* 选择签到记录 */}
                                                <Container>
                                                    <p>关联请假条到历史签到记录<span style={{color: 'yellow'}}>（批准必填项）</span></p>
                                                    <Dropdown fluid selection
                                                        onClick={() => {
                                                            this.getCourseSignInList(leaveSlip.courseID)
                                                        }}
                                                        onChange={(_, data) => this.signInID = (data.value as number)}
                                                        style={{backgroundColor: 'rgb(120, 180, 255)'}}
                                                        options={signInList.map(
                                                            (s) => ({key: s.id, value: s.id, text: `${s.startTime}到${s.endTime}`}))} />
                                                </Container>
                                                {/* 按钮 */}
                                                <Container textAlign='right' style={{marginTop: 10}}>
                                                    <Button size='mini' color='red' onClick={() => this.postLeaveSlipComment(leaveSlip.id, false)}>回绝</Button>
                                                    <Button size='mini' primary onClick={() => {
                                                        this.postLeaveSlipComment(leaveSlip.id, true, this.signInID, this.comment)
                                                        delete leaveSlips[idx]
                                                        this.forceUpdate()
                                                    }}>批准</Button>
                                                </Container>
                                            </Accordion.Content>
                                        ])
                                    )}
                                </Accordion>
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </div>
        )
    }
}

interface CreateCoursePageProps {
    onDone: () => void
}

class CreateCoursePage extends Component<CreateCoursePageProps> {
    
    private students: {[key: string]: Student}
    private courseName: string
    private avaliableStudents = new Set<string>()
    private selectedStudents = new Set<string>()

    constructor(props: CreateCoursePageProps) {
        super(props)
        this.students = {}
    }

    inputCourseName(e: React.ChangeEvent<HTMLInputElement>) {
        this.courseName = e.target.value
    }

    addStudent(sid: string) {
        this.avaliableStudents.delete(sid)
        this.selectedStudents.add(sid)
        this.forceUpdate()
    }

    removeStudent(sid: string) {
        this.selectedStudents.delete(sid)
        this.avaliableStudents.add(sid)
        this.forceUpdate()
    }

    componentDidMount() {
        Axios.get(API.user.teacher.getStudents)
            .then((rep) => {
                for (let s of (rep.data as Student[])) {
                    this.students[s.id] = s
                    this.avaliableStudents.add(s.id)
                }
                this.forceUpdate()
            })
            .catch(() => {
                toast('获取学生列表失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    createCourse() {
        let data: CreateCourseDto = {
            name: this.courseName,
            studentIDList: Array.from(this.selectedStudents)
        }
        Axios.post(API.user.teacher.createCourse, data)
            .then(() => {
                toast('创建课程成功', { position: toast.POSITION.BOTTOM_LEFT, type: 'success', autoClose: 3000 })
                this.props.onDone()
            })
            .catch(() => {
                toast('创建课程失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    render() {
        return (
            <Form>
                <Form.Field>
                    <label>课程名称</label>
                    <Input onChange={this.inputCourseName.bind(this)} />
                </Form.Field>
                <Form.Field>
                    <label>选择学生</label>
                    <Select fluid onChange={(_, data) => this.addStudent(data.value as string)}
                        options={
                            (() => {
                                let tmp: any = []
                                this.avaliableStudents.forEach((sid) => {
                                    let s = this.students[sid]
                                    tmp.push({key: s.id, value: s.id, text: s.name})
                                })
                                return tmp
                            })()
                        } />
                    <List divided relaxed>
                        <List.Header>
                            <h5>已选择的学生</h5>
                        </List.Header>
                        {
                            (() => {
                                let tmp: ReactElement[] = []
                                this.selectedStudents.forEach((sid) => {
                                    tmp.push(
                                        <List.Item key={sid}>
                                            <List.Content floated='right'>
                                                <span onClick={() => this.removeStudent(sid)}><Icon name='remove' color='red' /></span>
                                            </List.Content>
                                            <List.Icon name='user' size='large' verticalAlign='middle' />
                                            <List.Content>{this.students[sid].name}</List.Content>
                                        </List.Item>
                                    )
                                })
                                return tmp
                            })()
                        }
                    </List>
                </Form.Field>
                <Form.Field style={{textAlign: 'right'}}>
                    <Button onClick={() => this.props.onDone()} color='red'>取消</Button>
                    <Button onClick={this.createCourse.bind(this)}>提交</Button>
                </Form.Field>
            </Form>
        )
    }
}