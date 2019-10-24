package protocols;

import aops.LogExecutionTime;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SecureCompare {
    @Value("${party.bitSize}")
    private int bitSize;

    private BigInteger twoToL;

    private Random srand = new SecureRandom();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public SecureCompare(){

    }

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    @LogExecutionTime
    public int secureComparing(BigInteger distHa, BigInteger distCa, BigInteger distHb, BigInteger distCb){
       /* logger.info("Secure Comparing is Starting.....");
        Stopwatch stopwatch = Stopwatch.createStarted();*/
        int testTimes = 3;
        BigInteger[] r = new BigInteger[testTimes];
        int[] result = new int[testTimes];
        for(int i = 0; i<testTimes; i++){
            r[i] = new BigInteger((bitSize *(i+1))/testTimes, srand);
            //C generate rA
            BigInteger blindedCa = blindedDist(distCa,r[i] );
            //C generate rB
            BigInteger blindedCb = blindedDist(distCb,r[i] );
            //Ha generate distPrime
            BigInteger distHaPrime = genDistPrime(distHa, blindedCa);
            BigInteger distHbPrime = genDistPrime(distHb, blindedCb);
            result[i]= distHaPrime.compareTo(distHbPrime); // May need CMP compare!!!! FIXME

        }


        int voteResult= 0;
        for(int i = 0; i < testTimes; i++){
            voteResult = result[i] + voteResult;
        }
/*
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        logger.info("======== SecureCompare protocols cost time: " + mills + " milliseconds ==========");*/

        if (voteResult > 0 ){
            return 1;
        }
        else if(voteResult ==0){
            return 0;
        }
        else return -1;


    }

    private BigInteger blindedDist(BigInteger dist, BigInteger r){

        BigInteger distPlusR = (dist.add(r)).mod(twoToL);
        return distPlusR;
    }

    private BigInteger genDistPrime(BigInteger dist, BigInteger blindedDist){

        return dist.add(blindedDist).mod(twoToL);
    }

}
