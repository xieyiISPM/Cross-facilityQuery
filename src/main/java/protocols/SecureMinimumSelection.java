package protocols;

import helper.GeneralHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Service
public class SecureMinimumSelection{

    @Value("${party.bitSize}")
    private int bitSize;

    @Value("${minimum.selection.arraySize}")
    private int minimumSelectionArraySize;

    private BigInteger twoToL;

    @Getter
    private BigInteger xMinA;

    @Getter
    private BigInteger xMinB;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }


    public SecureMinimumSelection(){

    }

    public void getMini(BigInteger[] xA, BigInteger[] xB){
        if(xA.length != xB.length){
            logger.error("X and Y array size does not much!");
            new ArrayIndexOutOfBoundsException("Secure Minimum Selection input array error.");
        }

        int arraySize = xA.length;

        offlineShuffling.setArraySize(arraySize);
        BigInteger[] xBPrime = offlineShuffling.getL2FromPartyB();
        onlineShuffling.onLineShuffling(xB, xA);
        BigInteger[] xAPrime = onlineShuffling.getL4();

        BigInteger xDeltaA = xAPrime[0];
        BigInteger xDeltaB = xBPrime[0];

        for(int i=1; i< arraySize; i++){
            int theta = GeneralHelper.thetaHelper(xDeltaA, xDeltaB, xAPrime[i], xBPrime[i], twoToL);

            if(theta ==1){
                xDeltaA = xAPrime[i];
                xDeltaB = xBPrime[i];
            }
        }
        xMinA = xDeltaA;
        xMinB = xDeltaB;
    }


}
