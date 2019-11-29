import React, { Component } from "react";
import { Input, Button, Grid, Label } from "semantic-ui-react";

// TODO: 发送邮件后跳转到目标邮箱
  
export default class ResetPasswordPage extends Component {
    render() {
        return (
            <div style={{
                position: 'relative',
                boxSizing: 'border-box',
                top: 'calc(50% - 150px)',
                margin: '0px auto',
                width: 400, height: 300,
                borderRadius: 10,
                boxShadow: '0px 0px 5px black',
                backgroundColor: 'rgb(255, 255, 255)',
                overflow: 'hidden',
            }}>
            <Label size='big' style={{borderRadius: '0px 0px 5px 0px'}}>找回密码</Label>
            <div style={{width: '100%', height: '100%', padding: '0px 50px',}}>
                <Grid>
                    <Grid.Row style={{marginTop: 100}}>
                        <Grid.Column>
                            <Input className='InputEnhance' icon='mail' iconPosition='left' placeholder='请填写您账号绑定的邮箱'
                                focus style={{width: '100%'}} />
                        </Grid.Column>
                    </Grid.Row>
                    <Grid.Row>
                        <Grid.Column>
                            <Button primary floated='right'>发送验证邮件</Button>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </div>
            </div>
        )
    }
}