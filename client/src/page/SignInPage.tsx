import React, { Component } from 'react';
import { Grid, Header, Image, List, Segment, Button, Divider, Modal, Form, Dropdown, Dimmer, Loader, Container, Icon} from 'semantic-ui-react'
import './SignInPage.css'
import config from '../Config.json';

interface Student {
    id: string
    name: string
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
const AI_OPTION = ['mtcnn + facenet', 'opencv']

export default class SignInPage extends Component<any, State> {
    private aiOption: string = AI_OPTION[0]
    private identifiedStudents: Student[] = [
        // {id: '2016220204015', name: '李经纬'},
    ]

    constructor(props: any) {
        super(props)
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
    }

    switch() {
        if (!this.state.running) {
            this.setState({running: true});
            (window as any).startDataTransport({
                algorithm: this.aiOption,
                frameSize: VIDEO_SIZE,
                serverHost: config.server.addr,
                // TODO:
                userInfo: {
                    account: 't1',
                    password: '123456'
                },
                courseID: 1
            }, (data: Student[]) => {
                console.log(data)
                for (let s of data) {
                    this.identifiedStudents.push(s)
                }
                // send ajax request
                // signIn()
                this.forceUpdate()
            })
        } else {
            this.setState({running: false});
            (window as any).stopDataTransport()
        }
    }

    signIn() {

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
                                width: 30, height: 70,
                                top: VIDEO_SIZE.height / 2, right: 10,
                                boxSizing: 'border-box',
                                backgroundColor: 'rgba(0, 0, 0, 0)'}}>
                                <Button as='div' icon={running ? 'stop' : 'play'} circular primary size='tiny'
                                    onClick={() => this.switch()}></Button>
                                <Button as='div' icon='setting' circular primary size='tiny' disabled={running}
                                    style={{margin: '10px 0px 10px 0px'}}
                                    onClick={() => this.setState({openSettings: true})}></Button>
                                <Button as='div' icon='search' circular primary size='tiny' disabled={running}
                                    onClick={() => {}}></Button>
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
                                <Header as='h5'>已签到学生列表({this.identifiedStudents.length}/120)</Header>
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
  