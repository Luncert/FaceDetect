const conf = require('./config.json')
const net = require('net')
const { Worker } = require('worker_threads')
const ipcMain = require('electron').ipcMain

const INT_SZ = 4

class AIService {
    socketClosed = false

    start() {
        // TODO: create process to run event server

        this.cli = net.Socket()

        this.cli.connect(conf.eventServer.port, conf.eventServer.host, () => {
            console.debug('Connected to EventServer')

            ipcMain.on('/ai/dataTransport/start', (evt, settings) => {
                settings = Object.assign(settings, conf.smipc)
                this._emitEvent(conf.events.START_TRANSPORT, JSON.stringify(settings))
    
                // ft thread will exit gracefully when transport done.
                let dt = new Worker('./data_transport.js')
                dt.on('message', (packet) => evt.sender.send('/ai/dataTransport/stream', packet))
            })
    
            ipcMain.on('/ai/dataTransport/stop', (evt, msg) => {
                this._emitEvent(conf.events.STOP_TRANSPORT)
            })
        })

        this.cli.on('error', (err) => {
            console.log('Error:', err.message)
        })

        this.cli.on('close', () => {
            console.debug('Disconnected from EventServer')

            this.socketClosed = true
        })
    }

    stop() {
        if (!this.socketClosed) {
            this._emitEvent(conf.events.STOP_SERVICE)
        }
    }

    _emitEvent(evtName, message) {
        if (this.socketClosed) {
            console.error('EventClient is not active.')
            return
        }
        if (!evtName || evtName.length == 0) {
            console.error('Invalid event name, must be non-empty.')
            return
        }

        message = message || ''

        let buf = new Uint8Array(INT_SZ)
        // send event name size
        this._parseIntToBytes(evtName.length, buf, 0, INT_SZ)
        this.cli.write(buf)
        // send event name
        this.cli.write(evtName)

        // send message size
        buf = new Uint8Array(INT_SZ)
        this._parseIntToBytes(message.length, buf, 0, INT_SZ)
        this.cli.write(buf)
        // send message
        this.cli.write(message)
    }

    _parseIntToBytes(v, buf, startPos, sz) {
        for (let i = 0; v > 0 && i < sz; i++, startPos++) {
            buf[startPos] = v & 0xff
            v >>= 8
        }
    }
}

module.exports = {
    AIService: AIService
}