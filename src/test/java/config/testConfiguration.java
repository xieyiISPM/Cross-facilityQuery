package config;

import helper.Helper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(appConfiguration.class)
@ComponentScan(basePackages = {"helper"})
public class testConfiguration {

    @Bean
    public Helper getHelper(){
        return new Helper();
    }

}
