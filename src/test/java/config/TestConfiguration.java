package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppConfiguration.class/*,AsyncConfiguration.class*/})
@ComponentScan(basePackages = {"helper"})
public class TestConfiguration {

}
