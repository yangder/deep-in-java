/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author yangdw
 */
@Component
@Slf4j
public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Acceptor(Selector selector, ServerSocketChannel ssc) {
        this.ssc = ssc;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            //接收线程连接
            SocketChannel sc = ssc.accept();
            log.info(sc.socket().getRemoteSocketAddress().toString() + "> 已经连接");

            if (sc != null) {
                sc.configureBlocking(false);
                SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
                selector.wakeup();
                sk.attach(new TCPHandler(sk, sc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// 实现一个简单的多线程reactor模型的通信代码，能够实现不同客户端的连接，只需要实现服务端的代码，客户端通过telnet 或者 nc命令演示即可
}