package main

import (
	"github.com/panjf2000/gnet"
	"log"
	"time"
)

/*
 * cross build with:
 * # GOOS=linux GOARCH=amd64 go build main.go
 */
func main() {
	proxyServer, err := newProxyServer()
	if err != nil {
		log.Printf("Failed to create proxy server: %v", err)
		return
	}
	addr := "tcp://:8093"
	err = gnet.Serve(proxyServer, addr,
		gnet.WithOptions(gnet.Options{Multicore: true, LockOSThread: true, TCPNoDelay: gnet.TCPNoDelay, TCPKeepAlive: time.Minute * 15}))
	if err != nil {
		log.Printf("Failed to start proxy server: %v", err)
		return
	}
	log.Printf("Proxy server is now running: %s", addr)
}
