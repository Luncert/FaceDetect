import React, { Component } from 'react';
import { Input, Grid, Image, Button } from 'semantic-ui-react';

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
            backgroundColor: 'rgb(255, 255, 255, 1)',
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
            <Image src='https://react.semantic-ui.com/images/avatar/large/matthew.png' avatar size='tiny' bordered
                style={{
                    display: 'block',
                    position: 'relative',
                    margin: '0px auto',
                    marginTop: '-40px'
                }} />
            <Grid textAlign='center' stretched>
                <Grid.Row>
                    <Input icon='user' iconPosition='left' transparent
                        style={{borderBottom: '1px solid gray'}}
                        />
                </Grid.Row>
                <Grid.Row>
                    <Input icon='lock' iconPosition='left' transparent type='password'
                        onFocus={() => this.setState({focusPwInput: true})}
                        onBlur={() => this.setState({focusPwInput: false})}
                        style={{borderBottom: '1px solid gray'}}
                        />
                </Grid.Row>
                <Grid.Row>
                    <Button>登录</Button>
                </Grid.Row>
            </Grid>
        </div>
      )
    }
  }