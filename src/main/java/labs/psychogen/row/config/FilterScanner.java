package labs.psychogen.row.config;

import labs.psychogen.row.annotations.Filter;
import labs.psychogen.row.filter.RowFilter;
import labs.psychogen.row.filter.RowFilterChain;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterScanner {
    private final RowFilterChain rowFilterChain;
    private volatile boolean init = false;
    private final List<Object> queueFilters = new ArrayList<>();

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
        Class clazz = filter.type().equals(Filter.Type.AFTER) ? filter.after() : filter.before();
        if(rowFilterChain.hasFilter(clazz)){
            register((RowFilter) bean, filter);
            dequeue();
        }else {
            addToQueue(bean);
        }
    }

    private void dequeue() {
        queueFilters.forEach(this::handle);
    }

    private void addToQueue(Object bean) {
        queueFilters.add(bean);
    }

    private void register(RowFilter rowFilter, Filter filter){
        switch (filter.type()) {
            case AFTER:
                rowFilterChain.addFilterAfter(rowFilter, filter.after());
                break;
            case BEFORE:
                rowFilterChain.addFilterBefore(rowFilter, filter.before());
                break;
        }
    }
}
