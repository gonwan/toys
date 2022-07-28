package main

import (
	"github.com/panjf2000/gnet"
	"log"
	"strings"
)

var (
	res []byte
)

func init() {
	str := `HTTP/1.1 200 OK
Content-Type: text/plain; charset=utf-8
Date: Fri, 22 Jul 2022 06:57:28 GMT
Content-Length: 13

Hello, World!`
	res = []byte(strings.ReplaceAll(str, "\n", "\r\n"))
}

type echoServer struct {
	gnet.EventServer
}

func (es *echoServer) React(frame []byte, c gnet.Conn) (out []byte, action gnet.Action) {
	out = res
	/* much slower if returns gnet.Close, 58w/s -> 5w/s on a 24c linux. */
	return
}

/*
 * cross build with:
 * # GOOS=linux GOARCH=amd64 go build gin/gnetserver.go
 */
func main() {
	echo := new(echoServer)
	log.Fatal(gnet.Serve(echo, "tcp://:8093", gnet.WithMulticore(true)))
}
