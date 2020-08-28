package lab.idioglossia.row.annotations;

import lab.idioglossia.row.event.PublishStrategy;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostSubscribe {
    String value(); //event
    PublishStrategy.Strategy strategy();
}
