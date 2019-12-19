import React, { Component, ReactElement } from 'react';
import './App.css';
import LoginPage from './page/LoginPage';
import ResetPasswordPage from './page/ResetPasswordPage';
import SignInPage from './page/SignIn';
import TeacherPage from './page/TeacherPage'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Router, Route } from 'react-router';

interface State {
    currentPage: ReactElement
}

// TODO: 查看所有课程的页面、查看课程所有学生签到信息的页面、查看操作记录的页面、查看学生签到信息的页面
// TODO: 刷新后跳转到主页面
class App extends Component<any, State> {
    private pages = [
        () => <LoginPage afterSignin={this.afterSignin.bind(this)}/>,
        // () => <ResetPasswordPage />,
        // () => <TeacherPage startSignIn={(courseID) => this.setState({currentPageIdx: this.pages(courseID)})} />,
        // () => <div></div>,
        // (courseID: number) => <SignInPage courseID={courseID} />
    ]

    constructor(props: any) {
        super(props)
        this.state = {
            currentPage: this.pages[0]()
        }
    }

    afterSignin(role: string) {
        localStorage.setItem('role', role)
        localStorage.setItem('authorized', 'true')
    }

    render() {
        return (
            <a></a>
        )
    }
}

export default App;
