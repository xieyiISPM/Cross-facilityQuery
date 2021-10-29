import com.google.common.base.Stopwatch;
import config.TestConfiguration;
import helper.GSHelper;
import helper.Helper;
import helper.SecureHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;
import parties.PartyFactory;
import protocols.OfflineShuffling;
import protocols.OnlineShuffling;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class OnlineOfflineExperiments {

    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyFactory partyFactory;

    @Autowired
    private GSHelper gsHelper;

    @Value("${party.bitSize}")
    private int bitSize;

    @Value("${shuffle.arraySize}")
    private int arraySize;

    private PartyA partyA;
    private PartyB partyB;

    @Autowired
    private Helper helper;
    @Autowired
    private SecureHelper sh;
    @Autowired
    private OnlineShuffling onlineShuffling;
    @Autowired
    private OfflineShuffling offlineShuffling;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Before
    public void init(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        int id = 0;
        partyA = partyFactory.cloudSandboxBuilder(id);
        partyB = partyFactory.hospitalBuilder(id);
        offlineShuffling.setPartyA(partyA);
        offlineShuffling.setPartyB(partyB);
        onlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyB(partyB);
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("========  parties initialization cost time: " + mills + " ms init Time" + " ========");
    }

    @Test
    public void offlineAndOnlineCombining(){
        for(arraySize = 200; arraySize < 500; arraySize = arraySize+200) {



            BigInteger[] L2 = offlineShuffling.getL2FromPartyB(arraySize);

            Assert.assertNotNull(L2);

            BigInteger[] partyBHalf = sh.genRandomArray(arraySize, new SecureRandom());
            BigInteger[] partyAHalf = sh.genRandomArray(arraySize, new SecureRandom());

            BigInteger[] partyFull = new BigInteger[arraySize];

            BigInteger twoToL = BigInteger.TWO.pow(bitSize);

            for (int i = 0; i < arraySize; i++) {
                partyFull[i] = partyAHalf[i].add(partyBHalf[i]).mod(twoToL);
            }

            BigInteger[] partyFullPrime = helper.permutedArrayOrder(partyFull, partyA.getPi(arraySize));

            onlineShuffling.onLineShuffling(partyBHalf, partyAHalf);
            BigInteger[] L3 = onlineShuffling.getL3();
            BigInteger[] L4 = onlineShuffling.getL4();
            Assert.assertNotNull(L4);

            BigInteger[] partyRecover = new BigInteger[arraySize];
            for (int i = 0; i < arraySize; i++) {
                partyRecover[i] = L2[i].add(L4[i]).mod(twoToL);
            }

            Assert.assertArrayEquals(partyFullPrime, partyRecover);
        }

    }



}
