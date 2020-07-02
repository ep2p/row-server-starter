package labs.psychogen.row.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "row.handler")
@Getter
@Setter
public class HandlerProperties {
    private boolean allowMultipleSessionsForSingleUser = true;
    private boolean trackHeartbeats = true;
}
