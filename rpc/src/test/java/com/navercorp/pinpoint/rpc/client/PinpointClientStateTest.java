/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.navercorp.pinpoint.rpc.common.SocketStateCode;
import com.navercorp.pinpoint.rpc.server.DefaultPinpointServer;
import com.navercorp.pinpoint.rpc.server.PinpointServer;
import com.navercorp.pinpoint.rpc.server.PinpointServerAcceptor;
import com.navercorp.pinpoint.rpc.util.PinpointRPCTestUtils;

/**
 * @author Taejin Koo
 */
public class PinpointClientStateTest {

    private static int bindPort;

    @BeforeClass
    public static void setUp() throws IOException {
        bindPort = PinpointRPCTestUtils.findAvailablePort();
    }

    @Test
    public void connectFailedStateTest() throws InterruptedException {
        PinpointSocketFactory clientSocketFactory = null;
        PinpointSocketHandler handler = null;
        try {
            clientSocketFactory = PinpointRPCTestUtils.createSocketFactory(PinpointRPCTestUtils.getParams(), PinpointRPCTestUtils.createEchoClientListener());
            handler = connect(clientSocketFactory);

            Thread.sleep(2000);

            Assert.assertEquals(SocketStateCode.CONNECT_FAILED, handler.getCurrentStateCode());
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
        }
    }

    @Test
    public void closeStateTest() throws InterruptedException {
        PinpointServerAcceptor serverAcceptor = null;
        PinpointSocketFactory clientSocketFactory = null;
        PinpointSocketHandler handler = null;
        try {
            serverAcceptor = PinpointRPCTestUtils.createPinpointServerFactory(bindPort, PinpointRPCTestUtils.createEchoServerListener());

            clientSocketFactory = PinpointRPCTestUtils.createSocketFactory(PinpointRPCTestUtils.getParams(), PinpointRPCTestUtils.createEchoClientListener());
            handler = connect(clientSocketFactory);
            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.RUN_DUPLEX, handler.getCurrentStateCode());
            handler.close();

            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.CLOSED_BY_CLIENT, handler.getCurrentStateCode());
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    @Test
    public void closeByPeerStateTest() throws InterruptedException {
        PinpointServerAcceptor serverAcceptor = null;
        PinpointSocketFactory clientSocketFactory = null;
        PinpointSocketHandler handler = null;
        try {
            serverAcceptor = PinpointRPCTestUtils.createPinpointServerFactory(bindPort, PinpointRPCTestUtils.createEchoServerListener());

            clientSocketFactory = PinpointRPCTestUtils.createSocketFactory(PinpointRPCTestUtils.getParams(), PinpointRPCTestUtils.createEchoClientListener());
            handler = connect(clientSocketFactory);
            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.RUN_DUPLEX, handler.getCurrentStateCode());
            serverAcceptor.close();

            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.CLOSED_BY_SERVER, handler.getCurrentStateCode());
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    @Test
    public void unexpectedCloseStateTest() throws InterruptedException {
        PinpointServerAcceptor serverAcceptor = null;
        PinpointSocketFactory clientSocketFactory = null;
        PinpointSocketHandler handler = null;
        try {
            serverAcceptor = PinpointRPCTestUtils.createPinpointServerFactory(bindPort, PinpointRPCTestUtils.createEchoServerListener());

            clientSocketFactory = PinpointRPCTestUtils.createSocketFactory(PinpointRPCTestUtils.getParams(), PinpointRPCTestUtils.createEchoClientListener());
            handler = connect(clientSocketFactory);
            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.RUN_DUPLEX, handler.getCurrentStateCode());
            clientSocketFactory.release();

            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.UNEXPECTED_CLOSE_BY_CLIENT, handler.getCurrentStateCode());
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    @Test
    public void unexpectedCloseByPeerStateTest() throws InterruptedException {
        PinpointServerAcceptor serverAcceptor = null;
        PinpointSocketFactory clientSocketFactory = null;
        PinpointSocketHandler handler = null;
        try {
            serverAcceptor = PinpointRPCTestUtils.createPinpointServerFactory(bindPort, PinpointRPCTestUtils.createEchoServerListener());

            clientSocketFactory = PinpointRPCTestUtils.createSocketFactory(PinpointRPCTestUtils.getParams(), PinpointRPCTestUtils.createEchoClientListener());
            handler = connect(clientSocketFactory);
            Thread.sleep(1000);

            List<PinpointServer> pinpointServerList = serverAcceptor.getWritableServerList();
            PinpointServer pinpointServer = pinpointServerList.get(0);
            Assert.assertEquals(SocketStateCode.RUN_DUPLEX, handler.getCurrentStateCode());

            ((DefaultPinpointServer) pinpointServer).stop(true);

            Thread.sleep(1000);

            Assert.assertEquals(SocketStateCode.UNEXPECTED_CLOSE_BY_SERVER, handler.getCurrentStateCode());
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    private PinpointSocketHandler connect(PinpointSocketFactory factory) {
        ChannelFuture future = factory.reconnect(new InetSocketAddress("127.0.0.1", bindPort));
        SocketHandler handler = getSocketHandler(future, new InetSocketAddress("127.0.0.1", bindPort));
        return (PinpointSocketHandler) handler;
    }

    SocketHandler getSocketHandler(ChannelFuture channelConnectFuture, SocketAddress address) {
        if (address == null) {
            throw new NullPointerException("address");
        }

        Channel channel = channelConnectFuture.getChannel();
        SocketHandler socketHandler = (SocketHandler) channel.getPipeline().getLast();
        socketHandler.setConnectSocketAddress(address);

        return socketHandler;
    }

    private void closeHandler(PinpointSocketHandler handler) {
        if (handler != null) {
            handler.close();
        }
    }

    private void closeSocketFactory(PinpointSocketFactory factory) {
        if (factory != null) {
            factory.release();
        }
    }

}
