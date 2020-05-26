package labs.psychogen.row.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "row")
@Getter
@Setter
public class RowProperties {
    private boolean enable = true;
}
