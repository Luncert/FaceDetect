const net = require('net')


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
        if (evtName.length == 0) {
            console.error('Invalid event name, must be non-empty.')
            return
        }
        this.cli.write(String.fromCharCode(evtName.length))
        this.cli.write(evtName)
        this.cli.write(String.fromCharCode(message.length))
        this.cli.write(message)
    }
}

module.exports = {
    EventClient: EventClient
}