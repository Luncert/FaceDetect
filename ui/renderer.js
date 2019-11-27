  
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

function setPixel(img, frame, x, y) {
    let index = x + y * img.width
    let v = frame.data[index]
    index *= 4
    img.data[index + 0] = v
    img.data[index + 1] = v
    img.data[index + 2] = v
    img.data[index + 3] = 255
}

ipcRenderer.on('frame', (evt, frame) => {
    let img = ctx.createImageData(frame.width, frame.height)
    for (let y = 0; y < frame.height; y++) {
        for (let x = 0; x < frame.width; x++) {
            setPixel(img, frame, x, y)
        }
    }
    ctx.putImageData(img, 0, 0)
})