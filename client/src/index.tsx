import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';

import 'semantic-ui-css/semantic.min.css';
import { Router, Route } from 'react-router';
import { createBrowserHistory } from 'history'
import { ToastContainer } from 'react-toastify';
import TeacherPage from './page/TeacherPage';
import SignInPage from './page/SignInPage';
import LoginPage from './page/LoginPage';
import StudentPage from './page/StudentPage';
import ResetPasswordPage from './page/ResetPasswordPage';
import 'react-toastify/dist/ReactToastify.css';
import SignInRecordPage from './page/SignInRecordPage';

ReactDOM.render(
    <Router history={createBrowserHistory()}>
        <div style={{
                position: 'relative',
                boxSizing: 'border-box',
                top: 0,
                width: '100%', height: '100%',
            }}>
            <div className='App-Background'></div>
            <Route path='/user/login' component={LoginPage} />
            <Route path='/user/resetPassword' component={ResetPasswordPage} />
            <Route path='/user/teacher' component={TeacherPage} />
            <Route path='/user/student' component={StudentPage} />
            <Route path='/user/course/signIn' component={SignInPage} />
            <Route path='/user/course/signInRecord' component={SignInRecordPage} />
            <ToastContainer autoClose={false} />
        </div>
    </Router>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();