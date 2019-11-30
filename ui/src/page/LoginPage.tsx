import React, { Component } from 'react';
import { Input, Grid, Image, Button, Label, Radio, Checkbox } from 'semantic-ui-react';
import './LoginPage.css';
import Axios from '../Axios';
import API from '../API';

interface Props {
    afterSignin: () => void
}

interface State {
    focusPwInput: boolean
    avatar: {
        account: string
        url: string
    }
}

export default class LoginPage extends Component<Props, State> {
    private account: string;
    private password: string;

    constructor(props: Props) {
        super(props)
        this.state = {
            focusPwInput: false,
            avatar: {
                account: '',
                url: ''
            }
        }
    }

    private valueCheck(value: string): boolean {
        for (let c of value) {
            if (c.charCodeAt(0) === 95
                || ('0' <= c && c <= '9')
                || ('A' <= c && c <= 'Z')
                || ('a' <= c && c <= 'z')) {
                continue;
            }
            return false;
        }
        return true;
    }

    inputAccount(e: React.ChangeEvent<HTMLInputElement>) {
        this.account = e.target.value
    }

    inputPassword(e: React.ChangeEvent<HTMLInputElement>) {
        this.password = e.target.value
    }

    loadAvatar() {
        Axios.get(API.user.avatar + '/' + this.account)
            .then((rep) => {
                this.setState({
                    avatar: {
                        account: this.account,
                        url: rep.data.url
                    }
                })
            })
    }

    render() {
        const { avatar } = this.state
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
                    <Image className='Avatar' avatar size='tiny' bordered
                        src={avatar.url ? avatar.url : 'https://react.semantic-ui.com/images/avatar/large/matthew.png'} />
                </div>
                <div style={{width: 200, margin: '0px auto'}}>
                    <Input className='InputEnhance' icon='user' iconPosition='left' transparent focus
                        onChange={this.inputAccount.bind(this)}
                        onBlur={() => {
                            if (this.account
                                && this.account !== avatar.account
                                && this.valueCheck(this.account)) {
                                this.loadAvatar()
                            }
                        }} />
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