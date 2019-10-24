package main

import (
	log "github.com/Luncert/slog"
	"github.com/gwuhaolin/livego/av"
	"github.com/gwuhaolin/livego/protocol/rtmp/cache"
	cmap "github.com/orcaman/concurrent-map"
	"github.com/pkg/errors"
	"sync"
	"time"
)

const EmptyID = ""

// This function is used to read video stream from Rtmp,
// and then invoke AI function to process it. At last,
// Hls Server will publish the processed video stream.

type RtmpStreamHandler struct {
	channels cmap.ConcurrentMap //key
}

func NewAInvoker() *RtmpStreamHandler {
	ret := &RtmpStreamHandler{
		channels: cmap.New(),
	}
	go ret.CheckAlive()
	return ret
}

func (as *RtmpStreamHandler) CheckAlive() {
	for {
		<-time.After(5 * time.Second)
		for item := range as.channels.IterBuffered() {
			channel := item.Val.(*Channel)
			if channel.IsAlive() {
				as.channels.Remove(item.Key)
			}
		}
	}
}

// HandleReader receives a stream producer
func (as *RtmpStreamHandler) HandleReader(rc av.ReadCloser) {
	info := rc.Info()
	log.Info("HandleReader", info)

	var channel *Channel
	if tmp, ok := as.channels.Get(info.Key); ok {
		channel, _ = tmp.(*Channel)
		id := channel.ID()
		// check if this channel's producer is as same as the param
		if id != EmptyID && id != info.UID {
			channel.Pause()
			channel.producer = rc
		}
	} else {
		channel = NewChannel(info, rc)
		as.channels.Set(info.Key, channel)
	}
	channel.Start()
}

// HandleWriter receives a stream consumer
// this function will create a go-routine to read data from producer to consumer
func (as *RtmpStreamHandler) HandleWriter(wc av.WriteCloser) {
	info := wc.Info()
	log.Info("HandlerWriter", info)

	var channel *Channel
	if tmp, ok := as.channels.Get(info.Key); ok {
		channel, _ = tmp.(*Channel)
		channel.SetConsumer(wc)
	} else {
		log.Error("no channel found has id", info.Key)
	}
}

func (as *RtmpStreamHandler) GetWriter(info av.Info) av.WriteCloser {
	return nil
}

// TODO: handle av.Info
// Channel is a structure with only one writer and one reader
type Channel struct {
	stopSignal chan bool
	info       av.Info
	cache      *cache.Cache
	lock       *sync.Mutex
	producer   av.ReadCloser
	consumer   av.WriteCloser
}

func NewChannel(info av.Info, producer av.ReadCloser) *Channel {
	return &Channel{
		stopSignal: make(chan bool),
		info:       info,
		cache:      nil,
		lock:       new(sync.Mutex),
		producer:   producer,
		consumer:   nil,
	}
}

func (c *Channel) ID() string {
	if c.producer != nil {
		return c.producer.Info().UID
	}
	return EmptyID
}

func (c *Channel) SetConsumer(consumer av.WriteCloser) {
	c.lock.Lock()
	defer c.lock.Unlock()
	// consume cached packets
	if err := c.cache.Send(consumer); err != nil {
		log.Error(consumer.Info(), "send cache packet error:", err)
		c.consumer = nil
	} else {
		c.consumer = consumer
	}
}

// CheckAlive returns true if both of the writer and reader are died, else false
func (c *Channel) IsAlive() (alive bool) {
	if c.producer != nil && c.producer.Alive() {
		alive = true
	}
	if c.consumer != nil && c.consumer.Alive() {
		alive = true
	}
	return
}

func (c *Channel) Start() {
	go c.transport()
}

func (c *Channel) transport() {
	log.Info("Transport start:", c.info)

	// No StartStaticPush there, I think static push is to copy
	// the video stream and push it to pre-defined servers.
	// In this application, we don't need this function.

	var p av.Packet
	var err error
	for {
		select {
		case <-c.stopSignal:
			return
		default:
			if err = c.producer.Read(&p); err != nil {
				return
			}
			// output packet
			c.lock.Lock()
			if c.consumer == nil {
				if err = c.consumer.Write(&p); err != nil {
					log.Error(c.consumer.Info(), "write packet error:", err)
					c.consumer = nil
				} else {
					// write consumer succeed, no need to do cache
					c.lock.Unlock()
					continue
				}
			}
			// save packet to cache
			c.cache.Write(p)
			c.lock.Unlock()
		}
	}
}

func (c *Channel) Pause() {
	c.lock.Lock()
	c.stopSignal <- true
	c.lock.Unlock()

	log.Info("channel transport paused:", c.info)
}

func (c *Channel) Stop() {
	c.lock.Lock()
	c.stopSignal <- true
	// once send stopSignal succeed, transport must have exited
	// because c.stopSignal has no buffer

	if c.producer != nil {
		c.producer.Close(errors.New("stop old"))
		c.producer = nil
	}
	if c.consumer != nil {
		c.consumer.Close(errors.New("closed"))
		c.consumer = nil
	}
	log.Info("channel closed:", c.info)

	c.lock.Unlock()

	log.Info("channel transport stopped:", c.info)
}