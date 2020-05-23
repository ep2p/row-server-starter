package labs.psychogen.row.config;

import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.SetEndpointRepository;
import labs.psychogen.row.service.DefaultEndpointProvider;
import labs.psychogen.row.service.EndpointProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableWebSocket
@ComponentScan("labs.psychogen.row")
public class RowConfiguration {
    private final ApplicationContext applicationContext;

    @Autowired
    public RowConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean(EndpointRepository.class)
    public EndpointRepository endpointRepository(){
        return new SetEndpointRepository();
    }

    @Bean
    @ConditionalOnMissingBean({RowScanner.class})
    @DependsOn({"endpointRepository"})
    public RowScanner rowScanner(EndpointRepository endpointRepository){
        return new RowScanner(applicationContext, endpointRepository);
    }

    @Bean
    @ConditionalOnMissingBean({EndpointProvider.class})
    @DependsOn({"endpointRepository"})
    public EndpointProvider endpointProvider(EndpointRepository endpointRepository){
        return new DefaultEndpointProvider(endpointRepository);
    }
}
