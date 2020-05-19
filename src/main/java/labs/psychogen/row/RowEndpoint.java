package labs.psychogen.row;

import lombok.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RowEndpoint {
    private Object bean;
    private Method method;
    private RowMethod rowMethod;
    private String[] addresses;
    private Class produces;
    private Class query;
    private Class body;
    private String prefix;
    private int bodyIndex;
    private int queryIndex;
    private Map<String, Integer> pathVariables = new HashMap<>();

    public RowEndpoint(RowMethod rowMethod, String[] addresses) {
        this.rowMethod = rowMethod;
        this.addresses = addresses;
    }

    @Getter
    public enum RowMethod {
        GET("get"), POST("post"), FETCH("fetch"), PUT("put"), PATCH("patch"), DELETE("delete");
        private final String name;

        RowMethod(String name) {
            this.name = name;
        }
    }

    public boolean isValid(){
        return addresses != null && rowMethod != null && produces != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowEndpoint that = (RowEndpoint) o;
        return getRowMethod() == that.getRowMethod() &&
                Arrays.equals(getAddresses(), that.getAddresses());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getRowMethod());
        result = 31 * result + Arrays.hashCode(getAddresses());
        return result;
    }
}
