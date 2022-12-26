package ramana.example.httprestserver.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RestController;
import ramana.example.httprestserver.types.HandlerMethod;
import ramana.example.httprestserver.types.RequestMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HandlerMappings {
    private final HashMap<RequestMapping, HandlerMethod> handlerMethodMappings = new HashMap<>();
    private final HashMap<String, Set<String>> pathToMethodMappings = new HashMap<>();
    private final AnnotationConfigApplicationContext applicationContext;

    public HandlerMappings(AnnotationConfigApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initHandlerMethods();
    }

    protected void initHandlerMethods() {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(RestController.class).values();
        for (Object bean: beans) {
            Class<?> beanType = bean.getClass();
            org.springframework.web.bind.annotation.RequestMapping beanAnnotation = AnnotatedElementUtils.findMergedAnnotation(beanType, org.springframework.web.bind.annotation.RequestMapping.class);
            final String prefix = (beanAnnotation != null  &&  beanAnnotation.path().length > 0)
                    ? beanAnnotation.path()[0] : "";
            MethodIntrospector.selectMethods(beanType,
                    (MethodIntrospector.MetadataLookup<RequestMapping>) method -> {
                        org.springframework.web.bind.annotation.RequestMapping annotation = AnnotatedElementUtils.findMergedAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
                        if(annotation != null  &&  annotation.method().length > 0) {
                            String path = annotation.path().length > 0 ? annotation.path()[0] : "";
                            RequestMapping requestMapping = new RequestMapping(annotation.method()[0].name(), prefix + path);
                            handlerMethodMappings.put(requestMapping, new HandlerMethod(bean, method, annotation));
                            Set<String> methodsSet = pathToMethodMappings.computeIfAbsent(requestMapping.path, key -> new HashSet<>());
                            methodsSet.add(requestMapping.method);
                            return requestMapping;
                        }
                        return null;
                    });
        }
    }

    public HandlerMethod get(RequestMapping requestMapping) {
        return handlerMethodMappings.get(requestMapping);
    }

    public Set<String> get(String path) {
        return pathToMethodMappings.get(path);
    }
}
