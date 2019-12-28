import React, { Component } from 'react';
import { Grid, Header, Image, List, Segment, Loader, Dimmer, Select} from 'semantic-ui-react'
import { toast } from 'react-toastify';
import './SignIn.css'

interface Props {
    courseID: number
}

interface State {
    aiOption: 'opencv' | 'mtcnn + facenet' | string
}

export default class SignInPage extends Component<Props, State> {
    private godsTimer: any

    constructor(props: Props) {
        super(props)
        this.state = {
            aiOption: 'opencv'
        }
    }

    componentDidMount() {
        // (window as any).startFrameTransport()
    }

    componentWillUnmount() {
        // (window as any).stopFrameTransport()
    }

    render() {
        const { aiOption } = this.state
        let identifiedList = []
        for (let i = 0; i < 1; i++) {
            identifiedList.push({avatar: 'https://react.semantic-ui.com/images/avatar/large/matthew.png', name: 'Rachel', id: '2016220202001'})
        }
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
                            <Image src='https://react.semantic-ui.com/images/wireframe/square-image.png' avatar />
                            <span>Luncert</span>
                        </Grid.Column>
                    </Grid.Row>
                    </Grid>
                </Segment>
                <div style={{width: '100%'}}>
                    <div className='Container'>
                        <div className='VideoCanvas'>
                            {/* <Dimmer active style={{borderRadius: '10px 0px 0px 10px'}}><Loader /></Dimmer> */}
                            <canvas id='video_canvas' width='800' height='600'></canvas>
                        </div>
                        <div className='IdentifiedList'>
                            <Select className='icon' icon='setting' fluid labeled button defaultValue={aiOption}
                                onChange={(_, data) => {
                                    let option = data.value as string
                                    if (aiOption !== option) {
                                        if (this.godsTimer) {
                                            clearTimeout(this.godsTimer)
                                        }
                                        this.godsTimer = setTimeout(() => {
                                            this.setState({aiOption: option})
                                            toast(
                                                <span>已切换为<strong>{option}</strong></span>,
                                                { position: toast.POSITION.BOTTOM_LEFT, type: 'success', autoClose: 2000 }
                                            )
                                            this.godsTimer = null
                                        }, 1500)
                                    }
                                }}
                                options={[
                                    {key: 0, value: 'opencv', text: 'opencv'},
                                    {key: 1, value: 'mtcnn+facenet', text: 'mtcnn + facenet'}
                                ]} />
                            <div style={{textAlign: 'center', margin: '10px 0px 10px 0px'}}>
                                <Header as='h5'>已签到学生列表({identifiedList.length}/120)</Header>
                            </div>
                            <div className='scroller'
                                style={{position: 'relative', padding: 2,
                                    width: '100%', height: 'calc(100% - 38px)',
                                    overflowX: 'hidden',
                                }}>
                                <List>
                                    {
                                    identifiedList.map((v, idx) => 
                                    <List.Item key={idx}>
                                        <Image src={v.avatar} avatar />
                                        <List.Content>
                                        <List.Header as='a'>{v.name}</List.Header>
                                        <List.Description>{v.id}</List.Description>
                                        </List.Content>
                                    </List.Item>)
                                    }
                                </List>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
  