package main

import (
	"github.com/panjf2000/gnet"
)

type upstreamClient struct {
	*gnet.EventServer
}

func (uc *upstreamClient) OnInitComplete(svr gnet.Server) (action gnet.Action) {
	//log.Printf("Init: %v", svr.Addr)
	return
}

func (uc *upstreamClient) React(packet []byte, c gnet.Conn) (out []byte, action gnet.Action) {
	responsePacket := append([]byte{}, packet...)
	conn, ok := c.Context().(gnet.Conn)
	if ok {
		conn.AsyncWrite(responsePacket)
	}
	return nil, gnet.None
}

func (uc *upstreamClient) OnClosed(c gnet.Conn, err error) (action gnet.Action) {
	conn, ok := c.Context().(gnet.Conn)
	if ok {
		conn.Close()
	}
	return gnet.None
}
