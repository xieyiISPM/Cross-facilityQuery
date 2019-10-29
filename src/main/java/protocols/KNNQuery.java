package protocols;


import aops.LogExecutionTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class KNNQuery {
    @Getter
    List<Triple<BigInteger, Integer, Integer>> finalHKNN =new ArrayList<>();
    @Getter
    List<Triple<BigInteger, Integer, Integer>> finalCKNN = new ArrayList<>();

    @Autowired
    private SecureCompare secureCompare;

    @Getter
    @Setter
    private PartyB[] hospitals;

    @Getter
    @Setter
    private PartyA[] clouds;

    private int k;

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
    @LogExecutionTime
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
            int position = hospitals[winnerID].getIndexDistPairStackSize() - hospitals[winnerID].getIndexDistPairStackSize() - 1;
            hospitals[winnerID].setWinInfoList(position, i, true);
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
        this.hospitals = hospitals;
        this.clouds = clouds;
        this.k = k;
        genKNN();
    }


}
