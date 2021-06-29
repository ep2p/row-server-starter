package io.ep2p.row.server.annotations;

import io.ep2p.row.server.event.PublishStrategy;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostSubscribe {
    String value(); //event
    PublishStrategy.Strategy strategy();
}
