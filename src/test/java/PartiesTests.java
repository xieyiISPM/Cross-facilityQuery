import config.appConfiguration;
import config.testConfiguration;
import helper.Helper;
import helper.SecureHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;
import shuffling.OfflineShuffling;
import shuffling.OnlineShuffling;

import java.math.BigInteger;
import java.security.SecureRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={appConfiguration.class, testConfiguration.class})
@TestPropertySource(properties = {"party.arraySize=100", "party.bitSize = 10"})
@ComponentScan(basePackages = "test")
public class PartiesTests {
    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Value("${party.arraySize}")
    private int arraySize;

    @Value("${party.bitSize}")
    private int bitSize;

    @Autowired
    private Helper helper;

    @Autowired
    private SecureHelper sh;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Test
    public void simpleTest(){
        partyA.setPk(paillierPair.getPaillierPublicKey());
        partyA.addToRandomArrayPool(arraySize);

        BigInteger[] uArray = partyA.getRandomArray(arraySize);
        Assert.assertEquals(arraySize, uArray.length);
        helper.printList(uArray);

    }

    @Test
    public void partBL0Generation(){
        BigInteger twoToL = BigInteger.TWO.pow(bitSize);
        partyB.addToRandomArrayPool(arraySize);
        BigInteger[] partyBL0 = partyB.getL0(arraySize);
        Assert.assertNotNull(partyB.getL0(arraySize));
        Assert.assertEquals(arraySize,partyBL0.length);
        BigInteger[] decryptedL0 = helper.getDecryptedArray(partyBL0, twoToL);

        helper.printList(decryptedL0);

        Assert.assertArrayEquals(partyB.getVArray(arraySize), decryptedL0);
    }

    @Test
    public void partyAL1Test(){
        BigInteger twoToL = BigInteger.TWO.pow(bitSize);

        BigInteger[] partyBL0 = partyB.getL0(arraySize);
        BigInteger[] partyAL1Prime= partyA.getL1Prime(partyBL0);
        BigInteger[] partyAUArray = partyA.getUArray(arraySize);
        Assert.assertNotNull(partyBL0);
        Assert.assertNotNull(partyAUArray);
        Assert.assertNotNull(partyA.getL1(arraySize));
        Assert.assertNotNull(partyAL1Prime);

        BigInteger[] partyARArray = partyA.getRArray(arraySize);
        BigInteger[] partyBVArray = partyB.getVArray(arraySize);

/*      System.out.println("PartyA R Array");
        Helper.printList(partyARArray);

        System.out.println("PartyB V Array");
        Helper.printList(partyBVArray);

        System.out.println("PartyA U Array");
        Helper.printList(partyAUArray);*/

        BigInteger[] uPlusVPlusRArray = new BigInteger[arraySize];
        for (int i = 0; i < arraySize; i++){
            uPlusVPlusRArray[i] = ((partyAUArray[i].add(partyBVArray[i])).add(partyARArray[i])).mod(twoToL);
        }

       /* System.out.println("u + v + r :");
        helper.printList(uPlusVPlusRArray);*/

        BigInteger[] partyAL1 = partyA.getL1(arraySize);

        BigInteger[] decryptedL1 = helper.getDecryptedArray(partyAL1, twoToL);

        /*System.out.println("decryptedL1: ");
        helper.printList(decryptedL1);*/

        Assert.assertArrayEquals(decryptedL1,uPlusVPlusRArray);

        BigInteger[] decryptedPartyAL1Prime = helper.getDecryptedArray(partyAL1Prime, twoToL);
        Assert.assertArrayEquals(helper.permutedArrayOrder(decryptedL1, partyA.getPi(arraySize)),decryptedPartyAL1Prime );
    }

    @Test
    public void offlineAndOnlineCombining(){
        BigInteger[] L2 = offlineShuffling.getL2FromPartyB();

        Assert.assertNotNull(L2);

        BigInteger[] partyBHalf = sh.genRandomArray(arraySize, new SecureRandom());
        BigInteger[] partyAHalf = sh.genRandomArray(arraySize, new SecureRandom());

        BigInteger[] partyFull = new BigInteger[arraySize];

        BigInteger twoToL = BigInteger.TWO.pow(bitSize);

        for(int i = 0; i< arraySize; i++){
            partyFull[i] = partyAHalf[i].add(partyBHalf[i]).mod(twoToL);
        }

        BigInteger[] partyFullPrime = helper.permutedArrayOrder(partyFull,partyA.getPi(arraySize));


        BigInteger[] L3  = onlineShuffling.generateL3ForPartyB(partyBHalf);
        BigInteger[] L4 = onlineShuffling.generateL4ForParyA(partyAHalf);
        Assert.assertNotNull(L4);

        BigInteger[] partyRecover = new BigInteger[arraySize];
        for(int i = 0; i< arraySize; i++){
            partyRecover[i] = L2[i].add(L4[i]).mod(twoToL);
        }

        Assert.assertArrayEquals(partyFullPrime, partyRecover);

    }


}
