package ua.ats.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.ats.dao.ProductRepository;

@Configuration
abstract public class AppConfig {

    @Bean
    abstract public ProductRepository productRepository();
}
