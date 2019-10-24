package main

import (
	"bytes"
	"encoding/base64"
	log "github.com/Luncert/slog"
	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"image"
	"image/png"
	"net/http"
)

// HTTPServer ...
type HTTPServer struct {
	addr   string
	router *mux.Router
	server *http.Server
}

// NewHTTPServer ...
func NewHTTPServer(addr string) *HTTPServer {
	r := mux.NewRouter()
	s := &http.Server{
		Addr:    addr,
		Handler: r,
	}
	return &HTTPServer{
		addr:   addr,
		router: r,
		server: s,
	}
}

var imgChan = make(chan image.Image, 1)

// Start ...
func (s *HTTPServer) Start() {
	s.router.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		if _, err := w.Write([]byte("Hi.")); err != nil {
			log.Error(err)
		}
	})
	s.router.HandleFunc("/ws", func(w http.ResponseWriter, r *http.Request) {
		serverWs(w, r)
	})
	log.Info("Server started at", s.addr)
	// ListenAndServe only returns
	if err := s.server.ListenAndServe(); err != nil {
		if err != http.ErrServerClosed {
			log.Fatal(err)
		}
	}
}

// Stop ...
func (s *HTTPServer) Stop() {
	if err := s.server.Close(); err != nil {
		log.Fatal("Stop server failed", err)
	} else {
		log.Info("Server stopped")
	}
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func serverWs(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Error(err)
		return
	}
	defer func() {
		_ = conn.Close()
	}()
	for {
		_, message, err := conn.ReadMessage()
		if err != nil {
			log.Error("read:", err)
			break
		}
		src := decodeDataUrl(message)
		img, err := png.Decode(bytes.NewReader(src))
		if err != nil {
			log.Fatal(err)
		} else {
			imgChan <- img
		}
		//err = conn.WriteMessage(msgType, message)
	}
}

func decodeDataUrl(raw []byte) []byte {
	i := 0
	for raw[i] != ',' {
		i++
	}
	i++
	src := raw[i:]
	maxLen := base64.StdEncoding.DecodedLen(len(src))
	dst := make([]byte, maxLen)
	if _, err := base64.StdEncoding.Decode(dst, src); err != nil {
		log.Fatal(err)
	}
	return dst
}
