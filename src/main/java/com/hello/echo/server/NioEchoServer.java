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

        //In this version, if client put more than 10 chars, there will be two round of select() return.
        //It means it is LT mode. Next step: read all data when received read event.
        int readableNumber = 0;
        int eventRnd = 0;
        while (true) {
            int evtNumber = selector.select();
            eventRnd++;
            System.out.printf("Got event count %s at round: %s \n", evtNumber, eventRnd);

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    System.out.println("\tisAcceptable.");
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    System.out.println("\tAccepted connection from " + client);
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ,
                            ByteBuffer.allocate(10));
                }

                if (key.isReadable()) {
                    readableNumber++;
                    System.out.println("\tisReadable: " + readableNumber);
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer wordsFromClient = (ByteBuffer) key.attachment();
                    channel.read(wordsFromClient);
                    wordsFromClient.flip();
                    byte[] bytes = new byte[wordsFromClient.remaining()];
                    wordsFromClient.get(bytes);
                    System.out.println("\t\t" + new String(bytes));
                    wordsFromClient.clear();
                    channel.write(ByteBuffer.wrap(bytes));
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
