package labs.psychogen.row.config;

import labs.psychogen.row.annotations.Filter;
import labs.psychogen.row.filter.RowFilter;
import labs.psychogen.row.filter.RowFilterChain;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class FilterScanner {
    private final RowFilterChain rowFilterChain;
    private volatile boolean init = false;

    public FilterScanner(RowFilterChain rowFilterChain) {
        this.rowFilterChain = rowFilterChain;
    }

    public void init(ApplicationContext applicationContext){
        if(init)
            return;
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Filter.class);
        beansWithAnnotation.forEach((beanName, bean) -> {
            processBean(bean);
        });
        init = true;
    }

    private void processBean(Object bean) {
        if(bean instanceof RowFilter){
            handle(bean);
        }
    }

    private void handle(Object bean) {
        Filter filter = bean.getClass().getAnnotation(Filter.class);
        switch (filter.type()) {
            case AFTER:
                rowFilterChain.addFilterAfter((RowFilter) bean, filter.after());
                break;
            case BEFORE:
                rowFilterChain.addFilterBefore((RowFilter) bean, filter.before());
                break;
        }
    }
}
