package labs.psychogen.row.domain.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    @NotNull
    @NotEmpty
    private String id;
    @NotNull
    @NotEmpty
    private String method;
    @NotNull
    @NotEmpty
    private String address;
    private JsonNode query;
    private JsonNode body;
    private Map<String, String> header;
    private Double version = 1.0;
}
