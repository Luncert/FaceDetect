  
// This file is required by the index.html file and will
// be executed in the renderer process for that window.
// No Node.js APIs are available in this process because
// `nodeIntegration` is turned off. Use `preload.js` to
// selectively enable features needed in the rendering
// process.

const ipcRenderer = require('electron').ipcRenderer

let canvas = document.getElementById('video_canvas')
canvas.width = 800
canvas.height = 600
let ctx = canvas.getContext('2d')

ipcRenderer.on('frame', (evt, frame) => {
    let img = new ImageData(Uint8ClampedArray.from(frame.data), frame.width, frame.height)
    ctx.putImageData(img, 0, 0)
})
