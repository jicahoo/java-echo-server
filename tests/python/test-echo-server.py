#py27
import socket
import time

if __name__ == '__main__':
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', 9999))
    r = ""
    sr = ""
    for i in range(10):
        msg = "0123456789-%s" % i
        sr += msg
        print "send %s" % msg
        s.sendall(msg)
        #print msg == r
        #assert msg == r
    # shutdown gracefully, or server won't recieive EOF which will lead to 
    # exception.
    r = s.recv(1024)
    while r[-2:] != "-9":
        r += s.recv(1024)
    assert r == sr
    s.shutdown(socket.SHUT_RDWR)
    s.close()
