package lab.idioglossia.row.server.annotations;

import lab.idioglossia.row.server.event.PublishStrategy;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostSubscribe {
    String value(); //event
    PublishStrategy.Strategy strategy();
}
