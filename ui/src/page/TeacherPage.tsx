import React, { Component } from 'react'
import { Grid, Header, Segment, Image, Select, Table, Icon } from 'semantic-ui-react'
import './teacherPage.css'

export default class TeacherPage extends Component {
    getOptions() {
        let options = [
            {key: 'A', value: 'A', text: '微积分I'},
            {key: 'B', value: 'B', text: '微积分II'},
            {key: 'C', value: 'C', text: '大学物理'}
        ]
        return options
    }
    getCourseTime() {
        let times = [
            {key: 't1', value: 't1', text: '15周-1'},
            {key: 't2', value: 't2', text: '15周-2'},
            {key: 't3', value: 't3', text: '16周-1'},
            {key: 't4', value: 't4', text: '15周-2'}
        ]
        return times
    }
    render() {
        const options = this.getOptions()
        const timeOptions = this.getCourseTime()
        return (
            <div>
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
                <div className="teacher-wrapper">
                <Segment className="teacher-board">
                    <Grid style={{height: '100%'}}>
                        <Grid.Row stretched>
                        <Grid.Column width={6}>
                            <Segment >
                                <div className="teacher-label">Step1：选择课程</div>
                                <Select placeholder='选择课程' options={options} />
                            </Segment>
                            <Segment>
                                <div className="teacher-label">Step2：选择日期</div>
                                <Select placeholder='选择课程日期' options={timeOptions} />
                            </Segment>
                        </Grid.Column>
                        <Grid.Column width={10}>
                            <Segment>
                                <div className="teacher-label">签到名单</div>
                                    <Table basic='very' celled collapsing>
                                        <Table.Header>
                                        <Table.Row>
                                            <Table.HeaderCell>学生</Table.HeaderCell>
                                            <Table.HeaderCell>是否签到</Table.HeaderCell>
                                        </Table.Row>
                                        </Table.Header>

                                        <Table.Body>
                                        <Table.Row>
                                            <Table.Cell>
                                            <Header as='h4' image>
                                                <Image src='https://react.semantic-ui.com/images/avatar/small/lena.png' rounded size='mini' />
                                                <Header.Content>
                                                Lena
                                                <Header.Subheader>软件工程</Header.Subheader>
                                                </Header.Content>
                                            </Header>
                                            </Table.Cell>
                                            <Table.Cell><Icon color='green' name='checkmark' size='large' /></Table.Cell>
                                        </Table.Row>
                                        <Table.Row>
                                            <Table.Cell>
                                            <Header as='h4' image>
                                                <Image src='https://react.semantic-ui.com/images/avatar/small/matthew.png' rounded size='mini' />
                                                <Header.Content>
                                                Matthew
                                                <Header.Subheader>软件工程</Header.Subheader>
                                                </Header.Content>
                                            </Header>
                                            </Table.Cell>
                                            <Table.Cell><Icon color='green' name='checkmark' size='large' /></Table.Cell>
                                        </Table.Row>
                                        <Table.Row>
                                            <Table.Cell>
                                            <Header as='h4' image>
                                                <Image src='https://react.semantic-ui.com/images/avatar/small/lindsay.png' rounded size='mini' />
                                                <Header.Content>
                                                Lindsay
                                                <Header.Subheader>软件工程</Header.Subheader>
                                                </Header.Content>
                                            </Header>
                                            </Table.Cell>
                                            <Table.Cell><Icon color='green' name='checkmark' size='large' /></Table.Cell>
                                        </Table.Row>
                                        <Table.Row>
                                            <Table.Cell>
                                            <Header as='h4' image>
                                                <Image src='https://react.semantic-ui.com/images/avatar/small/mark.png' rounded size='mini' />
                                                <Header.Content>
                                                Mark
                                                <Header.Subheader>软件工程</Header.Subheader>
                                                </Header.Content>
                                            </Header>
                                            </Table.Cell>
                                            <Table.Cell><Icon color='red' name='close' size='large' /></Table.Cell>
                                        </Table.Row>
                                        </Table.Body>
                                    </Table>
                            </Segment>
                            </Grid.Column>
                            </Grid.Row>
                        </Grid>
                </Segment>
                </div>
            </div>
        )
    }
}