package ramana.example.httprestserver.processor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ramana.example.httprestserver.codec.Codec;
import ramana.example.httprestserver.codec.CodecRegistry;
import ramana.example.httprestserver.types.HandlerMethod;
import ramana.example.httprestserver.types.RequestMapping;
import ramana.example.httprestserver.util.HandlerMappings;
import ramana.example.httprestserver.util.HttpUtil;
import ramana.example.niotcpserver.codec.http.Util;
import ramana.example.niotcpserver.codec.http.request.Field;
import ramana.example.niotcpserver.codec.http.request.v1.RequestMessage;
import ramana.example.niotcpserver.codec.http.response.ResponseMessage;
import ramana.example.niotcpserver.codec.http.v1.Processor;
import ramana.example.niotcpserver.log.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProcessor implements Processor {
    private static final String PROPERTIES_FILE = "/server.properties";
    private final Logger logger = LogFactory.getLogger();
    private final HandlerMappings handlerMappings;
    private Properties properties;
    private final DataBinder dataBinder = new DataBinder();
    private static final String PACKAGES_TO_SCAN = "packages.to.scan";
    private final PreProcessor preProcessor;

    public RequestProcessor() {
        readProperties();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(basePackages());
        handlerMappings = new HandlerMappings(applicationContext);
        preProcessor = new PreProcessor(handlerMappings);
    }

    private String[] basePackages() {
        String packagesToScan = properties.getProperty(PACKAGES_TO_SCAN).trim();
        if(packagesToScan.isEmpty()) throw new RuntimeException();
        StringTokenizer tokenizer = new StringTokenizer(packagesToScan, ",");
        int count = tokenizer.countTokens();
        String[] packagesToBeScanned = new String[count];
        for (int i = 0; i < count; i++) {
            packagesToBeScanned[i] = tokenizer.nextToken().trim();
        }
        return packagesToBeScanned;
    }

    private void readProperties() {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(RequestMessage requestMessage, ResponseMessage responseMessage) {
        if(preProcessor.process(requestMessage, responseMessage)) return;

        RequestMapping requestMapping = new RequestMapping(requestMessage.method, "/" + requestMessage.path);
        HandlerMethod handlerMethod = handlerMappings.get(requestMapping);
        if(handlerMethod == null) {
            HttpUtil.notFound(responseMessage);
            return;
        }

        try {
            Object[] paramValues = dataBinder.resolve(handlerMethod.method, requestMessage);
            Object result = handlerMethod.method.invoke(handlerMethod.bean, paramValues);
            Codec codec = CodecRegistry.get(HttpUtil.mediaType(handlerMethod.annotation));
            responseMessage.body = codec.encode(result);
        } catch (DataBinder.UnSupportedMediaType exception) {
            HttpUtil.unsupportedMediaType(responseMessage);
            return;
        } catch (DataBinder.DataBindingException exception) {
            logger.log(Level.INFO, exception.getMessage(), exception);
            HttpUtil.badRequest(responseMessage);
            return;
        } catch (Exception exception) {
            logger.log(Level.INFO, exception.getMessage(), exception);
            HttpUtil.internalError(responseMessage);
            return;
        }

        responseMessage.statusCode = Util.STATUS_OK;
        ArrayList<String> values = new ArrayList<>(1);
        values.add(String.valueOf(responseMessage.body.length));
        responseMessage.headers.add(new Field(Util.REQ_HEADER_CONTENT_LENGTH, values));
    }
}
