package lab.idioglossia.row.server.context;

import org.springframework.util.Assert;

public class InheritableThreadLocalRowContextHolderStrategy implements RowContextHolderStrategy {
    private static final InheritableThreadLocal<RowContext> contextHolder = new InheritableThreadLocal<RowContext>(){
        @Override
        protected RowContext initialValue() {
            return new DefaultContextImpl(new RowUser(), false);
        }
    };

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public RowContext getContext() {
        RowContext ctx = contextHolder.get();
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
        return new DefaultContextImpl(new RowUser(), false);
    }
}
