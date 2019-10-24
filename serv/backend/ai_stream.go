package main

import "github.com/gwuhaolin/livego/av"

// This function is used to read video stream from Rtmp,
// and then invoke AI function to process it. At last,
// Hls Server will publish the processed video stream.

type AIStream struct {
}

func NewAIStream() *AIStream {
	return &AIStream{}
}

func (s *AIStream) GetWriter(info av.Info) av.WriteCloser {
	return nil
}

func (s *AIStream) HandleReader(rc av.ReadCloser) {

}

func (s *AIStream) HandleWriter(wc av.WriteCloser) {

}
