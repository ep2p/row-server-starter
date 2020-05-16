package labs.psychogen.row;

import lombok.*;

import java.lang.reflect.Method;

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
    private int bodyIndex;
    private int queryIndex;

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
}
