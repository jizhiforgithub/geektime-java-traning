package com.jizhi.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 2021/3/12
 * jizhi7
 **/
public class NIOClient {

    public void nioClient() {
        try {
            SocketChannel channel = SocketChannel.open(new InetSocketAddress(9999));
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selector.select();
            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);

            while (true) {
                for (SelectionKey selectionKey : selector.selectedKeys()) {
                    if (selectionKey.isWritable()) {
                        buffer.put("response".getBytes("UTF-8"));
                        buffer.flip();
                        channel.write(buffer);
                        buffer.clear();
                    }
                    if (selectionKey.isReadable()) {
                        buffer.flip();
                        byte[] datas = new byte[buffer.remaining()];
                        // 读取数据到数组
                        buffer.get(datas);
                        System.out.println("from server : " + new String(datas, "UTF-8"));
                        // 清空缓存
                        buffer.clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NIOClient client = new NIOClient();
        client.nioClient();
    }

}
