package net.lovenn.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        Reactor reactor = new Reactor(8081);
        Thread thread = new Thread(reactor);
        //thread.setDaemon(true);
        thread.start();

        SocketChannel sc = SocketChannel.open();
        sc.socket().connect(new InetSocketAddress(8081));
        sc.configureBlocking(false);
        sc.write(ByteBuffer.wrap("hello world".getBytes()));

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
