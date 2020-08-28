package lab.idioglossia.row.annotations;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface RowController {
    @AliasFor(
            annotation = RestController.class
    )
    String value() default "";
}
