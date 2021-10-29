package config;

import helper.SecureHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import paillier.PaillierPair;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages= {"protocols", "parties", "aops", "gc", "helper"})
@PropertySource(value={"classpath:application.properties"})
public class AppConfiguration {

    @Value("${paillier.keySize}")
    private int paillierKeySize;

    @Value("${party.bitSize}")
    private int bitSize;

    @Bean
    public PaillierPair getPaillierPair(){
        return new PaillierPair(paillierKeySize);
    }

    @Bean
    public SecureHelper getSecureHelper(){
        return new SecureHelper(bitSize);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
