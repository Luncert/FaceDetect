import React, { Component } from 'react';
import './StudentPage.css';

export default class StudentPage extends Component {

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
                backgroundImage: 'url(' + require('../res/login-header.jfif') + ')',
                backgroundSize: 'cover'
            }}>
            </div>
        )
    }
  }