import React, { Component } from 'react';
import './App.css';
import LoginPage from './page/LoginPage';
import ResetPasswordPage from './page/ResetPasswordPage';
import MainPage from './page/MainPage';


// TODO: 查看所有课程的页面、查看课程所有学生签到信息的页面、查看操作记录的页面、查看学生签到信息的页面
class App extends Component {
  render() {
    return (
      <div style={{
              position: 'relative',
              boxSizing: 'border-box',
              top: 0,
              width: '100%', height: '100%',
          }}>
        <div className='App-Background'></div>
        <LoginPage afterSignin={() => {}}/>
        {/* <ResetPasswordPage /> */}
        {/* <MainPage /> */}
      </div>
    )
  }
}

export default App;
