package lab.idioglossia.row.server.config;

import lab.idioglossia.row.server.event.EventPublisherProxy;
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
