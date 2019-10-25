package main

import (
	"flag"
	"fmt"
	"github.com/gwuhaolin/livego/av"
	"github.com/gwuhaolin/livego/configure"
	"github.com/gwuhaolin/livego/protocol/rtmp"
	"net"
	"os"
	"os/signal"
	"time"

	"github.com/Luncert/slog"
)

var (
	rtmpAddr = flag.String("rtmp-addr", ":1935", "RTMP server listen address")
)

func main() {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("server panic: ", r)
			time.Sleep(1 * time.Second)
		}
	}()

	flag.Parse()

	log.InitLogger("./logger.yml")
	defer log.DestroyLogger()

	if err := configure.LoadConfig("config.json"); err != nil {
		log.Error(err)
		return
	}
	handler := NewRtmpStreamHandler()
	handler.Close()
	startRtmp(handler)

	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	<-c
}

func startRtmp(handler av.Handler) {
	rtmpListen, err := net.Listen("tcp", *rtmpAddr)
	if err != nil {
		log.Fatal(err)
	}

	rtmpServer := rtmp.NewRtmpServer(handler, nil)

	defer func() {
		if r := recover(); r != nil {
			log.Error("RTMP server panic: ", r)
		}
	}()

	log.Info("RTMP Listen On", *rtmpAddr)
	_ = rtmpServer.Serve(rtmpListen)
}
