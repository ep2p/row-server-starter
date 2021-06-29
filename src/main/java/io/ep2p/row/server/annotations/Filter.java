package io.ep2p.row.server.annotations;

import io.ep2p.row.server.filter.RowInvokerFiler;
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
