package ramana.example.httprestserver.processor;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import ramana.example.httprestserver.codec.Codec;
import ramana.example.httprestserver.codec.CodecRegistry;
import ramana.example.httprestserver.util.HttpUtil;
import ramana.example.niotcpserver.codec.http.request.v1.RequestMessage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;

public class DataBinder {
    private static final HashSet<Class<?>> validAnnotations = new HashSet<>();
    static {
        validAnnotations.add(RequestParam.class);
        validAnnotations.add(RequestBody.class);
    }

    public Object[] resolve(Method handlerMethod, RequestMessage requestMessage) throws DataBindingException {
        Parameter[] parameters = handlerMethod.getParameters();
        Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation[] annotations = parameter.getAnnotations();
            Annotation annotation = checkAndGet(annotations);
            values[i] = resolve(parameter.getType(), annotation, requestMessage);
        }
        return values;
    }

    private Object resolve(Class<?> parameterType, Annotation annotation, RequestMessage requestMessage) throws DataBindingException {
        if(annotation instanceof RequestParam) {
            RequestParam requestParam = (RequestParam) annotation;
            return resolveRequestParam(requestParam, requestMessage.queryParameters);
        } else {
            RequestBody requestBody = (RequestBody) annotation;
            return resolveRequestBody(parameterType, requestBody, requestMessage);
        }
    }

    private Object resolveRequestBody(Class<?> type, RequestBody requestBody, RequestMessage requestMessage) throws DataBindingException {
        if(requestMessage.body == null  &&  requestBody.required()) throw new DataBindingException();
        String mediaType = HttpUtil.mediaType(requestMessage);
        Codec codec = CodecRegistry.get(mediaType);
        if(codec == null) throw new UnSupportedMediaType(mediaType);
        try {
            return codec.decode(requestMessage.body, type);
        } catch (Codec.CodecException e) {
            throw new DataBindingException(e);
        }
    }



    private Object resolveRequestParam(RequestParam requestParam, Map<String, String> queryParameters) throws DataBindingException {
        String name = requestParam.value();
        String value = queryParameters.get(name);
        if(ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())  &&  value == null) throw new DataBindingException();
        if(value == null) value = requestParam.defaultValue();
        return value;
    }

    private Annotation checkAndGet(Annotation[] annotations) throws DataBindingException {
        for (Annotation annotation: annotations) {
            if(validAnnotations.contains(annotation.annotationType())) return annotation;
        }
        throw new DataBindingException();
    }

    public static class DataBindingException extends Exception {
        public DataBindingException() {}
        public DataBindingException(Exception exception) {
            super(exception);
        }

        public DataBindingException(String message) {
            super(message);
        }
    }

    public static class UnSupportedMediaType extends DataBindingException {
        public UnSupportedMediaType(String message) {
            super(message);
        }
    }
}
