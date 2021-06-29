package io.ep2p.row.server.annotations;

import io.ep2p.row.server.config.PublisherProxyBeansRegistrar;
import io.ep2p.row.server.config.PublisherProxyConfig;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({PublisherProxyBeansRegistrar.class, PublisherProxyConfig.class})
public @interface EnableRowPublisher {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};
}
