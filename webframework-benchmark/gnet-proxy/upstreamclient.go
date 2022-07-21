package main

import (
	"github.com/panjf2000/gnet"
	"log"
)

type upstreamClient struct {
	*gnet.EventServer
}

func (uc *upstreamClient) OnInitComplete(svr gnet.Server) (action gnet.Action) {
	log.Printf("hahaha, %+v\n", svr.Addr)
	return
}

func (uc *upstreamClient) React(packet []byte, c gnet.Conn) (out []byte, action gnet.Action) {
	responsePacket := append([]byte{}, packet...)
	conn := c.Context().(gnet.Conn) // error??
	_ = conn.AsyncWrite(responsePacket)
	return nil, gnet.None
}
