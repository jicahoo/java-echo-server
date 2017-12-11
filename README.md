# java-echo-server
## CI Status
* [![Build Status](https://travis-ci.org/jicahoo/java-echo-server.svg?branch=master)](https://travis-ci.org/jicahoo/java-echo-server)
## Java echo server using NIO
* NIO is hard to use. Especially, ByteBuffer

## Quetions
1. What is attchment? A buffer used for BOTH of read and write.
2. Is LT(Level Trigger) mode is the only choice. What about Edge Trigger?
3. NIO vs AIO?

## Test case
1. In telnet, Empty String
2. In telnet, input string whose size is bigger than buffer
3. After some echo, close terminal directly.

## NIO buffer operation
* flip: limit -> position, position -> 0
* compact: limit -> capacity, position -> limit - position, [position : (limit-1)] -> [0 : (limit - 1 - position)]

## My Goal
* Use **CONCISE** code to implement an **CORRECT** and **HIGH-PERFORMANCE** echo server using **EVENT-DRIVEN**(epoll) model.
* What is CONCISE? 
    1. The less code, the good readibility. You can use any kind of language: Go, Python, Java, C.
* What is CORRECT? 
    1. Functionality is correct, for concurrent clients, it can also work well. 
    2. It is real EVENT-DRIVEN (use epoll correctly). 
    3. For boundary case, it can work well. Such as, empty string echo, large string echo, the client was killed.
* What is HIGH-PERFORMANCE? 
    1. From client, I can see the result as quick as possible; 
    2. From server, I use resource as less as possible, use less CPU&Memory resource.

## Errors you will come across
* "Connection reset by peer"
    1. https://stackoverflow.com/questions/1434451/what-does-connection-reset-by-peer-mean
## Key points
* How TCP peers start conversation?
* How TCP peers stop converations?
    1. FIN. "I finished talking to you, but I'll still listen to everything you have to say until you're done"
    2. RST. "There is no conversation. I am resetting the connection!"

