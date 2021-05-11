package com.jizhi.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 2021/3/12
 * jizhi7
 **/
public class NIOServer {

    public void javaNio() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9999));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, byteBuffer);
                    }
                    if (selectionKey.isReadable()) {
                        byteBuffer.clear();
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        channel.read(byteBuffer);
                        // 重置游标
                        byteBuffer.flip();
                        byte[] datas = new byte[byteBuffer.remaining()];
                        byteBuffer.get(datas);
                        System.out.println("read：" + new String(datas));
                        byteBuffer.clear();
                        channel.register(selector, SelectionKey.OP_WRITE, byteBuffer);
                    }
                    if (selectionKey.isWritable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        byteBuffer.put("hello word".getBytes());
                        if (channel.write(byteBuffer) != -1) {
                            System.out.println("write finnish!");
                        }
                        byteBuffer.clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


    public static void main(String[] args) {
        NIOServer server = new NIOServer();
        server.javaNio();
    }

}
