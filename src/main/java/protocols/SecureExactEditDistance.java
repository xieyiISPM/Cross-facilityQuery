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
public class SecureExactEditDistance {

    @Value("${party.bitSize}")
    private int bitSize;

    private BigInteger twoToL;
    @Getter
    private BigInteger dEDA;

    @Getter
    private BigInteger dEDB;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecureBranch sb;

    @Autowired
    private SecureMinimumSelection sms;



    public SecureExactEditDistance(){

    }

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    @LogExecutionTime
    public void  getExactEditDistance(BigInteger[] xA, BigInteger[] xB, BigInteger[] yA, BigInteger[]yB){
        Stopwatch stopwatch = Stopwatch.createStarted();

        if (xA.length != xB.length || yA.length != yB.length){
            logger.error("X Half or Y Half array size does not much!");
            throw new IllegalArgumentException("Secure Edit Distance input array error."); // shared sequence between two parties has to be match
        }

        int n1= xA.length;
        int n2 = yA.length;
        if( twoToL.compareTo(BigInteger.valueOf(GeneralHelper.getMax(n1, n2))) < 0){
            logger.error("2^l should be larger than max(xA.length, yA.length)!");
            throw new IllegalArgumentException();
        }


        BigInteger[][] deltaA = new BigInteger[n1+1][n2+1];
        BigInteger[][] deltaB = new BigInteger[n1+1][n2+1];

        for(int i = 0; i<= n1; i++){
            deltaA[i][0] =BigInteger.valueOf(i);
            deltaB[i][0] = BigInteger.ZERO;
        }
        for(int j = 0; j<= n2; j++){
            deltaA[0][j] =BigInteger.valueOf(j);
            deltaB[0][j] = BigInteger.ZERO;
        }

        BigInteger cDelA = BigInteger.ONE;
        BigInteger cDelB = BigInteger.ZERO;

        BigInteger cInA = BigInteger.ONE;
        BigInteger cInB = BigInteger.ZERO;

        BigInteger z0A = BigInteger.ZERO;
        BigInteger z0B = BigInteger.ZERO;

        BigInteger z1A= BigInteger.ONE;
        BigInteger z1B = BigInteger.ZERO;

        for (int i = 1; i<= n1; i++){
            for(int j=1; j<=n2; j++){
                BigInteger t1A = yA[j-1].add(BigInteger.ONE).mod(twoToL);
                BigInteger t1B = yB[j-1];

                BigInteger t2A = yA[j-1].subtract(BigInteger.ONE).mod(twoToL);
                BigInteger t2B = yB[j-1];

                sb.addAndCompare(GeneralHelper.genArray(t1A,xA[i-1]),GeneralHelper.genArray(t1B,xB[i-1]), GeneralHelper.genArray(z0A, z1A), GeneralHelper.genArray(z0B, z1B));
                BigInteger t3A = sb.getYOutputA();
                BigInteger t3B = sb.getYOutputB();

                sb.addAndCompare(GeneralHelper.genArray(xA[i-1],t2A),GeneralHelper.genArray(xB[i-1],t2B), GeneralHelper.genArray(t3A, z1A),GeneralHelper.genArray(t3B, z1B));
                BigInteger cSubA = sb.getYOutputA();
                BigInteger cSubB = sb.getYOutputB();


                BigInteger term1A =  deltaA[i-1][j].add(cDelA).mod(twoToL);
                BigInteger term1B =  deltaB[i-1][j].add(cDelB).mod(twoToL);

                BigInteger term2A =  deltaA[i][j-1].add(cInA).mod(twoToL);
                BigInteger term2B =  deltaB[i][j-1].add(cInB).mod(twoToL);

                BigInteger term3A =  deltaA[i-1][j-1].add(cSubA).mod(twoToL);
                BigInteger term3B =  deltaB[i-1][j-1].add(cSubB).mod(twoToL);


                sms.getMini(GeneralHelper.genArray(term1A, term2A, term3A), GeneralHelper.genArray(term1B, term2B, term3B));

                deltaA[i][j] = sms.getXMinA();
                deltaB[i][j] = sms.getXMinB();

            }
        }
        dEDA = deltaA[n1][n2];
        dEDB = deltaB[n1][n2];

        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== SecureEditDistance protocols cost time: " + mills + " ms n1 = " + n1 + " n2 = " + n2 + " ==========");
    }

}
