import React from 'react'
import { Layout, Typography } from 'antd'
import './App.css'

interface State {
}

export default class App extends React.Component<any, State> {
  
  constructor(props: any) {
      super(props);
      this.state = {
      }
  }

  render() {
    return (
      <Layout style={{
        display: 'block',
        height: '100%', width: '100%'}}>
        <Layout.Header>
          <Typography.Title style={{color: 'white', textShadow: '1px 1px 3px black'}}>Face Detect</Typography.Title>
        </Layout.Header>
        <Layout.Content style={{width: '60%', height: '100%', margin: '0px auto'}}>
        </Layout.Content>
      </Layout>
    )
  }

}
