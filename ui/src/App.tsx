import React from 'react'
import { Layout, Typography, Button } from 'antd'
import './App.css'

interface State {
}

export default class App extends React.Component<any, State> {
  
  private canvasContext: CanvasRenderingContext2D | null = null
  constructor(props: any) {
      super(props)
      this.state = {
      }
  }

  componentDidMount() {
    let canvas = (this.refs.canvas as HTMLCanvasElement)
    this.canvasContext = canvas.getContext('2d')
    let video = (this.refs.video as HTMLVideoElement)
    const errHandle = 
    navigator.getUserMedia({'video': true},
      (stream) => {
        video.srcObject = stream
        video.play()
      }, 
      (err: MediaStreamError) => console.error(err))
  }

  drawImage() {
    if (this.canvasContext != null) {
      let video = (this.refs.video as HTMLVideoElement)
      this.canvasContext.drawImage(video, 0, 0, 500, 300);
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
          <video ref='video' width={500} height={300} style={{border: '1px black solid'}}></video>
          <canvas ref='canvas' width={500} height={300} style={{border: '1px black solid'}}></canvas>
          <Button type="primary">Capture</Button>
        </Layout.Content>
      </Layout>
    )
  }

}