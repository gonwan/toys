package main

import (
	"github.com/panjf2000/gnet"
	"go.uber.org/atomic"
	"log"
)

var (
	upstreamServers = []string{"10.188.10.75:8099", "10.188.10.76:8099", "10.188.10.77:8099"}
	//upstreamServers = []string{"www.baidu.com:80"}
)

type proxyServer struct {
	*gnet.EventServer
	Client *gnet.Client
	Index  atomic.Int32
}

func (ps *proxyServer) getUpstreamServer() string {
	idx := ps.Index.Load() % int32(len(upstreamServers))
	ps.Index.Inc()
	return upstreamServers[idx]
}

func newProxyServer() *proxyServer {
	cli, err := gnet.NewClient(&upstreamClient{},
		gnet.WithMulticore(true),
		gnet.WithTCPNoDelay(gnet.TCPNoDelay))
	if err != nil {
		log.Printf("Failed to create gnet client: %+v", err)
	}
	return &proxyServer{
		Client: cli,
	}
}

func (ps *proxyServer) React(packet []byte, c gnet.Conn) (out []byte, action gnet.Action) {
	requestPacket := append([]byte{}, packet...)
	upstreamServer := ps.getUpstreamServer()
	conn, err := ps.Client.Dial("tcp", upstreamServer) /* no close */
	if err != nil {
		log.Printf("Failed to connect to %s: %+v", upstreamServer, err)
		return nil, gnet.None
	}
	conn.SetContext(c) /* set context */
	err = ps.Client.Start()
	//defer ps.Client.Stop()
	if err != nil {
		log.Printf("Failed to start client to %s: %+v", upstreamServer, err)
		return nil, gnet.None
	}
	err = conn.AsyncWrite(requestPacket)
	if err != nil {
		log.Printf("Failed to write to %s: %+v", upstreamServer, err)
		return nil, gnet.Close
	}
	return nil, gnet.None
}
