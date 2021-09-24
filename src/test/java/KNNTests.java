import config.TestConfiguration;
import helper.GSHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import parties.PartyA;
import parties.PartyB;
import parties.PartyFactory;
import protocols.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

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
    private MultiThreadingTopKComputation multiThreadingTopKComputation;

    @Autowired
    private KNNQuery knnQuery;


    static final int  partyNum = 10;
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


        for(int k = 10; k< 100; k=k+10) {

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

    }

    /*@Test
    public void multiThreadTest() throws Exception{
        int k =5;
        CompletableFuture<Void>[] cfs = new CompletableFuture[partyNum];

        for(int i = 0; i< partyNum; i++) {
           cfs[i] = multiThreadingTopKComputation.topKComputation(partyAs[i], partyBs[i], k);
       }

       CompletableFuture.allOf(cfs).join();


        knnQuery.kNN(partyAs, partyBs, k);
        Assert.assertNotNull(knnQuery.getFinalCKNN());
        Assert.assertNotNull(knnQuery.getFinalHKNN());

    }
*/




   @Test
    public void multiThreadTest() throws Exception{
       ExecutorService executorService = Executors.newFixedThreadPool(2);
       MultiThreadingTopKComputation[] multiThreadingTopKComputations = new MultiThreadingTopKComputation[partyNum];
       int k = 5;
       List<Future<String>> list =new ArrayList<>();
       List<Callable<String>> callables = new ArrayList<>();
       for(int i = 0; i< partyNum; i++){
           multiThreadingTopKComputations[i] = new MultiThreadingTopKComputation();
           multiThreadingTopKComputations[i].setPartyA(partyAs[i]);
           multiThreadingTopKComputations[i].setPartyB(partyBs[i]);
           multiThreadingTopKComputations[i].setK(k);
           multiThreadingTopKComputations[i].setGsHelper(gsHelper);
           multiThreadingTopKComputations[i].setOfflineShuffling(offlineShuffling);
           multiThreadingTopKComputations[i].setOnlineShuffling(onlineShuffling);
           multiThreadingTopKComputations[i].setSecureTopKSequenceQuery(secureTopKSequenceQuery);
           /*Future<String> future = executorService.submit(multiThreadingTopKComputations[i]);
           list.add(future);*/
           callables.add(multiThreadingTopKComputations[i]);
       }

       executorService.invokeAll(callables);

       /*for(Future<String> fut : list){
           try {
               //print the return value of Future, notice the output delay in console
               // because Future.get() waits for task to get completed
               System.out.println(new Date()+ "::"+fut.get());
           } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
           }
       }*/


      // executorService.shutdown();

       /*try{
           executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
           knnQuery.kNN(partyAs, partyBs, k);
           Assert.assertNotNull(knnQuery.getFinalCKNN());
           Assert.assertNotNull(knnQuery.getFinalHKNN());
       }
       catch (InterruptedException e){
           executorService.shutdownNow();
           System.out.println(e.getStackTrace());
       }
*/
       try{
           executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
           knnQuery.kNN(partyAs, partyBs, k);
           Assert.assertNotNull(knnQuery.getFinalCKNN());
           Assert.assertNotNull(knnQuery.getFinalHKNN());
       }
       catch (InterruptedException e){
           e.printStackTrace();
       }
   }

}
