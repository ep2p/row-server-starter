package labs.psychogen.row.context;

import org.springframework.util.Assert;

public class ThreadLocalRowContextHolderStrategy implements RowContextHolderStrategy {
    private static final ThreadLocal<RowContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public RowContext getContext() {
        RowContext ctx = (RowContext) contextHolder.get();
        if (ctx == null) {
            ctx = this.createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    @Override
    public void setContext(RowContext rowContext) {
        Assert.notNull(rowContext, "Only non-null Row context instances are permitted");
        contextHolder.set(rowContext);
    }

    @Override
    public RowContext createEmptyContext() {
        return null;
    }
}
