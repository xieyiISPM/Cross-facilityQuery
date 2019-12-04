package protocols;

import helper.GSHelper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;
import java.util.concurrent.Callable;

@Service
public class MultiThreadingTopKComputation implements Callable<String> {
    //@Autowired
    @Setter
    private OfflineShuffling offlineShuffling;

    //@Autowired
    @Setter
    private OnlineShuffling onlineShuffling;

    //@Autowired
    @Setter
    private GSHelper gsHelper;

    //@Autowired
    @Setter
    private SecureTopKSequenceQuery secureTopKSequenceQuery;


    @Setter
    private PartyA partyA;
    @Setter
    private PartyB partyB;
    @Setter
    private int k;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MultiThreadingTopKComputation(){

    }
    /*public MultiThreadingTopKComputation(PartyA partyA, PartyB partyB, int k){
        this.partyA = partyA;
        this.partyB = partyB;
        this.k = k;
    }*/

    /*@Async
    public CompletableFuture<Void> topKComputation(PartyA partyA, PartyB partyB, int k) {
        logger.info("Thread starting----");
        this.partyA = partyA;
        this.partyB = partyB;
        this. k = k;
        offlineShuffling.setPartyB(partyB);
        offlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyB(partyB);
        BigInteger[] queryA = gsHelper.getQueryA();
        BigInteger[] queryB = gsHelper.getQueryB();
        BigInteger[][] genomicSequenceA = gsHelper.getGSA();
        BigInteger[][] genomicSequenceB = gsHelper.getGSB();
        secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA, queryB, genomicSequenceB, k);
        partyA.addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
        partyB.addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());
        logger.info("Thread finished!");
        return CompletableFuture.completedFuture(null);
    }*/

    @Override
    public String call() {
        logger.info("Thread starting----");
        offlineShuffling.setPartyB(partyB);
        offlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyB(partyB);
        BigInteger[] queryA = gsHelper.getQueryA();
        BigInteger[] queryB = gsHelper.getQueryB();
        BigInteger[][] genomicSequenceA = gsHelper.getGSA();
        BigInteger[][] genomicSequenceB = gsHelper.getGSB();
        secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA, queryB, genomicSequenceB, k);
        partyA.addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleA());
        partyB.addTopKIndexDistancePair(secureTopKSequenceQuery.getTopKIndexDistTupleB());
        logger.info("Thread finished!");
        return Thread.currentThread().getName();
    }



}
