import React, { Component } from 'react';
import { Button, Image, Icon, Segment, Grid, Header, List } from 'semantic-ui-react';
import './SignInRecordPage.css';
import { VerticalTimeline, VerticalTimelineElement }  from 'react-vertical-timeline-component';
import 'react-vertical-timeline-component/style.min.css';
import Axios from '../Axios';
import API from '../API';
import { toast } from 'react-toastify'

interface Student {
    id: string
    name: string
}

interface SignInRecord {
    id: number
    startTime: number
    endTime: number
    lateStudentList: Student[]
    nonSignedInStudentList: Student[]
}

interface State {
    records: SignInRecord[]
}

export default class SignInRecordPage extends Component<any, State> {
    constructor(props: any) {
        super(props)
        this.state = {
            records: []
        }
    }

    componentDidMount() {
        Axios.get(API.user.teacher.course.getSignInList(this.props.location.state.courseID))
            .then((rep) => {
                let data = (rep.data as SignInRecord[])
                this.setState({records: data})
            })
            .catch(() => {
                toast('获取签到记录失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
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
        const { records } = this.state
        return (
            <div style={{position: 'relative', width: '100%', height: '100%'}}>
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
                <div className='scroller' style={{
                    position: 'relative',
                    boxSizing: 'border-box',
                    width: '60%', maxHeight: 'calc(100% - 80px)',
                    overflowX: 'hidden',
                    overflowY: 'scroll',
                    margin: '0px auto',
                    borderRadius: 10,
                    backgroundColor: 'rgba(255, 255, 255, 0.5)'
                }}>
                    <Button as='a' icon primary circular floated='left'
                        style={{backgroundColor: 'rgba(0,0,0,0)'}}
                        onClick={() => this.props.history.push({pathname: '/user/teacher'})}>
                        <Icon name='arrow left' />
                    </Button>
                    <VerticalTimeline>
                        { records.map((record, idx) =>
                            <VerticalTimelineElement
                                key={idx}
                                className="vertical-timeline-element--work"
                                contentStyle={{ background: 'rgb(33, 150, 243)', color: '#fff' }}
                                contentArrowStyle={{ borderRight: '7px solid  rgb(33, 150, 243)' }}
                                iconStyle={{ background: 'rgb(33, 150, 243)', color: '#fff' }}
                            >
                                <h3 className='vertical-timeline-element-title'>{this.parseDate(record.startTime)}</h3>
                                <h4 className='vertical-timeline-element-subtitle'>{this.parseDuration(record.startTime, record.endTime)}</h4>
                                { record.lateStudentList.length > 0 && 
                                    <List bulleted>
                                        <List.Header>迟到学生列表</List.Header>
                                        <List.Content>
                                            { record.lateStudentList.map((student, idx) =>
                                                <List.Item key={idx}>{student.name}</List.Item> )}
                                        </List.Content>
                                    </List> }
                                { record.nonSignedInStudentList.length > 0 ? 
                                    <List divided relaxed>
                                        <List.Header>未签到学生列表</List.Header>
                                            { record.nonSignedInStudentList.map((student, idx) =>
                                                <List.Item key={idx}>
                                                    <List.Icon name='user' size='small' verticalAlign='middle' />
                                                    <List.Content>
                                                        <span style={{verticalAlign: 'middle'}}>{student.id}</span>
                                                        <span style={{verticalAlign: 'middle', marginLeft: 10}}>{student.name}</span>
                                                    </List.Content>
                                                </List.Item> )}
                                    </List>
                                    : '无迟到学生' }
                            </VerticalTimelineElement>) }
                    </VerticalTimeline>
                </div>
            </div>
        )
    }
  }