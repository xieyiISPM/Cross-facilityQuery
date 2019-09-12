package protocols;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

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

    private BigInteger[] getL0FromPartyB(){
        logger.info("Offline protocols starting...!");
        L0 = partyB.getL0(arraySize);
        return L0;
    }

    private BigInteger[] getL1PrimeFromPartyA(){
        L1 = partyA.getL1Prime(L0);
        return L1;
    }

    public BigInteger[] getL2FromPartyB(){

        Stopwatch stopwatch = Stopwatch.createStarted();
        getL0FromPartyB();
        getL1PrimeFromPartyA();

        Assert.notNull(L1, "Party A must generate L1 first!" );

        L2 = partyB.getL2(L1);

        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== Offline protocols cost time: " + mills + " ms arraySize= " + L2.length + " ==========");
        return L2;
    }

}
