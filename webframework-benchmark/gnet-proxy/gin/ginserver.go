package main

import (
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
)

/*
 * cross build with:
 * # GOOS=linux GOARCH=amd64 go build gin/ginserver.go
 */
func main() {
	gin.SetMode(gin.ReleaseMode)
	r := gin.New()
	r.Use(func(c *gin.Context) {
		c.Writer.Header()["Date"] = nil
	})
	r.GET("/text", func(c *gin.Context) {
		c.String(http.StatusOK, "Hello, World!")
	})
	r.GET("/json", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"Message": "Hello, World!",
		})
	})
	addr := ":8099"
	log.Printf("Starting gin server at %s..", addr)
	err := r.Run(addr)
	if err != nil {
		log.Printf("Failed to start gin server: %v", err)
	}
}
