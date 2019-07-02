package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;

@Configuration
public class appConfiguration {

    @Bean
    public PaillierPair getPaillierPair(){
        int paillierKeySize = 1024;
        return new PaillierPair(paillierKeySize);
    }

    @Bean
    public PartyA getPartyA() {
        int bitSize = 5;
        return new PartyA(bitSize);
    }

    @Bean
    public PartyB getPartyB() {
        return new PartyB();
    }
}
