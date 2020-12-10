package lab.idioglossia.row.server.annotations;

import lab.idioglossia.row.server.filter.RowInvokerFiler;
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
