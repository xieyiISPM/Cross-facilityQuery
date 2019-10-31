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

import java.math.BigInteger;

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
    private KNNQuery knnQuery;

    @Autowired
    private GSHelper gsHelper;

    static final int  partyNum = 2;
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
    public void knnTest(){
        int k = 100;
        for (int i = 0; i< partyNum; i++){
            offlineShuffling.setPartyB(partyBs[i]);
            offlineShuffling.setPartyA(partyAs[i]);
            onlineShuffling.setPartyA(partyAs[i]);
            onlineShuffling.setPartyB(partyBs[i]);
            BigInteger[] queryA = gsHelper.getQueryA();
            BigInteger[] queryB = gsHelper.getQueryB();
            BigInteger[][] genomicSequenceA = gsHelper.getGSA();
            BigInteger[][] genomicSequenceB = gsHelper.getGSB();
            secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA,queryB,genomicSequenceB, k);

            Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleA());
            Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleB());
            Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleA());
            Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleB());
            partyAs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
            partyBs[i].addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());
            System.out.println();


        }

        knnQuery.kNN(partyAs, partyBs, k);
        Assert.assertNotNull(knnQuery.getFinalCKNN());
        Assert.assertNotNull(knnQuery.getFinalHKNN());
    }
}
