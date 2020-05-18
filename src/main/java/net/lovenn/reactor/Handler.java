package net.lovenn.reactor;

import javax.annotation.processing.Processor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class Handler implements Runnable {

    final SelectionKey sk;

    final SocketChannel sc;

    static final int MAXIN = 10000, MAXOUT = 10000;

    ByteBuffer input = ByteBuffer.allocate(MAXIN);
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);
    static final int READING = 0, SENDING = 1, PROCESSING = 3;
    int state = READING;

    static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10,
                                      0L,TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());

    public Handler(Selector selector, SocketChannel sc) throws IOException {
        this.sc = sc;
        sc.configureBlocking(false);
        sk = sc.register(selector, SelectionKey.OP_READ, this);
        selector.wakeup();
    }

    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void read() throws IOException {
        sc.read(input);
        if (inputIsComplete()) {
            state = PROCESSING;
            pool.execute(new Processor());
        }
    }

    synchronized void send() throws IOException {
        sc.write(output);
        if (outputIsComplete()) {
            sk.cancel();
        }
    }

    private boolean inputIsComplete() {
        return true;
    }

    private boolean outputIsComplete() {
        return true;
    }

    private void process() { /* ... */ }

    synchronized void processAndHandOff() {
        process();
        state = SENDING; // or rebind attachment
        sk.interestOps(SelectionKey.OP_WRITE);
    }

    public class Processor implements Runnable{

        @Override
        public void run() {
            processAndHandOff();
        }
    }
}
