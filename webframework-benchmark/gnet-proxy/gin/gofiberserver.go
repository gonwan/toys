package main

import (
	"github.com/gofiber/fiber/v2"
	"log"
)

func main() {
	app := fiber.New(fiber.Config{
		DisableDefaultDate: true,
	})
	app.Get("/text", func(c *fiber.Ctx) error {
		c.Set(fiber.HeaderContentType, fiber.MIMETextPlain)
		return c.SendString("Hello, World!")
	})
	app.Get("/json", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"Message": "Hello, World!",
		})
	})
	addr := ":8099"
	log.Printf("Starting fiber server at %s..", addr)
	err := app.Listen(addr)
	if err != nil {
		log.Printf("Failed to start fiber server: %v", err)
	}
}
