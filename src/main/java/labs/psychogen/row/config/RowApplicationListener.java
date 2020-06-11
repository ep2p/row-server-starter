package labs.psychogen.row.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class RowApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final RowScanner rowScanner;

    public RowApplicationListener(RowScanner rowScanner) {
        this.rowScanner = rowScanner;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        rowScanner.init(contextRefreshedEvent.getApplicationContext());
    }
}
