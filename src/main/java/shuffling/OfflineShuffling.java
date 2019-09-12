package shuffling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

@Service
public class OfflineShuffling {
    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Value("${shuffle.arraySize}")
    private int arraySize;

    private BigInteger[] L0;
    private BigInteger[] L1;
    private BigInteger[] L2;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OfflineShuffling(){
    }

    /*@PostConstruct
    private void runOfflineShuffling(){
        getL0FromPartyB();
        getL1PrimeFromPartyA();
    }*/


    private BigInteger[] getL0FromPartyB(){
        logger.info("Offline shuffling starting...!");
        L0 = partyB.getL0(arraySize);
        return L0;
    }

    private BigInteger[] getL1PrimeFromPartyA(){
        L1 = partyA.getL1Prime(L0);
        return L1;
    }

    public BigInteger[] getL2FromPartyB(){
        getL0FromPartyB();
        getL1PrimeFromPartyA();
        if(L1 == null) {
            logger.error("Party A must generate L1 first!");
            return null;
        }
        L2 = partyB.getL2(L1);
        return L2;
    }

}
