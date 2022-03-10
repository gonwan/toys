#!/usr/bin/pyton
import keyboard
import random
import time

while True:
    keyboard.send('caps lock')
    r = random.randint(480, 900)
    time.sleep(r)
