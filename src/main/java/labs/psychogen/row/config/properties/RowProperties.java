package labs.psychogen.row.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "row")
@Getter
@Setter
public class RowProperties {
    private boolean enable = true;
    private int publisherQueue = 10;
    private int publisherMaxPoolSize = 20;
    private int publisherCorePoolSize = 5;
}
