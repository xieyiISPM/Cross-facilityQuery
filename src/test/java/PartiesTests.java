import aops.StopwatchAspect;
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
import protocols.OfflineShuffling;
import protocols.OnlineShuffling;
import protocols.SecureBranch;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={appConfiguration.class, testConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")

@ComponentScan(basePackages = "test")
public class PartiesTests {
    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Value("${shuffle.arraySize}")
    private int arraySize;

    @Value("${branch.arraySize}")
    private int secureBranchArraySize;

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

    @Autowired
    private SecureBranch sb;

    @Autowired
    private StopwatchAspect sa;

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
        arraySize = new Random().nextInt(50);
        offlineShuffling.setArraySize(arraySize);

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

        onlineShuffling.onLineShuffling(partyBHalf, partyAHalf);
        BigInteger[] L3  = onlineShuffling.getL3();
        BigInteger[] L4 = onlineShuffling.getL4();
        Assert.assertNotNull(L4);

        BigInteger[] partyRecover = new BigInteger[arraySize];
        for(int i = 0; i< arraySize; i++){
            partyRecover[i] = L2[i].add(L4[i]).mod(twoToL);
        }

        Assert.assertArrayEquals(partyFullPrime, partyRecover);

    }

    @Test
    public void secureBranchTest(){
        BigInteger[] xBHalf = sh.genRandomArray(secureBranchArraySize, new SecureRandom());
        BigInteger[] xAHalf = sh.genRandomArray(secureBranchArraySize, new SecureRandom());

        BigInteger[]yBHalf = sh.genRandomArray(secureBranchArraySize, new SecureRandom());
        BigInteger[] yAHalf = sh.genRandomArray(secureBranchArraySize, new SecureRandom());

        /*BigInteger[] xBHalf = {BigInteger.valueOf(5),BigInteger.valueOf(6)};
        BigInteger[] xAHalf = {BigInteger.valueOf(3), BigInteger.valueOf(21)};*/

        System.out.println("Reconstructed x[0] = "  + helper.reconstruct(xBHalf[0], xAHalf[0]));
        System.out.println("Reconstructed x[1] = " +  helper.reconstruct(xBHalf[1], xAHalf[1]));

        /*BigInteger[] yBHalf = {BigInteger.valueOf(4),BigInteger.valueOf(11)};
        BigInteger[] yAHalf = {BigInteger.valueOf(2), BigInteger.valueOf(5)};*/

       // System.out.println("y[0] y[1]");
/*        helper.printList(yAHalf);
        helper.printList(yBHalf);*/
        System.out.println("Reconstructed y[0] = "  + helper.reconstruct(yBHalf[0], yAHalf[0]));
        System.out.println("Reconstructed y[1] = " +  helper.reconstruct(yBHalf[1], yAHalf[1]));

        sb.addAndCompare(xAHalf, xBHalf,yAHalf,yBHalf);
        Assert.assertNotNull(sb.getYOutputA());
        Assert.assertNotNull(sb.getYOutputB());
/*        System.out.println(sb.getYOutputA());
        System.out.println(sb.getYOutputB());*/

        System.out.print("Shuffling order: ");
        helper.printList(partyA.getPi(secureBranchArraySize));

        System.out.println("Reconstructed y output: " +helper.reconstruct(sb.getYOutputA(), sb.getYOutputB()));

    }


}
