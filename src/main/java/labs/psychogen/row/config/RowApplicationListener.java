package labs.psychogen.row.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class RowApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final RowScanner rowScanner;
    private final FilterScanner filterScanner;

    public RowApplicationListener(RowScanner rowScanner, FilterScanner filterScanner) {
        this.rowScanner = rowScanner;
        this.filterScanner = filterScanner;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        rowScanner.init(contextRefreshedEvent.getApplicationContext());
        filterScanner.init(contextRefreshedEvent.getApplicationContext());
    }
}
