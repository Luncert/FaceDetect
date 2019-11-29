import React, { Component } from 'react';
import {
  Grid,
  Header,
  Image,
  List,
  Segment,
  Loader,
  Dimmer,
  Select,
} from 'semantic-ui-react'
import './App.css';
import LoginPage from './page/LoginPage';

class MainPage extends Component {
  render() {
    let identifiedList = []
    for (let i = 0; i < 20; i++) {
      identifiedList.push({avatar: 'https://react.semantic-ui.com/images/avatar/large/matthew.png', name: 'Rachel', id: '2016220202001'})
    }
    let classList = [
      { key: 'G0915320.01', value: 'G0915320.01', text: '计算机组成原理' },
      { key: 'G0915320.02', value: 'G0915320.02', text: '软件安全设计' },
    ]
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
                  <Dimmer active style={{borderRadius: '10px 0px 0px 10px'}}>
                    <Loader />
                  </Dimmer>
                  <canvas width='800' height='600'></canvas>
                </div>
                <div className='IdentifiedList'>
                  <div style={{textAlign: 'center', margin: '10px 0px 10px 0px'}}>
                    <Select placeholder='选择课程' options={classList} />
                  </div>
                  <div style={{textAlign: 'center', margin: '10px 0px 10px 0px'}}>
                    <Header as='h5'>已签到学生列表</Header>
                  </div>
                  <div className='scroller'
                    style={{position: 'relative', padding: 2,
                      width: '100%', height: 'calc(100% - 38px)',
                      overflowX: 'hidden',
                    }}>
                    <List>
                      {
                        identifiedList.map((v) => 
                        <List.Item>
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

class App extends Component {
  render() {
    return (
      <div style={{
              position: 'relative',
              boxSizing: 'border-box',
              top: 0,
              width: '100%', height: '100%',
          }}>
        <LoginPage />
        <div className='App-Background'></div>
      </div>
    )
  }
}

export default App;
