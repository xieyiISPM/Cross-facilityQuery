import config.TestConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import protocols.SecureCompare;

import java.math.BigInteger;
import java.security.SecureRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class SecureCompareTests {

    @Autowired
    private SecureCompare secureCompare;

    @Value("${party.bitSize}")
    private int bitSize;

    private int distBitSize = 10;

    private SecureRandom srand = new SecureRandom();

    private BigInteger twoToL;

    private int startValue = 0;

    private long totalTestRound = 1;

    @Before
    public void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    @Test
    public void secureComparing() {
        //int failTimes= 0;
        for (long testRound = 0; testRound < totalTestRound; testRound++) {
            BigInteger randA = new BigInteger(distBitSize, srand);
            BigInteger distA = randA.add(BigInteger.valueOf(startValue));

            BigInteger distCa = new BigInteger(distBitSize, srand);
            BigInteger dishHa = (distA.subtract(distCa)).mod(twoToL);

            BigInteger randB = new BigInteger(distBitSize, srand);
            BigInteger distB = randB.add(BigInteger.valueOf(startValue));

            BigInteger distCb = new BigInteger(distBitSize, srand);
            BigInteger distHb = ((distB.subtract(distCb)).mod(twoToL));

            int rawCompareResult = (distA.compareTo(distB));
            int secureCompareResult = secureCompare.secureComparing(dishHa, distCa, distHb, distCb);

            Assert.assertEquals(rawCompareResult, secureCompareResult);

            /*if (rawCompareResult != secureCompareResult) {

                *//*System.out.println("rawCompareResult: " + rawCompareResult);
                System.out.println("secureCompareResult: " + secureCompareResult);
                System.out.println("test round= " + testRound);*//*
                failTimes++;
            }
            //System.out.println();*/
        }
       /* System.out.println(failTimes);
        System.out.println((double) failTimes / totalTestRound);*/
    }

}


