package ramana.example.httprestserver.processor;

import ramana.example.httprestserver.util.HandlerMappings;
import ramana.example.httprestserver.util.HttpUtil;
import ramana.example.niotcpserver.codec.http.Util;
import ramana.example.niotcpserver.codec.http.request.v1.RequestMessage;
import ramana.example.niotcpserver.codec.http.response.ResponseMessage;

import java.util.ArrayList;
import java.util.Set;

public class PreProcessor {
    private final HandlerMappings handlerMappings;

    public PreProcessor(HandlerMappings handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public boolean process(RequestMessage requestMessage, ResponseMessage responseMessage) {
        if(Util.METHOD_OPTIONS.equals(requestMessage.method)) {
            if("*".equals(requestMessage.path)) {
                responseMessage.statusCode = Util.STATUS_NO_CONTENT;
                responseMessage.headers.put("Allow", HttpUtil.allowedMethods);
                responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, HttpUtil.contentLengthZero);
            } else {
                Set<String> allowedMethodsForPath = handlerMappings.get("/" + requestMessage.path);
                if(allowedMethodsForPath == null) {
                    HttpUtil.notFound(responseMessage);
                } else {
                    responseMessage.statusCode = Util.STATUS_NO_CONTENT;
                    addAllowedMethods(allowedMethodsForPath, responseMessage);
                    responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, HttpUtil.contentLengthZero);
                }
            }
            return true;
        }

        Set<String> allowedMethodsForPath = handlerMappings.get("/" + requestMessage.path);
        if(allowedMethodsForPath != null  &&  !allowedMethodsForPath.contains(requestMessage.method)) {
            responseMessage.statusCode = Util.STATUS_METHOD_NOT_ALLOWED;
            addAllowedMethods(allowedMethodsForPath, responseMessage);
            responseMessage.headers.put(Util.REQ_HEADER_CONTENT_LENGTH, HttpUtil.contentLengthZero);
            return true;
        }

        return false;
    }

    private void addAllowedMethods(Set<String> allowedMethodsForPath, ResponseMessage responseMessage) {
        ArrayList<String> tmp = new ArrayList<>(allowedMethodsForPath);
        tmp.add("OPTIONS");
        responseMessage.headers.put("Allow", tmp);
    }
}
