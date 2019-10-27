package main

import (
	"flag"
	"github.com/Luncert/FaceDetect/serv/backend/av"
	"github.com/Luncert/FaceDetect/serv/backend/rtmp"
	"github.com/Luncert/slog"
	"net"
	"os"
	"os/signal"
)

var (
	rtmpAddr = flag.String("rtmp-addr", ":1935", "RTMP server listen address")
)

func main() {
	flag.Parse()

	log.InitLogger("./logger.yml")
	defer log.DestroyLogger()

	handler := NewRtmpStreamHandler()
	defer handler.Close()
	startRtmp(handler)

	c := make(chan os.Signal)
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
