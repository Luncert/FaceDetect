import React, { Component } from 'react';
import { Grid, Header, Image, List, Segment, Button, Divider, Modal, Form, Dropdown, Dimmer, Loader, Container, Icon} from 'semantic-ui-react'
import './SignInPage.css'
import config from '../Config.json';
import Axios from '../Axios';
import API from '../API';
import { toast } from 'react-toastify'

interface Student {
    id: string
    name: string
}

interface SignInResult {
    result: 'processed' | 'unprocessed'
    description: string
    external: SignInFailureRecord[]
}

interface SignInFailureRecord {
    studentID: string
    failureReason: string
}

interface State {
    openSettings: boolean
    loading: boolean
    running: boolean
}

const VIDEO_SIZE = {
    width: 800,
    height: 600
}
const AI_OPTION = ['opencv', 'mtcnn + facenet']

export default class SignInPage extends Component<any, State> {
    private courseID: number
    private courseName: string
    private signInID: number | null
    private aiOption: string = AI_OPTION[0]
    private identifiedStudents: Student[] = []

    constructor(props: any) {
        super(props)
        this.courseID = this.props.location.state.courseID
        this.courseName = this.props.location.state.courseName
        this.state = {
            openSettings: false,
            loading: false,
            running: false
        }
    }

    componentWillUnmount() {
        if (this.state.running) {
            (window as any).stopDataTransport()
        }
        this.stopSignIn()
    }

    switch() {
        if (!this.state.running) {
            // notify server to start sign in
            Axios.post(API.user.teacher.course.startSignIn(this.courseID))
                .then((rep) => {
                    this.setState({running: true});
                    this.signInID = rep.data.signInID;
                    console.log(`sign in started, id=${this.signInID}`);
                    // notify ai service
                    (window as any).startDataTransport({
                        algorithm: this.aiOption,
                        frameSize: VIDEO_SIZE,
                        serverHost: config.server.addr,
                        // TODO:
                        userInfo: {
                            account: 't1',
                            password: '123456'
                        },
                        courseID:  this.courseID// pass by another prev page
                    }, (data: Student[]) => {
                        let studentIds = []
                        for (let s of data) {
                            this.identifiedStudents.push(s)
                            studentIds.push(s.id)
                        }
                        // send ajax request
                        Axios.put(API.user.teacher.course.signInStudents(this.courseID, this.signInID), studentIds)
                            .catch((error) => {
                                console.error('签到失败', error.response.data)
                            })
                        // signIn()
                        this.forceUpdate()
                    });
                })
                .catch((error) => {
                    toast('开始签到失败: ' + error.message, { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
                });
        } else {
            this.setState({running: false})
            this.stopSignIn()
        }
    }

    stopSignIn() {
        if (this.signInID != null) {
            (window as any).stopDataTransport();
            Axios.put(API.user.teacher.course.stopSignIn(this.courseID, this.signInID))
                .then(() => {
                    console.log(`sign in stoped`);
                    toast('结束签到成功', { position: toast.POSITION.BOTTOM_LEFT, type: 'success', autoClose: 2000 })
                })
                .catch(() => {
                    toast('结束签到失败', { position: toast.POSITION.BOTTOM_LEFT, type: 'error' })
                })
            this.signInID = null
            this.identifiedStudents = []
        }
    }

    render() {
        const { openSettings, loading, running } = this.state
        return (
            <div style={{
                    position: 'relative',
                    boxSizing: 'border-box',
                    top: 0,
                    width: '100%', height: '100%',
                }}>
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
                <div style={{width: '100%'}}>
                    <div className='Container'>
                        <div className='VideoCanvas'>
                            <div style={{position: 'absolute',
                                width: 30, top: VIDEO_SIZE.height / 2, right: 10,
                                boxSizing: 'border-box',
                                backgroundColor: 'rgba(0, 0, 0, 0)'}}>
                                <Button as='div' icon={running ? 'stop' : 'play'} circular primary size='tiny'
                                    onClick={() => this.switch()}></Button>
                                <Button as='div' icon='setting' circular primary size='tiny' disabled={running}
                                    style={{margin: '10px 0px 10px 0px'}}
                                    onClick={() => this.setState({openSettings: true})}></Button>
                                <Button as='div' icon='search' circular primary size='tiny' disabled={running}
                                    onClick={() => {}}></Button>
                                <Button as='div' icon='close' circular color='red' size='tiny' disabled={running}
                                    style={{marginTop: 10}}
                                    onClick={() => this.props.history.push({pathname: '/user/teacher'})}></Button>
                            </div>
                            <Modal open={openSettings} closeOnDimmerClick={false} style={{width: 500}}>
                                <Modal.Header>设置</Modal.Header>
                                <Modal.Content>
                                    <Dimmer active={loading}><Loader /></Dimmer>
                                    <Form>
                                        <Form.Field>
                                            <label>选择识别算法</label>
                                            <Dropdown
                                                placeholder='选择识别算法'
                                                fluid search selection
                                                defaultValue={this.aiOption}
                                                options={
                                                    AI_OPTION.map((item, idx) => ({key: idx, value: item, text: item}))
                                                }
                                                onChange={(_, data) => this.aiOption = (data.value as string)}
                                            />
                                        </Form.Field>
                                        <div style={{textAlign: 'right'}}>
                                            <Button primary onClick={() => this.setState({openSettings: false})}>确认</Button>
                                        </div>
                                    </Form>
                                </Modal.Content>
                            </Modal>
                            <canvas id='video_canvas' width={VIDEO_SIZE.width} height={VIDEO_SIZE.height}></canvas>
                        </div>
                        <div className='IdentifiedList'>
                            <div style={{textAlign: 'center',
                                padding: '10px 0px 10px 0px',
                                borderBottom: '1px solid rgb(180, 180, 180)'}}>
                                <Header as='h5'>《{this.courseName}》已签到学生({this.identifiedStudents.length})</Header>
                            </div>
                            <div className='scroller'
                                style={{position: 'relative', padding: 2,
                                    width: '100%', height: 'calc(100% - 38px)',
                                    overflowX: 'hidden',
                                }}>
                                <List style={{padding: '5px 0px 0px 5px'}}>
                                    {this.identifiedStudents.map((s, idx) => 
                                        <List.Item key={idx} style={{
                                            boxSizing: 'border-box',
                                            borderRadius: '16px 0px 0px 16px'}}>
                                            <Image avatar src='https://react.semantic-ui.com/images/avatar/small/matthew.png' />
                                            <List.Content>
                                                <List.Header as='span' style={{color: 'rgb(60, 131, 218)'}}>{s.name}</List.Header>
                                                <List.Description>{s.id}</List.Description>
                                            </List.Content>
                                        </List.Item>)}
                                </List>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
  