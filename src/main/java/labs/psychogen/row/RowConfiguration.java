package labs.psychogen.row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowConfiguration {
    private final ApplicationContext ctx;
    private final List<RowEndpoint> rowEndpoints;

    @Autowired
    public RowConfiguration(ApplicationContext ctx) {
        this.ctx = ctx;
        this.rowEndpoints = new ArrayList<>();
    }


    @PostConstruct
    public void init(){
        Map<String, Object> allBeansWithNames = ctx.getBeansWithAnnotation(RowController.class);
        allBeansWithNames.forEach((beanName, bean) -> {
            processBean(bean);
        });
        rowEndpoints.forEach(System.out::println);
    }

    private void processBean(Object bean){
        String prefix = bean.getClass().getAnnotation(RowController.class).value();

        for (Method method : bean.getClass().getMethods()) {
            RowIgnore ignore = method.getAnnotation(RowIgnore.class);
            if(ignore != null)
                return;
            RowEndpoint rowEndpoint = getMethodAndAddress(method);
            if(rowEndpoint == null)
                return;
            setProduces(method, rowEndpoint);
            setBodyAndQuery(method, rowEndpoint);
            setPathVariables(method, rowEndpoint);
            if(!rowEndpoint.isValid())
                return;
            rowEndpoint.setMethod(method);
            rowEndpoint.setBean(bean);
            rowEndpoint.setPrefix(prefix);
            rowEndpoints.add(rowEndpoint);
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

    private void setProduces(Method method, RowEndpoint rowEndpoint) {
        rowEndpoint.setProduces(method.getReturnType());
    }

    private void setPathVariables(Method method, RowEndpoint rowEndpoint){
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if(pathVariable != null){
                rowEndpoint.getPathVariables().put(pathVariable.value(), i);
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

}