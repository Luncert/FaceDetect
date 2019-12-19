const conf = require('./config.json').smipc
const smipc = require('smipc')
const { parentPort } = require('worker_threads')

let INT_SZ = 4

class FrameReceiver {
    constructor(isTraceMode) {
        smipc.init(isTraceMode)
        if (!smipc.openChannel(conf.cid, smipc.CHAN_R, conf.chanSz)) {
            throw new Error(`Failed to open channel(${conf.cid}).`)
        }
    }

    readFrame() {
        let frame = {}
        // read shape size
        let buf = new Uint8Array(INT_SZ)
        this._readN(buf, INT_SZ)
        let shapeSz = this._parseBytesToInt(buf, 0, INT_SZ)
        if (shapeSz == 0) {
            // received stop signal
            return null
        } else if (shapeSz != 3) {
            throw new Error(`Unsupported frame format, shapeSz=${shapeSz}.`)
        }
        // read shape
        buf = new Uint8Array(shapeSz * INT_SZ)
        this._readN(buf, buf.length)
        frame.height = this._parseBytesToInt(buf, 0, INT_SZ)
        frame.width = this._parseBytesToInt(buf, 4, INT_SZ)
        frame.pixelSize = this._parseBytesToInt(buf, 8, INT_SZ)
        if (frame.pixelSize != 4) {
            throw new Error(`Unsupported frame format, pixel size=${frame.pixelSize}.`)
        }
        // read data
        let sz = frame.width * frame.height * frame.pixelSize
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
        smipc.closeChannel(conf.cid)
        smipc.deinit()
    }
}

let fr = new FrameReceiver(smipc.LOG_ALL)
try {
    let frame
    while ((frame = fr.readFrame()) != null) {
        parentPort.postMessage(frame)
    }
} finally {
    fr.close()
    console.log('Frame Transport stopped.')
}
