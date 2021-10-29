package protocols;

import com.google.common.base.Stopwatch;
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
public class OfflineShuffling {
    @Setter
    private PartyA partyA;

    @Setter
    private PartyB partyB;

    private BigInteger[] L0;
    private BigInteger[] L1;
    private BigInteger[] L2;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OfflineShuffling(){
    }


    public synchronized BigInteger[] getL2FromPartyB(int arraySize){

        Stopwatch stopwatch = Stopwatch.createStarted();
        getL0FromPartyB(arraySize);
        getL1PrimeFromPartyA();

        Assert.notNull(L1, "Party A must generate L1 first!" );

        L2 = partyB.getL2(L1);

        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.SECONDS);
     //   logger.info("======== Offline protocols cost time: " + mills + " s arraySize= " + L2.length + " ==========");
        return L2;
    }

    private BigInteger[] getL0FromPartyB(int arraySize){
        //logger.info("Offline protocols starting...!");
        L0 = partyB.getL0(arraySize);
        return L0;
    }

    private BigInteger[] getL1PrimeFromPartyA(){
        L1 = partyA.getL1Prime(L0);
        return L1;
    }

}
