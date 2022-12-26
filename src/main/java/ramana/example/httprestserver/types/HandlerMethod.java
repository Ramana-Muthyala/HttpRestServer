package ramana.example.httprestserver.types;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

public class HandlerMethod {
    public final Object bean;
    public final Method method;
    public final RequestMapping annotation;

    public HandlerMethod(Object bean, Method method, RequestMapping annotation) {
        this.bean = bean;
        this.method = method;
        this.annotation = annotation;
    }
}
