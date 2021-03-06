package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args) throws IOException {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();

        new Thread(()->{
            try {
                // 对应IO编程中服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8000));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true){
                    if (serverSelector.select(1) <= 0) {
                        Thread.sleep(100);
                        continue;
                    }

                    Set<SelectionKey> set = serverSelector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = set.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (!key.isAcceptable()) {
                            continue;
                        }

                        try {
                            // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                            SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                            InetSocketAddress remoteSocketAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
                            System.out.println(remoteSocketAddress.getPort());
                            System.out.println(remoteSocketAddress.getHostName());
                            System.out.println(remoteSocketAddress.getAddress());
                            clientChannel.configureBlocking(false);
                            clientChannel.register(clientSelector, SelectionKey.OP_READ);
                        } finally {
                            keyIterator.remove();
                        }
                    }

                }
            } catch (IOException | InterruptedException e){
                e.printStackTrace();
            }

        }).start();


        new Thread(()->{
            try {
                while (true){
                    // epoll 空轮询 bug 会导致 cpu 飙升 100%
                    if (clientSelector.select(1) <= 0) {
                        Thread.sleep(1000);
                        continue;
                    }
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为 1ms
                    Set<SelectionKey> set = clientSelector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = set.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        if (!key.isReadable()) {
                            continue;
                        }

                        try {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            // (3) 读取数据以块为单位批量读取
                            clientChannel.read(byteBuffer);
                            byteBuffer.flip();
                            CharsetDecoder charsetDecoder = Charset.defaultCharset().newDecoder();
                            System.out.println(charsetDecoder.decode(byteBuffer).toString());
                        } finally {
                            keyIterator.remove();
                            key.interestOps(SelectionKey.OP_READ);
                        }

                    }

                }
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }
}
