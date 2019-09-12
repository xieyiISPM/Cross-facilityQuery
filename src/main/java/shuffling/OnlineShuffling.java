package shuffling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

@Service
public class OnlineShuffling {

    @Autowired
    PartyA partyA;

    @Autowired
    PartyB partyB;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BigInteger[] L3;

    public OnlineShuffling(){
        logger.info("Online shuffling started...");
    }

    public BigInteger[] generateL3ForPartyB(BigInteger[] partyBHalf){
        logger.info("Generate L3 array!");
        L3 = partyB.getL3(partyBHalf);
        return L3;
    }

    public BigInteger[] generateL4ForParyA(BigInteger[] partyAHalf){


        logger.info("Generate L4' array!");
        if(L3==null){
            logger.error("L3 is null, you must let partyB generate L3 first...");
            return null;
        }
        return partyA.getL4Prime(partyAHalf, L3);
    }
}
