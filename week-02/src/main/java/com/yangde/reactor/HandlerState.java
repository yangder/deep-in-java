/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yangdw
 */
public interface HandlerState {

    void changeState(TCPHandler h);

    void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolTaskExecutor pool) throws IOException;

}