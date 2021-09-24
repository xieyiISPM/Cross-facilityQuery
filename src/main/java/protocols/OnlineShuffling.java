package protocols;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class OnlineShuffling {

    @Setter
    PartyA partyA;

    @Setter
    PartyB partyB;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    BigInteger[] L3;
    @Getter
    BigInteger[] L4;

    public OnlineShuffling(){
        logger.info("Online protocols started...");
    }


    public void onLineShuffling(BigInteger[] partyBHalf, BigInteger[] partyAHalf){
        Stopwatch stopwatch = Stopwatch.createStarted();
        generateL3ForPartyB(partyBHalf);
        generateL4ForPartyA(partyAHalf);
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
       // logger.info("======== Online protocols cost time: " + mills + " ms arraySize = " + L3.length + " ========");
    }

    private void generateL3ForPartyB(BigInteger[] partyBHalf){
        //logger.info("Generate L3 array!");
        L3=null;
        Assert.isNull(L3, "L3 init should be null!");
        L3 = partyB.getL3(partyBHalf);
    }

    private void generateL4ForPartyA(BigInteger[] partyAHalf){
        //  logger.info("Generate L4' array!");
        Assert.notNull(L3, "L3 is null, you must let partyB generate L3 first...");
        L4=null;
        Assert.isNull(L4, "L4 init should be null!");
        L4 =  partyA.getL4Prime(partyAHalf, L3);
    }
}
