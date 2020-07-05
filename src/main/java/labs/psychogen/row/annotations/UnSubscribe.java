package labs.psychogen.row.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnSubscribe {
    String value(); //event
}
