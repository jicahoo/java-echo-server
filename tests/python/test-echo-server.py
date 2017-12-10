#py27
import socket

if __name__ == '__main__':
    for i in range(10):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(('127.0.0.1',9999))
        msg = "Hello: %s" % i
        s.send(msg)
        r = s.recv(1024)
        print r
        print msg == r
        assert msg == r
        s.close()
