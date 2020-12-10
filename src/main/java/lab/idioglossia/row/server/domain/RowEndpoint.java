package lab.idioglossia.row.server.domain;

import lab.idioglossia.row.server.annotations.PostSubscribe;
import lab.idioglossia.row.server.annotations.PreSubscribe;
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
@ToString
public class RowEndpoint {
    private Object bean;
    private Method method;
    private RowMethod rowMethod;
    private String[] addresses;
    private Class produces;
    private Class query;
    private Class body;
    private String prefix = "";
    private int bodyIndex = -1;
    private int queryIndex = -1;
    private Map<String, Integer> pathVariables = new HashMap<>();
    private int parametersCount = 0;
    private String finalAddress;
    private PostSubscribe postSubscribe;
    private PreSubscribe preSubscribe;
    private int requestIndex = -1;
    private int responseIndex = -1;

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
