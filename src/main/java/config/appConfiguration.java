package config;

import helper.SecureHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;

@Configuration
@PropertySource(value={"classpath:application.properties"})
public class appConfiguration {

    @Value("${party.bitSize}")
    private int bitSize;

    @Bean
    public PaillierPair getPaillierPair(){
        int paillierKeySize = 1024;
        return new PaillierPair(paillierKeySize);
    }

    @Bean
    public PartyA getPartyA() {
        return new PartyA(bitSize);
    }

    @Bean
    public PartyB getPartyB() {
        return new PartyB(bitSize);
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
