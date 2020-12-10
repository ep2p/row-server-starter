package lab.idioglossia.row.server.config;

import lab.idioglossia.row.server.domain.RowEndpoint;
import lab.idioglossia.row.annotations.*;
import lab.idioglossia.row.server.annotations.*;
import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.repository.EndpointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RowScanner {
    private final EndpointRepository endpointRepository;

    public RowScanner(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;
    }

    public void init(ApplicationContext applicationContext){
        Map<String, Object> allBeansWithNames = applicationContext.getBeansWithAnnotation(RowController.class);
        allBeansWithNames.forEach((beanName, bean) -> {
            processProxyBean(bean, true);
        });
        log.info("Registered all scanned endpoints");
    }

    public void processProxyBean(Object bean, boolean checkAnnotation){
        if(AopUtils.isAopProxy(bean)){
            Class<?> aClass = AopUtils.getTargetClass(bean);
            if (checkAnnotation && aClass.getAnnotation(RowController.class) == null) {
                return;
            }
            handleBean(bean, aClass);
        }
    }

    public void processBean(Object bean, Class aClass){
        handleBean(bean, aClass);
    }

    //todo: create bean handler pipeline
    private void handleBean(Object bean, Class<?> aClass) {
        String prefix = "";
        RowController rowControllerAnnotation = aClass.getAnnotation(RowController.class);
        if(rowControllerAnnotation != null)
            prefix = rowControllerAnnotation.value();

        for (Method method : aClass.getMethods()) {
            RowIgnore ignore = method.getAnnotation(RowIgnore.class);
            if(ignore != null)
                continue;
            RowEndpoint rowEndpoint = getMethodAndAddress(method);
            if(rowEndpoint == null)
                continue;
            if(!setProduces(method, rowEndpoint))
                continue;
            rowEndpoint.setParametersCount(method.getParameterCount());
            setBodyAndQuery(method, rowEndpoint);
            setPathVariables(method, rowEndpoint);
            setRequestResponseIndex(method, rowEndpoint);
            setSubscription(method, rowEndpoint);
            if(!rowEndpoint.isValid())
                continue;
            rowEndpoint.setMethod(method);
            rowEndpoint.setBean(bean);
            rowEndpoint.setPrefix(prefix);
            endpointRepository.addEndpoint(rowEndpoint);
        }
    }

    private void setBodyAndQuery(Method method, RowEndpoint rowEndpoint) {
        int i = 0;
        for (Parameter parameter : method.getParameters()) {
            for (Annotation annotation : parameter.getAnnotations()) {
                if(annotation instanceof RequestBody){
                    rowEndpoint.setBody(parameter.getType());
                    rowEndpoint.setBodyIndex(i);
                }
                if(annotation instanceof RowQuery){
                    rowEndpoint.setQuery(parameter.getType());
                    rowEndpoint.setQueryIndex(i);
                }
            }
            i++;
        }
    }

    private boolean setProduces(Method method, RowEndpoint rowEndpoint) {
        ResponseBody annotation1 = method.getAnnotatedReturnType().getAnnotation(ResponseBody.class);
        ResponseBody annotation2 = method.getAnnotation(ResponseBody.class);
        if(annotation2 == null && annotation1 == null)
            return false;
        rowEndpoint.setProduces(method.getReturnType());
        return true;
    }

    private void setPathVariables(Method method, RowEndpoint rowEndpoint){
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if(pathVariable != null){
                if(rowEndpoint.getPathVariables() == null)
                    rowEndpoint.setPathVariables(new HashMap<>());
                rowEndpoint.getPathVariables().put(pathVariable.value(), i);
            }
        }
    }

    private void setRequestResponseIndex(Method method, RowEndpoint rowEndpoint){
        for(int i = 0; i < method.getParameters().length; i++){
            Parameter parameter = method.getParameters()[i];
            if (parameter.getType().equals(RequestDto.class)) {
                rowEndpoint.setRequestIndex(i);
            }
            if (parameter.getType().equals(ResponseDto.class)) {
                rowEndpoint.setResponseIndex(i);
            }
        }
    }

    private RowEndpoint getMethodAndAddress(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if(annotation instanceof GetMapping){
                return new RowEndpoint(RowEndpoint.RowMethod.GET, ((GetMapping) annotation).value());
            }
            if(annotation instanceof PostMapping){
                return new RowEndpoint(RowEndpoint.RowMethod.POST, ((PostMapping) annotation).value());
            }
            if(annotation instanceof PatchMapping){
                return new RowEndpoint(RowEndpoint.RowMethod.PATCH, ((PatchMapping) annotation).value());
            }
            if(annotation instanceof PutMapping){
                return new RowEndpoint(RowEndpoint.RowMethod.PUT, ((PutMapping) annotation).value());
            }
            if(annotation instanceof DeleteMapping){
                return new RowEndpoint(RowEndpoint.RowMethod.DELETE, ((DeleteMapping) annotation).value());
            }
        }
        return null;
    }

    private void setSubscription(Method method, RowEndpoint rowEndpoint){
        PreSubscribe preSubscribe = method.getAnnotation(PreSubscribe.class);
        PostSubscribe postSubscribe = method.getAnnotation(PostSubscribe.class);
        rowEndpoint.setPostSubscribe(postSubscribe);
        rowEndpoint.setPreSubscribe(preSubscribe);
    }

}
