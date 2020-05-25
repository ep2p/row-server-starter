package labs.psychogen.row.domain.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
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
    private Double version = 1.0;
}
