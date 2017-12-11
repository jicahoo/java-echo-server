#py27
import socket
import time

if __name__ == '__main__':
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', 9999))
    for i in range(10):
        msg = "012-%s" % i
        print "send %s" % msg
        s.sendall(msg)
        r = s.recv(1024)
        print r
        #print msg == r
        assert msg == r
    # shutdown gracefully, or server won't recieive EOF which will lead to 
    # exception.
    s.shutdown(socket.SHUT_RDWR)
    s.close()
