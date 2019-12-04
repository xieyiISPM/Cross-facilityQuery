package protocols;


import aops.LogExecutionTime;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@NoArgsConstructor
public class KNNQuery {
    @Getter
    List<Triple<BigInteger, Integer, Integer>> finalHKNN =new ArrayList<>();
    @Getter
    List<Triple<BigInteger, Integer, Integer>> finalCKNN = new ArrayList<>();

    @Value("${genomic.records}")
    private int genomicRecords;

    @Autowired
    private SecureCompare secureCompare;

    @Getter
    @Setter
    private PartyB[] hospitals;

    @Getter
    @Setter
    private PartyA[] clouds;

    private int k;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private Set<Integer> winnerSet = new HashSet<>();

    /**
     * Generate final k-NN among different hospitals
     * Algorithm:
     *   for 0 to k-1:
     *      1. peek each top distance from hospital's stack
     *      2. compare distances among distances
     *     3. pop out distance of final winner
     *     4. store winner information
     *  endFor
     */

    private void genKNN() {
        Assert.isTrue(clouds.length == hospitals.length, "hospitals size must match clouds size");
        int hospitalNum = hospitals.length;

        for (int i = 0; i < k; i++) {
            int next = 1;
            int winnerID = 0;

            while (next < hospitalNum) {
                BigInteger distHA = hospitals[winnerID].peekIndexDistPair().getRight();
                BigInteger distHB = hospitals[next].peekIndexDistPair().getRight();
                BigInteger distCA = clouds[winnerID].peekIndexDistPair().getRight();
                BigInteger distCB = clouds[next].peekIndexDistPair().getRight();

                int compareResult = secureCompare.secureComparing(distHA, distCA, distHB, distCB);

                if (compareResult > 0) {
                    winnerID = next;
                }
                next++;
            }
            hospitals[winnerID].popIndexDistPair();
            clouds[winnerID].popIndexDistPair();
            int position = hospitals[winnerID].getIndexDistPairArrayLength() - hospitals[winnerID].getIndexDistPairStackSize()-1 ; //todo
            hospitals[winnerID].setWinInfoList(position, i, true);
            clouds[winnerID].setWinInfoList(position, i, true);
            winnerSet.add(winnerID);
        }

        for(Integer hospitalId: winnerSet){
            hospitals[hospitalId].genFinalResults();
            clouds[hospitalId].genFinalResults();
            finalHKNN.addAll(hospitals[hospitalId].getFinalResultList());
            finalCKNN.addAll(clouds[hospitalId].getFinalResultList());
        }


    }


    public void kNN(PartyA[] clouds, PartyB[] hospitals, int k){
        Assert.isTrue(k<=genomicRecords,"k should less or equal than records number!");
        this.hospitals = hospitals;
        this.clouds = clouds;
        this.k = k;
        Stopwatch stopwatch = Stopwatch.createStarted();
        genKNN();
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== kNN protocols cost time: " + mills + " ms k= " + k + " ==========");
    }


}
