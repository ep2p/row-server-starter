package lab.idioglossia.row.config;

import lab.idioglossia.row.event.EventPublisherProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class PublisherProxyBeanFactory {
    @Autowired
    EventPublisherProxy eventPublisherProxy;

    @SuppressWarnings("unchecked")
    public <PS> PS createPublisherProxyBean(ClassLoader classLoader, Class<PS> clazz) {
        return (PS) Proxy.newProxyInstance(classLoader, new Class[] {clazz}, eventPublisherProxy);
    }
}
