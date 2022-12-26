package ramana.example.httprestserver.util;

import org.springframework.web.bind.annotation.RequestMapping;
import ramana.example.httprestserver.codec.CodecRegistry;
import ramana.example.niotcpserver.codec.http.Util;
import ramana.example.niotcpserver.codec.http.request.Field;
import ramana.example.niotcpserver.codec.http.request.v1.RequestMessage;
import ramana.example.niotcpserver.codec.http.response.ResponseMessage;

import java.util.ArrayList;

public class HttpUtil {
    public static final ArrayList<String> contentLengthZero = new ArrayList<>(1);
    public static final ArrayList<String> acceptedMediaTypes = new ArrayList<>(1);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    static {
        contentLengthZero.add(String.valueOf(0));
        acceptedMediaTypes.add(CodecRegistry.APPLICATION_BY_JSON);
    }
    public static final Field contentLengthZeroHeader = new Field(Util.REQ_HEADER_CONTENT_LENGTH, contentLengthZero);
    public static final Field acceptedMediaTypesHeader = new Field(ACCEPT_ENCODING, acceptedMediaTypes);

    public static final ArrayList<String> allowedMethods = new ArrayList<>();
    static {
        allowedMethods.add("GET");
        allowedMethods.add("HEAD");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("DELETE");
        allowedMethods.add("OPTIONS");
        allowedMethods.add("TRACE");
        allowedMethods.add("PATCH");
    }
    public static final Field optionsAllowedMethodsResponseHeader = new Field("Allow", allowedMethods);


    public static void internalError(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_INTERNAL_SERVER_ERROR;
        responseMessage.headers.add(contentLengthZeroHeader);
    }

    public static void notFound(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_NOT_FOUND;
        responseMessage.headers.add(contentLengthZeroHeader);
    }

    public static void badRequest(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_BAD_REQUEST;
        responseMessage.headers.add(contentLengthZeroHeader);
    }
    
    public static String mediaType(RequestMessage requestMessage) {
        for (Field field: requestMessage.headers) {
            if(CONTENT_TYPE.equals(field.name)  &&  !field.values.isEmpty()) return field.values.get(0);
        }
        return CodecRegistry.APPLICATION_BY_JSON;
    }

    public static String mediaType(RequestMapping annotation) {
        return annotation.produces().length > 0
                ? annotation.produces()[0] : CodecRegistry.APPLICATION_BY_JSON;
    }

    public static void unsupportedMediaType(ResponseMessage responseMessage) {
        responseMessage.statusCode = Util.STATUS_UNSUPPORTED_MEDIA_TYPE;
        responseMessage.headers.add(acceptedMediaTypesHeader);
        responseMessage.headers.add(contentLengthZeroHeader);
    }
}
