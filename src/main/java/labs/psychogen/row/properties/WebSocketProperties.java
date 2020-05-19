package labs.psychogen.row.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "labs.psychogen.row.ws")
//todo: set default values
@Getter
@Setter
public class WebSocketProperties {
    private int maxBinaryBuffer;
    private int maxTextBuffer;
    private long maximumSessionIdle;
    private long maximumAsyncSendTimeout;
}
