const conf = require('./config.json')
const net = require('net')
const { parentPort, Worker } = require('worker_threads')
const ipcMain = require('electron').ipcMain

// TODO: receive message from event server, 识别结果

class EventClient {
    connect(host, port) {
        this.cli = net.Socket()
        this.cli.connect(port, host, () => {
            console.debug('Connected to EventServer')
        })
    }

    emit(evtName, message) {
        if (!this.cli) {
            console.error('EventClient is not active.')
            return
        }
        if (!evtName || evtName.length == 0) {
            console.error('Invalid event name, must be non-empty.')
            return
        }
        this.cli.write(String.fromCharCode(evtName.length))
        this.cli.write(evtName)
        if (message) {
            this.cli.write(String.fromCharCode(message.length))
            this.cli.write(message)
        } else {
            this.cli.write(String.fromCharCode(0))
        }
    }

    // No close method, socket will be closed automatically
}

class AIService {
    start() {
        // TODO: create process to run event server
        this.evtCli = new EventClient()
        this.evtCli.connect(conf.eventServer.host, conf.eventServer.port)

        ipcMain.on('/ai/frameTransport/start', (evt, msg) => {
            this.evtCli.emit(conf.events.START_TRANSPORT, JSON.stringify(conf.smipc))

            // ft thread will exit gracefully when transport done.
            let ft = new Worker('./frame_transport.js')
            ft.on('message', (frame) => evt.sender.send('/ai/frameTransport/stream', frame))
        })

        ipcMain.on('/ai/frameTransport/stop', (evt, msg) => {
            this.evtCli.emit(conf.events.STOP_TRANSPORT)
        })
    }

    stop() {
        this.evtCli.emit(conf.events.STOP_SERVICE)
    }
}

module.exports = {
    AIService: AIService
}