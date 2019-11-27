const conf = require('./smipc_conf.json')
const smipc = require('smipc')
const { parentPort } = require('worker_threads')

class FrameReceiver {
    constructor(isTraceMode) {
        smipc.init(isTraceMode)
        if (!smipc.openChannel(conf.cid, smipc.CHAN_R, conf.chanSz)) {
            throw new Error(`Failed to open channel(${conf.cid}).`)
        }
    }

    readFrame() {
        let frame = {
            width: undefined,
            height: undefined,
            data: undefined
        }
        // read shape
        let buf = new Uint8Array(8)
        this._readN(buf, 8)
        frame.height = this._parseBytesToInt(buf, 0, 4)
        frame.width = this._parseBytesToInt(buf, 4, 4)
        if (frame.height == 0 && frame.width == 0) {
            return null
        }
        // read data
        let sz = frame.width * frame.height
        frame.data = new Uint8Array(sz)
        this._readN(frame.data, sz)
        return frame
    }

    _readN(buf, n) {
        let ret = smipc.readChannel(conf.cid, buf, n, true)
        if (ret < 0) {
            throw new Error(`Failed to read channel(${conf.cid}), ret=${ret}`);
        } else if (ret != n) {
            throw new Error(`Failed to read channel(${conf.cid}), ${n} bytes expected, ${ret} bytes read.`)
        }
    }

    _parseBytesToInt(buf, startPos, sz) {
        let v = 0
        for (let i = 0; i < sz; i++) {
            v += buf[startPos] << (i * 8)
            startPos += 1
        }
        return v
    }

    close() {
        if (!smipc.closeChannel(conf.cid)) {
            console.warn(`Failed to close channel(${conf.cid})`)
        }
        smipc.deinit()
    }
}

let fr = new FrameReceiver(true)
let frame
while ((frame = fr.readFrame()) != null) {
    parentPort.postMessage(frame)
}
fr.close()

console.log('Frame Transport stopped.')
