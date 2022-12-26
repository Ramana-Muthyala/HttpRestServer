package ramana.example.httprestserver;

import ramana.example.httprestserver.processor.RequestProcessor;
import ramana.example.niotcpserver.Bootstrap;
import ramana.example.niotcpserver.codec.http.handler.v1.CodecChannelHandler;
import ramana.example.niotcpserver.codec.http.handler.v1.ProcessorChannelHandler;
import ramana.example.niotcpserver.codec.http.v1.Processor;

public class Server {
    public static void run() {
        new Bootstrap().listen(8080)
                .enableDefaultRead()
                .numOfWorkers(4)
                .channelHandler(CodecChannelHandler.class)
                .channelHandler(ChannelHandler.class)
                .start();
    }

    public static class ChannelHandler extends ProcessorChannelHandler {
        private static final RequestProcessor requestProcessor = new RequestProcessor();
        @Override
        protected Processor create() {
            return requestProcessor;
        }
    }
}
