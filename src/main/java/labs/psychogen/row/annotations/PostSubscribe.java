package labs.psychogen.row.annotations;

import labs.psychogen.row.event.PublishStrategy;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostSubscribe {
    String value(); //event
    PublishStrategy.Strategy strategy();
}