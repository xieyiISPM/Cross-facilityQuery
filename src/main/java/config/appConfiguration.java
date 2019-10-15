package config;

import helper.GeneralHelper;
import helper.SecureHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import paillier.PaillierPair;

@Configuration
@ComponentScan(basePackages= {"protocols", "parties", "aops"})
@PropertySource(value={"classpath:application.properties"})
public class appConfiguration {

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
