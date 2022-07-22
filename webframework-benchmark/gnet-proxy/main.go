package main

import (
	"github.com/panjf2000/gnet"
	"log"
	"time"
)

/*
 * cross build with:
 * # GOOS=linux GOARCH=amd64 go build
 */
func main() {
	srv, err := newProxyServer()
	if err != nil {
		log.Printf("Failed to create proxy server: %v", err)
		return
	}
	addr := "tcp://:8093"
	log.Printf("Starting proxy server %s..", addr)
	err = gnet.Serve(srv, addr,
		gnet.WithOptions(gnet.Options{Multicore: true, LockOSThread: true, ReusePort: true, TCPNoDelay: gnet.TCPNoDelay, TCPKeepAlive: time.Minute * 15}))
	if err != nil {
		log.Printf("Failed to start proxy server: %v", err)
	}
}
