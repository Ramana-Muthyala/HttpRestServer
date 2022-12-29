package ramana.example.httprestserver.util;

import org.springframework.web.bind.annotation.RequestMapping;
import ramana.example.httprestserver.codec.CodecRegistry;
import ramana.example.niotcpserver.codec.http.Util;
import ramana.example.niotcpserver.codec.http.request.v1.RequestMessage;
import ramana.example.niotcpserver.codec.http.response.ResponseMessage;

import java.util.ArrayList;

public class HttpUtil {
    public static final ArrayList<String> contentLengthZero = new ArrayList<>(1);
    public static final ArrayList<String> acceptedMediaTypes = new ArrayList<>(1);
    public static final ArrayList<String> allowedMethods = new ArrayList<>();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    static {
        contentLengthZero.add(String.valueOf(0));
        acceptedMediaTypes.add(CodecRegistry.APPLICATION_BY_JSON);
        allowedMethods.add("GET");
        allowedMethods.add("HEAD");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("DELETE");
        allowedMethods.add("OPTIONS");
        allowedMethods.add("TRACE");
        allowedMethods.add("PATCH");
    }

    public static void internalError(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_INTERNAL_SERVER_ERROR;
        responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, contentLengthZero);
    }

    public static void notFound(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_NOT_FOUND;
        responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, contentLengthZero);
    }

    public static void badRequest(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_BAD_REQUEST;
        responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, contentLengthZero);
    }
    
    public static String mediaType(RequestMessage requestMessage) {
        ArrayList<String> contentType = requestMessage.headers.get(CONTENT_TYPE);
        if(contentType != null  &&  !contentType.isEmpty()) return contentType.get(0);
        return CodecRegistry.APPLICATION_BY_JSON;
    }

    public static String mediaType(RequestMapping annotation) {
        return annotation.produces().length > 0
                ? annotation.produces()[0] : CodecRegistry.APPLICATION_BY_JSON;
    }

    public static void unsupportedMediaType(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_UNSUPPORTED_MEDIA_TYPE;
        responseMessage.headers.put(ACCEPT_ENCODING, acceptedMediaTypes);
        responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, contentLengthZero);
    }
}
