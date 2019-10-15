package protocols;

import helper.GeneralHelper;
import helper.SecureHelper;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class SecureTopKSequenceQuery {
    @Value("${party.bitSize}")
    private int bitSize;

    private int m; //sequence length
    private int n; // records number

    private BigInteger twoToL;

    @Getter
    private Pair<BigInteger, BigInteger> [] indexDistTupleB;

    @Getter
    private Pair<BigInteger, BigInteger>[] indexDistTupleA;

    @Getter
    private BigInteger[][] deltaA;

    @Getter
    private BigInteger[][] deltaB;

    @Getter
    private Pair<BigInteger, BigInteger>[] topKIndexDistTupleB;

    @Getter
    private Pair<BigInteger, BigInteger>[] topKIndexDistTupleA;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @Autowired
    private SecureExactEditDistance seed;

    @Autowired
    private SecureHelper secureHelper;


    public SecureTopKSequenceQuery(){

    }

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    /**
     * Set (index, distance) tuple, which compute distance between each record sequence and query sequence
     * @param QA
     * @param SA
     * @param QB
     * @param SB
     */
    public void secureQueryPreCompute(BigInteger[]QA,BigInteger[][] SA, BigInteger[] QB, BigInteger[][] SB){

        if(QA.length !=QB.length || SA.length != SB.length || SA[0].length !=SB[0].length){
            logger.error("QA, QB or SA, SB array size does not much!");
            throw new IllegalArgumentException("Secure Top1 Sequence query pre-computation input array error."); // shared sequence between two parties has to be match
        }

        this.m = SA.length; //row length
        this.n = SA[0].length; //column length

        BigInteger[] dEDA = new BigInteger[m];
        BigInteger[] dEDB = new BigInteger[m];
        BigInteger[] originalIndex = new BigInteger[m];

        indexDistTupleB = new Pair[m];
        indexDistTupleA = new Pair[m];


        for(int i=0; i < m; i++){
            seed.getExactEditDistance(SA[i], SB[i], QA, QB);
            dEDA[i] = seed.getDEDA();
            dEDB[i] = seed.getDEDB();
            originalIndex[i] = BigInteger.valueOf(i);
        }

        SecureRandom srand = new SecureRandom();

        BigInteger[] indexA  = secureHelper.getFirstHalf(m, srand);
        BigInteger[] indexB  = secureHelper.getSecondHalf(originalIndex, indexA);

        offlineShuffling.setArraySize(indexA.length);

        BigInteger[] indexBPrime = offlineShuffling.getL2FromPartyB();
        BigInteger[] distBPrime = offlineShuffling.getL2FromPartyB();

        onlineShuffling.onLineShuffling(indexB, indexA);
        BigInteger[] indexAPrime = onlineShuffling.getL4();
        onlineShuffling.onLineShuffling(dEDB, dEDA);
        BigInteger[] distAPrime = onlineShuffling.getL4();

        for (int i = 0; i<m; i++){
            indexDistTupleB[i] = new ImmutablePair<>(indexBPrime[i], distBPrime[i]);
            indexDistTupleA[i] = new ImmutablePair<>(indexAPrime[i], distAPrime[i]);

        }
    }

    public void genTopKIndexDistTuple(int k){
        Assert.state(k<m,"Top k where k must smaller than array size" + m );

        deltaA = new BigInteger[m][2]; //index 0 save index, index 1 save distance
        deltaB = new BigInteger[m][2];

        for(int i= 0; i< m; i++){
            deltaA[i][0]= indexDistTupleA[i].getLeft();
            deltaB[i][0]= indexDistTupleB[i].getLeft();
            deltaA[i][1]= indexDistTupleA[i].getRight();
            deltaB[i][1]= indexDistTupleB[i].getRight();
        }

        for(int i= 1; i<= k;i++){
            for(int j= m-1; j>= i; j--){
                int theta =  GeneralHelper.thetaHelper(deltaA[j][1], deltaB[j][1], deltaA[j-1][1], deltaB[j-1][1], twoToL);
                if(theta==0) {
                    /**
                     * Java pass by value, therefore, I can not create a method to do swap
                     */
                    BigInteger temp;
                    temp = deltaA[j-1][0];
                    deltaA[j-1][0] = deltaA[j][0];
                    deltaA[j][0]= temp;

                    temp = deltaB[j-1][0];
                    deltaB[j-1][0] = deltaB[j][0];
                    deltaB[j][0]= temp;

                    temp = deltaA[j-1][1];
                    deltaA[j-1][1] = deltaA[j][1];
                    deltaA[j][1]= temp;

                    temp = deltaB[j-1][1];
                    deltaB[j-1][1] = deltaB[j][1];
                    deltaB[j][1]= temp;
                }
            }
        }
        topKIndexDistTupleB = new Pair[k];
        topKIndexDistTupleA = new Pair[k];

        for (int i = 0; i < k; i++){
            topKIndexDistTupleB[i]= new ImmutablePair(deltaB[i][0], deltaB[i][1]);
            topKIndexDistTupleA[i]= new ImmutablePair(deltaA[i][0], deltaA[i][1]);
        }

    }

}
