package labs.psychogen.row.config;

import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.SetEndpointRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RowConfiguration {
    @Bean
    @ConditionalOnMissingBean(EndpointRepository.class)
    public EndpointRepository endpointRepository(){
        return new SetEndpointRepository();
    }
}
