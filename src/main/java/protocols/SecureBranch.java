package protocols;

import aops.LogExecutionTime;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import parties.PartyA;
import parties.PartyB;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

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
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    @LogExecutionTime
    public void addAndCompare(BigInteger[] xA, BigInteger[]xB, BigInteger[] yA, BigInteger[]yB){
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

        int theta = thetaHelper(xAPrime[0], xBPrime[0], xAPrime[1], xBPrime[1]);

        if(theta == 1){
            yOutputA = yAPrime[0];
            yOutputB = yBPrime[0];

        }
        else{
            yOutputA = yAPrime[1];
            yOutputB = yBPrime[1];
        }
    }



    private int thetaHelper(BigInteger x0A,BigInteger x0B, BigInteger x1A, BigInteger x1B){
        int result = (x0A.add(x0B).mod(twoToL)).compareTo(x1A.add(x1B).mod(twoToL));
        if(result >=0){
            return 1;
        }
        else return 0;
    }


}
