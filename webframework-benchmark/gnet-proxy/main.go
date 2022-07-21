package main

import (
	"github.com/panjf2000/gnet"
	"log"
	"time"
)

func main() {
	proxyServer := newProxyServer()
	err := gnet.Serve(proxyServer, "tcp://:8093",
		gnet.WithOptions(gnet.Options{Multicore: true, TCPNoDelay: gnet.TCPNoDelay, TCPKeepAlive: time.Minute * 15}))
	if err != nil {
		log.Printf("Failed to create proxy server: %+v", err)
		return
	}
	log.Printf("Proxy server is now running...")
}
