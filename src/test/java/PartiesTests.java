import aops.StopwatchAspect;
import config.TestConfiguration;
import helper.Helper;
import helper.SecureHelper;
import helper.WagnerFisher;
import org.apache.commons.lang3.RandomStringUtils;
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
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;
import parties.PartyFactory;
import protocols.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class PartiesTests {
    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyFactory partyFactory;


    private PartyA partyA;


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
    private SecureMinimumSelection sms;

    @Autowired
    private StopwatchAspect sa;

    @Autowired
    private SecureExactEditDistance seed;

    @Autowired
    private WagnerFisher wagnerFisher;

    @Before
    public void init(){
        int id = 0;
        partyA = partyFactory.cloudSandboxBuilder(id);
        partyB = partyFactory.hospitalBuilder(id);
        offlineShuffling.setPartyA(partyA);
        offlineShuffling.setPartyB(partyB);
        onlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyB(partyB);
    }

    @Test
    public void simpleTest(){
        Assert.assertNotNull(partyA);
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
        /*offlineShuffling.setPartyA(partyA);
        offlineShuffling.setPartyB(partyB);*/
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

    @Test
    public void secureMinimumSelectionTest() {
        BigInteger[] xBHalf = sh.genRandomArray(arraySize, new SecureRandom());
        BigInteger[] xAHalf = sh.genRandomArray(arraySize, new SecureRandom());

        /*BigInteger[] xBHalf = {BigInteger.valueOf(5),BigInteger.valueOf(6)};
        BigInteger[] xAHalf = {BigInteger.valueOf(3), BigInteger.valueOf(21)};*/

        System.out.println("Reconstructed x:");
        for (int i = 0; i < arraySize; i++){
            System.out.print(helper.reconstruct(xBHalf[i], xAHalf[i]) + " ");
        }
        System.out.println();

        sms.getMini(xAHalf, xBHalf);
        Assert.assertNotNull(sms.getXMinA());
        Assert.assertNotNull(sms.getXMinB());

        System.out.print("Shuffling order: ");
        helper.printList(partyA.getPi(arraySize));

        System.out.println("Reconstructed minimum output: " +helper.reconstruct(sms.getXMinA(), sms.getXMinB()));

    }


    @Test
    public void secureExactEditDistanceTest(){

       int xLength = 15;
       int yLength = 10;

        String strX = RandomStringUtils.randomAlphabetic(xLength).toUpperCase();
        System.out.println(strX);
        String strY = RandomStringUtils.randomAlphabetic(yLength).toUpperCase();
        System.out.println(strY);

        BigInteger[] x  = helper.strToBigInt(strX);
        BigInteger[] y = helper.strToBigInt(strY);

        /*System.out.println("x original array:");
        helper.printList(x);
        System.out.println("y original array");
        helper.printList(y);*/

        SecureRandom srand = new SecureRandom();

        BigInteger[] xAHalf = sh.getFirstHalf(x.length, srand);
        BigInteger[] xBHalf = sh.getSecondHalf(x, xAHalf);

        BigInteger[] yAHalf = sh.getFirstHalf(y.length, srand);
        BigInteger[] yBHalf = sh.getSecondHalf(y, yAHalf);

        BigInteger[] xFull = helper.reconstruct(xAHalf, xBHalf);
        BigInteger[] yFull = helper.reconstruct(yAHalf, yBHalf);

        /*System.out.println("x reconstructed array:");
        helper.printList(xFull);
        System.out.println("y reconstructed array");
        helper.printList(yFull);
        System.out.println();*/

        Assert.assertArrayEquals(x, xFull);
        Assert.assertArrayEquals(y, yFull);


        BigInteger[][] editDistance = wagnerFisher.getEditDistance(xFull,yFull);
        //helper.print2DArray(editDistance);

        int n1 = editDistance.length;
        int n2 = editDistance[0].length;

        seed.getExactEditDistance(xAHalf, xBHalf, yAHalf, yBHalf);
        BigInteger dEDA = seed.getDEDA();
        BigInteger dEBB = seed.getDEDB();

       // System.out.println("Reconstructed edit distance: " + helper.reconstruct(dEDA, dEBB));

        Assert.assertEquals(editDistance[n1 -1 ][n2 -1 ], helper.reconstruct(dEDA, dEBB));

    }




}
