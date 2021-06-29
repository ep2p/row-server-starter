package io.ep2p.row.server.config;

import io.ep2p.row.server.annotations.Filter;
import io.ep2p.row.server.filter.RowFilter;
import io.ep2p.row.server.filter.RowFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("Scanning row filters");
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Filter.class);
        beansWithAnnotation.forEach((beanName, bean) -> {
            processBean(bean);
        });
        log.info("Finished scanning row filters");
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
