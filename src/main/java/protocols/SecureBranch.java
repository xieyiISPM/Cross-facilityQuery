package protocols;

import aops.LogExecutionTime;
import com.google.common.base.Stopwatch;
import helper.GeneralHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class SecureBranch {

    @Value("${party.bitSize}")
    private int bitSize;

    @Value("${branch.arraySize}")
    private int branchArraySize;

    @Getter
    private BigInteger yOutputA;
    @Getter
    private BigInteger yOutputB;

    private BigInteger twoToL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    //@LogExecutionTime
    public void addAndCompare(BigInteger[] xA, BigInteger[]xB, BigInteger[] yA, BigInteger[]yB){
        Stopwatch stopwatch = Stopwatch.createStarted();
        if(xA.length != xB.length || yA.length!= yB.length || xA.length != yA.length || xA.length != 2){
            logger.error("X and Y must be size of 2.");
            new ArrayIndexOutOfBoundsException("Secure branch compare input array error.");
        }

        offlineShuffling.setArraySize(branchArraySize);
        BigInteger[] xBPrime = offlineShuffling.getL2FromPartyB();
        onlineShuffling.onLineShuffling(xB, xA);
        BigInteger[] xAPrime = onlineShuffling.getL4();

        offlineShuffling.setArraySize(branchArraySize);
        BigInteger[] yBPrime = offlineShuffling.getL2FromPartyB();
        onlineShuffling.onLineShuffling(yB, yA);
        BigInteger[] yAPrime = onlineShuffling.getL4();

        int theta = GeneralHelper.thetaHelper(xAPrime[0], xBPrime[0], xAPrime[1], xBPrime[1], twoToL);

        if(theta == 1){
            yOutputA = yAPrime[0];
            yOutputB = yBPrime[0];

        }
        else{
            yOutputA = yAPrime[1];
            yOutputB = yBPrime[1];
        }

        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== SecureBranch protocols cost time: " + mills + " ms arraySize= " + branchArraySize + " ==========");
    }

}
