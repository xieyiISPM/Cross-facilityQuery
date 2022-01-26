import com.google.common.base.Stopwatch;
import config.TestConfiguration;
import helper.GSHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import parties.PartyA;
import parties.PartyB;
import parties.PartyFactory;
import protocols.KNNQuery;
import protocols.OfflineShuffling;
import protocols.OnlineShuffling;
import protocols.SecureTopKSequenceQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class KNNTests {

    @Autowired
    private PartyFactory partyFactory;

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @Autowired
    private SecureTopKSequenceQuery secureTopKSequenceQuery;

    @Autowired
    private GSHelper gsHelper;


    @Autowired
    private KNNQuery knnQuery;




    static final int  partyNum = 32;
    private PartyA[] partyAs = new PartyA[partyNum];
    private PartyB[] partyBs = new PartyB[partyNum];

    @Before
    public void init(){
        for(int i = 0; i< partyNum; i++) {
            partyAs[i] = partyFactory.cloudSandboxBuilder(i);
            partyBs[i] = partyFactory.hospitalBuilder(i);
        }

    }

    @Test
    public void knnTest() throws IOException {

        int k = 10;
        com.google.common.base.Stopwatch stopwatch = Stopwatch.createStarted();


        for (int i = 0; i < partyNum; i++) {

                offlineShuffling.setPartyB(partyBs[i]);
                offlineShuffling.setPartyA(partyAs[i]);
                onlineShuffling.setPartyA(partyAs[i]);
                onlineShuffling.setPartyB(partyBs[i]);
                BigInteger[] queryA = gsHelper.getQueryA();
                BigInteger[] queryB = gsHelper.getQueryB();
                BigInteger[][] genomicSequenceA = gsHelper.getGSA();
                BigInteger[][] genomicSequenceB = gsHelper.getGSB();
                secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA, queryB, genomicSequenceB, k);

                Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleA());
                Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleB());
                Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleA());
                Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleB());
                partyAs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
                partyBs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());

            }

        knnQuery.kNN(partyAs, partyBs, k);
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);


        String filename = "knntest";
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(filename),true));

        printWriter.printf("  KNNTest single thread party numbers:" + partyNum + " Cost Time: " + mills + " milliseconds ");
        printWriter.close();


        Assert.assertEquals(k, knnQuery.getFinalCKNN().size());
        Assert.assertEquals(k, knnQuery.getFinalHKNN().size());

    }

    @Test
    public void knnDifferentHospitalTest() throws IOException {

        int k = 10;


        for (int i = 0; i < partyNum; i++) {

            offlineShuffling.setPartyB(partyBs[i]);
            offlineShuffling.setPartyA(partyAs[i]);
            onlineShuffling.setPartyA(partyAs[i]);
            onlineShuffling.setPartyB(partyBs[i]);
            BigInteger[] queryA = gsHelper.getQueryA();
            BigInteger[] queryB = gsHelper.getQueryB();
            BigInteger[][] genomicSequenceA = gsHelper.getGSA();
            BigInteger[][] genomicSequenceB = gsHelper.getGSB();
            secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA, queryB, genomicSequenceB, k);

            Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleA());
            Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleB());
            Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleA());
            Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleB());
            partyAs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
            partyBs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());
            System.out.println();

        }

        knnQuery.kNN(partyAs, partyBs, k);




        Assert.assertEquals(k, knnQuery.getFinalCKNN().size());
        Assert.assertEquals(k, knnQuery.getFinalHKNN().size());

    }



   @Test
    public void multiThreadsTest() throws Exception{

       //ExecutorService eService = Executors.newSingleThreadExecutor();
       ExecutorService eService = Executors.newCachedThreadPool();

       int k = 10;
       AtomicInteger partyIndex  = new AtomicInteger(0);
       com.google.common.base.Stopwatch stopwatch = Stopwatch.createStarted();

       for(int i = 0; i< partyNum; i++) {
           prepareOfflineShuffling(i);
       }

       for(int i = 0; i< partyNum; i++) {
           eService.execute(() -> {
                computeSecureTopkSequenceQuery(partyIndex.getAndIncrement(),k);
           });

       }
       eService.shutdown();
       try{
           eService.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
           knnQuery.kNN(partyAs, partyBs, k);
           stopwatch.stop();
           long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);

           System.out.println();


           String filename = "knntest";
           PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(filename),true));

           printWriter.printf("  KNNTest multiThread party numbers:" + partyNum + " Cost Time: " + mills + " milliseconds ");
           printWriter.close();
           Assert.assertNotNull(knnQuery.getFinalCKNN());
           Assert.assertNotNull(knnQuery.getFinalHKNN());
       }
       catch (InterruptedException e){
           e.printStackTrace();
       }


   }

   private  void computeSecureTopkSequenceQuery(int i, int k) {
       System.out.println("Thread " + i + " starting----");

       BigInteger[] queryA = gsHelper.getQueryA();
       BigInteger[] queryB = gsHelper.getQueryB();
       BigInteger[][] genomicSequenceA = gsHelper.getGSA();
       BigInteger[][] genomicSequenceB = gsHelper.getGSB();
       secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA, queryB, genomicSequenceB, k);
       partyAs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
       partyBs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());

       System.out.println("Thread " + i + " finished!");

   }

   private synchronized void prepareOfflineShuffling(int i){
       offlineShuffling.setPartyB(partyBs[i]);
       offlineShuffling.setPartyA(partyAs[i]);
       onlineShuffling.setPartyA(partyAs[i]);
       onlineShuffling.setPartyB(partyBs[i]);
   }

}
