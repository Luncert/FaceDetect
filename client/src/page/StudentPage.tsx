import React, { Component } from 'react';
import { Segment, Grid, Header, Image, Card, Icon, Label, List, Button, Modal, Form } from 'semantic-ui-react';
import Axios from '../Axios'
import API from '../API'
import { toast } from 'react-toastify'

interface StudentProfile {
    id: string
    name: string
    hasFaceData: boolean
}

interface CourseSignInDetail {
    startTime: number
    endTime: number
    beLate: boolean
    nonSignedIn: boolean
}

interface CourseInfo {
    courseID: number
    courseName: string
    teacherName: string
    lateTimes: number
    nonSignedInTimes: number
    signInDetails: CourseSignInDetail[]
}

interface State {
    profile: StudentProfile
    courseInfo: CourseInfo[]
    applyLeaveSlip: number | null // course id
}

export default class StudentPage extends Component<any, State> {
    private date: string
    private content: string
    private attachmentName: string

    constructor(props: any) {
        super(props)
        this.state = {
            profile: {} as any,
            courseInfo: [],
            applyLeaveSlip: null
        }
    }

    componentDidMount() {
        Axios.get(API.user.student.getProfile)
            .then((rep) => {
                this.setState({profile: (rep.data as StudentProfile)})
            })
            .catch(() => {
                toast('获取个人信息失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
        Axios.get(API.user.student.getCourseInfo)
            .then((rep) => {
                this.setState({courseInfo: (rep.data as CourseInfo[])})
            })
            .catch(() => {
                toast('获取课程信息失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    postLeaveSlip(courseID: number) {
        let data = new FormData()
        data.append('date', this.date)
        data.append('content', this.content)
        let fileInput = (this.refs.fileInput as HTMLInputElement)
        if (fileInput.files.length > 0) {
            data.append('attachment', fileInput.files[0])
        }
        // data
        Axios.post(API.user.student.applyLeaveSlip(courseID), data, {headers: {'Content-Type': 'multipart/form-data'}})
            .then(() => {
                toast('提交成功', { position: toast.POSITION.BOTTOM_LEFT, autoClose: 2000, type: 'success' })
                this.setState({applyLeaveSlip: null})
            })
            .catch(() => {
                toast('提交请假信息失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
            })
    }

    parseDate(timestamp: number) {
        let d = new Date(timestamp)
        return `${d.getFullYear()}年${d.getMonth()}月${d.getDate()}日`
    }

    parseDuration(startTime: number, endTime: number) {
        let s = new Date(startTime)
        let e = new Date(endTime)
        return `${s.getHours()}点${s.getMinutes()}分 到 ${e.getHours()}点${e.getMinutes()}分`
    }

    render() {
        const { profile, courseInfo, applyLeaveSlip } = this.state
        return (
            <div style={{width: '100%'}}>
                <Segment className='Header'>
                    <Grid>
                        <Grid.Row columns={2}>
                            <Grid.Column>
                                <Header as='h3' className='Title' style={{color: 'rgb(8, 89, 187)'}}>Face Detect</Header>
                            </Grid.Column>
                            <Grid.Column floated='right' textAlign='right'>
                                <Image src='https://react.semantic-ui.com/images/avatar/large/matthew.png' avatar />
                            </Grid.Column>
                        </Grid.Row>
                    </Grid>
                </Segment>
                <Grid style={{width: '50%', margin: '0px auto'}}>
                    <Grid.Row columns={2}>
                        <Grid.Column width={4}>
                            <Card>
                                <Image src='https://react.semantic-ui.com/images/avatar/large/matthew.png' wrapped ui={false} />
                                <Card.Content>
                                    <Card.Header>{profile.name}</Card.Header>
                                    <Card.Meta><span>{profile.id}</span></Card.Meta>
                                </Card.Content>
                                <Card.Content extra>
                                    { true || profile.hasFaceData ? <Label color='green'>已录入脸部数据</Label> : <Label color='yellow'>无脸部数据</Label>}
                                </Card.Content>
                            </Card>
                        </Grid.Column>
                        <Grid.Column width={12}>
                            <Segment>
                                <Label attached='top' style={{textAlign: 'center'}}>
                                    <h4>我的课程</h4>
                                </Label>
                                <Modal open={applyLeaveSlip != null} closeOnDimmerClick={false} style={{width: 500}}>
                                    <Modal.Header>请假</Modal.Header>
                                    <Modal.Content>
                                        <Form>
                                            <Form.Field>
                                                <label>请假时间<span style={{color: 'red'}}> *</span></label>
                                                <input placeholder='请假时间' onChange={(e) => this.date = e.target.value} />
                                            </Form.Field>
                                            <Form.Field>
                                                <label>请假理由<span style={{color: 'red'}}> *</span></label>
                                                <input placeholder='请假理由' onChange={(e) => this.content = e.target.value} />
                                            </Form.Field>
                                            <Form.Field>
                                                <label>附件（可选）</label>
                                                <Button icon primary onClick={() => (this.refs.fileInput as HTMLInputElement).click()}><Icon name='upload' /></Button>
                                                <input ref='fileInput' type='file' placeholder='附件' style={{display: 'none'}}
                                                    onChange={(e) => {
                                                        this.attachmentName = e.target.value
                                                        this.forceUpdate()
                                                    }} />
                                                <Label>{this.attachmentName}</Label>
                                            </Form.Field>
                                            <div style={{textAlign: 'right'}}>
                                                <Button color='red' onClick={() => this.setState({applyLeaveSlip: null})}>取消</Button>
                                                <Button primary onClick={() => this.postLeaveSlip(1)}>提交</Button>
                                            </div>
                                        </Form>
                                    </Modal.Content>
                                </Modal>
                                <List divided relaxed style={{
                                    boxSizing: 'border-box',
                                    padding: '10px 0px', borderRadius: 10,
                                }}>
                                    { courseInfo.map((course, idx) => 
                                        <List.Item key={idx} style={{position: 'relative'}}>
                                            <div>
                                                {/* <span style={{
                                                    backgroundColor: 'rgb(100, 150, 230)', color: 'white',
                                                    marginLeft: -10, padding: '2px 5px 2px 20px',
                                                    borderRadius: '0px 5px 5px 0px'}}>
                                                    {course.name}
                                                </span> */}
                                                <Label color='blue' ribbon>{course.teacherName}【{course.courseName}】</Label>
                                                <Label basic color='teal' content='迟到' detail={course.lateTimes + '次'}></Label>
                                                <Label basic color='red' content='未到' detail={course.nonSignedInTimes + '次'}></Label>
                                                <Button primary size='tiny' floated='right' onClick={() => this.setState({applyLeaveSlip: course.courseID})}>请假</Button>
                                            </div>
                                            <List>
                                                { course.signInDetails.map((item, idx) =>
                                                    <List.Item key={idx}>
                                                        <List.Content>
                                                            <Icon name='circle' style={{color: item.nonSignedIn ? 'red' : (item.beLate ? 'yellow' : 'green')}} />
                                                            <span>{this.parseDate(item.startTime)}</span>
                                                            <span style={{marginLeft: 10}}>{this.parseDuration(item.startTime, item.endTime)}</span>
                                                        </List.Content>
                                                    </List.Item>
                                                )}
                                            </List>
                                        </List.Item> )}
                                </List>
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </div>
        )
    }
  }