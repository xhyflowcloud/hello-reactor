package net.lovenn.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {

    final Selector selector;

    final ServerSocketChannel ssc;


    public Acceptor(Selector selector, ServerSocketChannel ssc) {
        this.selector = selector;
        this.ssc = ssc;
    }

    public void run() {
        try {
            SocketChannel sc = ssc.accept();
            if(sc != null) {
                new Handler(selector, sc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
