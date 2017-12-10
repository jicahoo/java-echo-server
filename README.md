# java-echo-server
## CI Status
* [![Build Status](https://travis-ci.org/jicahoo/java-echo-server.svg?branch=master)](https://travis-ci.org/jicahoo/java-echo-server)
## Java echo server using NIO
* NIO is hard to use. Especially, ByteBuffer

## Quetions
1. What is attchment?
2. Is LT(Level Trigger) mode is the only choice. What about Edge Trigger?
3. NIO vs AIO?

## Test case
1. In telnet, Empty String
2. In telnet, input string whose size is bigger than buffer
3. After some echo, close terminal directly.

## NIO buffer operation
* flip: limit -> position, position -> 0
* compact: limit -> capacity, position -> limit - position, [position : (limit-1)] -> [0 : (limit - 1 - position)]
