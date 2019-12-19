import React, { Component } from 'react';
import { Input, Button, Checkbox, Loader, Dimmer } from 'semantic-ui-react';
import { toast } from 'react-toastify';
import './LoginPage.css';
import Axios from '../Axios';
import API from '../API';

interface Props {
    afterSignin: (role: string) => void
}

interface State {
    focusPwInput: boolean
    loading: boolean
}

export default class LoginPage extends Component<Props, State> {
    private account: string;
    private password: string;

    constructor(props: Props) {
        super(props)
        this.state = {
            focusPwInput: false,
            loading: false,
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

    verify() {
        // 值校验
        if (!this.account || !this.valueCheck(this.account)
            || !this.password || !this.valueCheck(this.password)) {
            toast('请正确填写登录信息', { position: toast.POSITION.BOTTOM_LEFT, type: 'warning', autoClose: 3000 })
        } else {
            // 发起验证请求
            let encode = encodeURIComponent;
            let data = encode('account') + '=' + encode(this.account)
                + '&' + encode('password') + '=' + encode(this.password);
            this.setState({loading: true})
            Axios.post(API.user.signin, data,
                { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })
                .then((rep) => {
                    this.props.afterSignin(rep.data.role)
                })
                .catch((err) => {
                    console.log(err)
                    // let rep = err.response
                    // if (!rep.data.identified) {
                    //     toast('账号或密码不正确', { position: toast.POSITION.BOTTOM_LEFT, type: 'error', autoClose: 3000 })
                    // } else {
                    //     console.error(err)
                    // }
                    toast('账号或密码不正确', { position: toast.POSITION.BOTTOM_LEFT, type: 'error', autoClose: 3000 })
                    this.setState({loading: false})
                })
        }
    }

    render() {
        const { loading } = this.state
        return (
            <div style={{
                position: 'relative',
                top: 'calc(50% - 150px)',
                margin: '0px auto',
                width: 400, height: 300,
                borderRadius: 10,
                boxShadow: '0px 0px 5px black',
                backgroundColor: 'rgb(255, 255, 255)',
                backgroundImage: 'url(' + require('../res/login-header.jfif') + ')',
                backgroundSize: 'cover'
            }}>
            <Dimmer active={loading}>
              <Loader indeterminate />
            </Dimmer>
                <div style={{
                    width: '100%', height: 100,
                    boxSizing: 'border-box',
                    position: 'relative',
                    padding: 5,
                    textAlign: 'center',
                    borderRadius: '10px 10px 0px 0px',
                    borderTop: '1px solid rgb(80, 80, 80)',
                }}>
                    <span style={{
                        position: 'relative',
                        top: 60,
                        color: 'white',
                        fontWeight: 'bold',
                        fontSize: '3em',
                        textShadow: '0px 0px 3px black'
                    }}>Face Detect</span>
                </div>
                <div style={{position: 'relative', width: '100%', padding: '10px 0px',
                    backgroundColor: 'rgba(255, 255, 255, 0.7)',
                    boxShadow: '0px 0px 3px black'}}>
                    <div style={{width: 200, margin: '0px auto'}}>
                        <Input className='InputEnhance' icon='user' iconPosition='left' focus
                            onChange={this.inputAccount.bind(this)}
                            />
                        <Input className='InputEnhance' icon='lock' iconPosition='left'
                                    type='password'
                                    onFocus={() => this.setState({focusPwInput: true})}
                                    onBlur={() => this.setState({focusPwInput: false})}
                                    onChange={this.inputPassword.bind(this)}
                                    />
                        <a style={{float: 'right', cursor: 'pointer'}}>找回密码</a>
                        <Button primary size='mini' style={{width: 200, marginTop: 20}}
                            onClick={this.verify.bind(this)}>登录</Button>
                    </div>
                </div>
            </div>
        )
    }
  }