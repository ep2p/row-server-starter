package labs.psychogen.row.annotations;

import labs.psychogen.row.filter.RowInvokerFiler;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Filter {
    Type type() default Type.BEFORE;
    Class before() default RowInvokerFiler.class;
    Class after() default RowInvokerFiler.class;

    enum Type {
        BEFORE, AFTER
    }
}
