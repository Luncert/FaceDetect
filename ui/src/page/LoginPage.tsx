import React, { Component } from 'react';
import { Input, Grid, Image, Button, Label, Radio, Checkbox } from 'semantic-ui-react';
import './LoginPage.css';

export default class LoginPage extends Component {
    render() {
      return (
        <div style={{
            position: 'relative',
            top: 'calc(50% - 150px)',
            margin: '0px auto',
            width: 400, height: 300,
            borderRadius: 10,
            boxShadow: '0px 0px 5px black',
            backgroundColor: 'rgb(255, 255, 255)',
        }}>
            <div style={{
                width: '100%', height: 100,
                boxSizing: 'border-box',
                position: 'relative',
                padding: 5,
                borderRadius: '10px 10px 0px 0px',
                borderTop: '1px solid rgb(80, 80, 80)',
                backgroundImage: 'url(' + require('../res/login-header.jfif') + ')',
                backgroundSize: 'cover'
            }}>
                <span style={{
                    color: 'white',
                    fontWeight: 'bold',
                    fontSize: '1.5em',
                    textShadow: '0px 0px 3px rgba(20, 88, 180)'
                }}>Face Detect</span>
            </div>
            <div style={{width: '100%', height: 50}}>
                <Image className='Avatar' src='https://react.semantic-ui.com/images/avatar/large/matthew.png' avatar size='tiny' bordered />
            </div>
            <div style={{width: 200, margin: '0px auto'}}>
                <Input className='InputEnhance' icon='user' iconPosition='left' transparent focus />
                <Input className='InputEnhance' icon='lock' iconPosition='left' transparent
                            type='password'
                            onFocus={() => this.setState({focusPwInput: true})}
                            onBlur={() => this.setState({focusPwInput: false})}
                            />
                <Checkbox label='记住账号' />
                <a style={{float: 'right', cursor: 'pointer'}}>找回密码</a>
                <Button primary size='mini' style={{width: 200, marginTop: 20}}>登录</Button>
            </div>
        </div>
      )
    }
  }