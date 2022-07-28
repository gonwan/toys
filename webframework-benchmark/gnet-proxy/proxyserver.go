package main

import (
	"github.com/panjf2000/gnet"
	goPool "github.com/panjf2000/gnet/pkg/pool/goroutine"
	"go.uber.org/atomic"
	"log"
)

var (
	upstreamServers = [...]string{"172.16.56.137:8099", "172.16.56.138:8099", "172.16.56.139:8099"}
)

type proxyServer struct {
	*gnet.EventServer
	Client     *gnet.Client
	WorkerPool *goPool.Pool
	Index      atomic.Int32
}

func (ps *proxyServer) getUpstreamServer() string {
	idx := ps.Index.Load() % int32(len(upstreamServers))
	ps.Index.Inc()
	return upstreamServers[idx]
}

func newProxyServer() (srv *proxyServer, err error) {
	cli, err := gnet.NewClient(&upstreamClient{WorkerPool: goPool.Default()},
		gnet.WithMulticore(true),
		gnet.WithLockOSThread(true),
		gnet.WithTCPNoDelay(gnet.TCPNoDelay))
	if err != nil {
		log.Printf("Failed to create gnet client: %v", err)
		return
	}
	_ = cli.Start()
	srv = &proxyServer{
		Client:     cli,
		WorkerPool: goPool.Default(),
	}
	return
}

func (ps *proxyServer) React(packet []byte, c gnet.Conn) (out []byte, action gnet.Action) {
	requestPacket := append([]byte{}, packet...)
	upstreamServer := ps.getUpstreamServer()
	conn, err := ps.Client.Dial("tcp", upstreamServer) /* no close here */
	if err != nil {
		log.Printf("Failed to connect to %s: %v", upstreamServer, err)
		return nil, gnet.None
	}
	conn.SetContext(c) /* pass context */
	if err != nil {
		log.Printf("Failed to start client to %s: %v", upstreamServer, err)
		return nil, gnet.None
	}
	_ = ps.WorkerPool.Submit(func() {
		_ = conn.AsyncWrite(requestPacket)
	})
	//err = conn.AsyncWrite(requestPacket)
	//if err != nil {
	//	log.Printf("Failed to write to %s: %v", upstreamServer, err)
	//	return nil, gnet.Close
	//}
	return nil, gnet.None
}

func (ps *proxyServer) OnClosed(c gnet.Conn, err error) (action gnet.Action) {
	/* ignore... */
	return gnet.None
}
