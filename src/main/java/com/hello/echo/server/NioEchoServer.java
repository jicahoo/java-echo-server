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

public class NioEchoServer {
    public void server(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress addr = new InetSocketAddress(port);
        ss.bind(addr);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        //bug:
        // telnet and type some words. Then close connection from cient, server will goes into dead loop.
        while (true) {
            System.out.println("Before select");
            int evtNumber = selector.select();
            System.out.println("After select");
            System.out.println("Event number: " + evtNumber);

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                System.out.println("Has more event.");
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    System.out.println("isAcceptable.");
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    System.out.println("Accepted connection from " + client);
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ,
                            ByteBuffer.allocate(100));
                }

                if (key.isReadable()) {
                    System.out.println("isReadable");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer output = (ByteBuffer) key.attachment();
                    channel.read(output);
                    System.out.println(output.toString());
                }

                if (key.isWritable()) {
                    System.out.println("isWritable");
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer output = (ByteBuffer) key.attachment();
                    output.flip();
                    client.write(output);
                    output.compact();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioEchoServer().server(9999);
    }
}
