package com.hello.echo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO: known issues in this vesion
 * 1. In client terminal, before ctrl+], ctrl+D.  Just close terminal, it will run into for-ever loop.
 */
public class RealNioEchoServer {
    public void server(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress addr = new InetSocketAddress(port);
        ss.bind(addr);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(100);
        while (true) {
            System.out.println("Loop");
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    System.out.println("Accepted connection from " + client);
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ, buffer);

                }

                if (key.isReadable()) {
                    System.out.println("[read]");
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer readAttach = (ByteBuffer) key.attachment();
                    if (readAttach == buffer) {
                        System.out.println("\tSame buffer.");
                    }
                    System.out.printf("\tChannel to read: %s\n", readAttach);
                    // Write data to readAttach buffer.
                    int rc = client.read(readAttach);
                    System.out.printf("\tChannel after read: %s\n", readAttach);
                    if (rc == -1) {
                        System.out.printf("\tClosed connection: %s\n", client);
                        client.close();
                    } else {
                        //If non-EOF, have to write it back. So register OP_WRITE
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
                    }
                }

                if (key.isValid() && key.isWritable()) {
                    System.out.println("[write]");
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer output = (ByteBuffer) key.attachment();
                    if (output == buffer) {
                        System.out.println("\tSame buffer");
                    }
                    System.out.printf("\tchannel to write: %s\n", output);
                    //flip in order to read data from the buffer.
                    output.flip();
                    System.out.printf("\tchannel to write after flip: %s\n", output);
                    if (!output.hasRemaining()) {
                        System.out.println("\tAll data (so far) are written to channel.");
                        client.register(selector, SelectionKey.OP_READ, buffer);
                    }
                    client.write(output);
                    System.out.printf("\tchannel to write after write: %s\n", output);
                    //Use compact to clear the data we have read.
                    //If not call it, we will read old data after flip.
                    output.compact();
                    System.out.printf("\tchannel to write after compact: %s\n", output);

                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new RealNioEchoServer().server(9999);
    }
}