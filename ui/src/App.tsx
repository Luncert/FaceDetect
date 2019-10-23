import React from 'react'
import { Layout, Typography, Button } from 'antd'
import config from './Config.json';
import './App.css'
const format = require('format') as any

interface State {
}

export default class App extends React.Component<any, State> {
  
  constructor(props: any) {
      super(props)
      this.state = {
      }
  }

  componentDidMount() {
    let canvas = (this.refs.canvas as HTMLCanvasElement)
    let canvasContext = canvas.getContext('2d') as CanvasRenderingContext2D

    let video = (this.refs.video as HTMLVideoElement)
    navigator.getUserMedia({video: true},
      (stream: MediaStream) => {
        // let serverConfig = {
        //   "iceServers": [
        //       { "urls": ["turn:192.168.3.65:3478"], 
        //       "username": "webrtc", 
        //       "credential": "turnpassword" 
        //       }
        //   ]
        // };
        // let localPeer = new RTCPeerConnection(serverConfig)
        // stream.getVideoTracks().forEach((track) => {
        //   localPeer.addTrack(track, stream)
        // })
        // // localPeer.addStream(stream)
        // localPeer.ontrack = (e: RTCTrackEvent) => {
        //   // bind video element
        //   let s = e.streams[0]
        //   if (video.srcObject !== s) {
        //   }
        // }
        video.srcObject = stream
        video.play()
        // upload stream to server
        // let ws = this.initWebSocket(
        //   () => {
            console.log('start send frames')
            setInterval(() => {
                canvasContext.drawImage(video, 0, 0, 800, 800)
                // TODO: check ws' status
                // ws.send(canvas.toDataURL('image/png', 1))
                console.log(canvas.toDataURL('image/png', 1))
            }, 2000) // 1 frame / second
          // }
        // )
      }, 
      (err: MediaStreamError) => console.error(err))

  }

  initWebSocket(onReady: () => void) {
    let ws = new WebSocket(format('ws://%s:%d/ws', config.server.host, config.server.port))
    ws.onopen = () => {
      console.log('websocket opened')
      onReady()
    }
    ws.onclose = () => console.log('websocket closed')
    return ws
  }

  handleData(frame: string) {

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
          <video ref='video' width={800} height={800} style={{border: '1px black solid'}}></video>
          <canvas ref='canvas' width={800} height={800} style={{display: 'block', border: '1px black solid'}}></canvas>
          {/* <Button type="primary" onClick={this.drawImage.bind(this)}>Capture</Button> */}
        </Layout.Content>
      </Layout>
    )
  }

}