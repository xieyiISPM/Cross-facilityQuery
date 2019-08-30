package config;

import helper.Helper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class testConfiguration {

    @Bean
    public Helper getHelper(){
        return new Helper();
    }

}
