package labs.psychogen.row.context;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

public class RowContextHolder {
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
//    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
//    public static final String MODE_GLOBAL = "MODE_GLOBAL";
    public static final String SYSTEM_PROPERTY = "spring.security.strategy";
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
    private static RowContextHolderStrategy strategy;
    private static int initializeCount = 0;

    public RowContextHolder() {
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static RowContext getContext() {
        return strategy.getContext();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    private static void initialize() {
        if (!StringUtils.hasText(strategyName)) {
            strategyName = MODE_THREADLOCAL;
        }

        if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalRowContextHolderStrategy();
        } else {
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (RowContextHolderStrategy) customStrategy.newInstance();
            } catch (Exception var2) {
                ReflectionUtils.handleReflectionException(var2);
            }
        }

        ++initializeCount;
    }

    public static void setContext(RowContext context) {
        strategy.setContext(context);
    }

    public static void setStrategyName(String strategyName) {
        RowContextHolder.strategyName = strategyName;
        initialize();
    }

    public static RowContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public static RowContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public String toString() {
        return "RowContextHolder[strategy='" + strategyName + "'; initializeCount=" + initializeCount + "]";
    }

    static {
        initialize();
    }
}
