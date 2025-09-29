package ramana.example.httprestserver.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ramana.example.httprestserver.Server;
import ramana.example.niotcpserver.Client;
import ramana.example.niotcpserver.handler.ChannelHandler;
import ramana.example.niotcpserver.handler.Context;
import ramana.example.niotcpserver.handler.impl.ChannelHandlerAdapter;
import ramana.example.niotcpserver.io.Allocator;
import ramana.example.niotcpserver.types.InternalException;
import ramana.example.niotcpserver.util.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class ClientTest {
    @BeforeAll
    static void startServer() {
        Server.run();
    }

    @Test
    void testGetGreeting() throws IOException {
        System.out.println("testGetGreeting:");
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/greeting").openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line = in.readLine()) != null) System.out.println(line);
        in.close();
        connection.disconnect();
    }

    @Test
    void testPostGreetingEcho() throws IOException {
        System.out.println("testPostGreetingEcho:");
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/greeting/echo").openConnection();
        connection.setDoOutput(true);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write("{\"id\":1,\"content\":\"Hello, World!\"}");
        out.flush();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line = in.readLine()) != null) System.out.println(line);
        in.close();
        connection.disconnect();
    }

    @Test
    void testGetGreetingUsingClient() {
        System.out.println("testGetGreetingUsingClient:");
        ChannelHandler handler = new ChannelHandlerAdapter() {
            @Override
            public void onConnect(Context.OnConnect context, Object data) throws InternalException {
                Allocator.Resource<ByteBuffer> resource = context.allocator().allocate(1024);
                ByteBuffer buffer = resource.get();
                String request = "GET /greeting HTTP/1.1\r\n" +
                        "Host:localhost\r\n" +
                        "User-Agent:NIOTCPServer/1.0\r\n" +
                        "Accept:*/*\r\n" +
                        "Accept-Encoding:gzip, deflate, br\r\n" +
                        "\r\n";
                buffer.put(request.getBytes());
                context.write(resource);
                context.flush();
                super.onConnect(context, data);
            }

            @Override
            public void onRead(Context.OnRead context, Object data) throws InternalException {
                System.out.println("Received: " + Util.toString(data));
                context.close();
                super.onRead(context, data);
            }
        };
        new Client().connect("localhost", 8080)
                .enableDefaultRead()
                .channelHandler(handler)
                .run();
    }
}
