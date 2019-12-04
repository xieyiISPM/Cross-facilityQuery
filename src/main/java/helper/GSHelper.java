package helper;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Service
public class GSHelper {
    @Value("${party.bitSize}")
    private int bitSize;

    @Value("${genomic.sequence.arraySize}")
    @Setter
    private int arraySize;

    private BigInteger twoToL;

    @Value("${genomic.records}")
    private int records;

    @Getter
    private Triple<BigInteger[], BigInteger[], BigInteger[]> queryTriple;

    @Getter
    private Triple<BigInteger[][], BigInteger[][], BigInteger[][]> genomicSequenceTriple;

    private List<BigInteger> genomicElements = new ArrayList<>(Arrays.asList(BigInteger.valueOf(1),
                                                                                    BigInteger.valueOf(2),
                                                                                    BigInteger.valueOf(3),
                                                                                    BigInteger.valueOf(4)));
    private Random random = new Random();


    public GSHelper(){}

    @PostConstruct
    private void init(){
        twoToL = BigInteger.TWO.pow(bitSize);
        queryTriple = genQueryTriple();
        genomicSequenceTriple = genGS();
    }

    public BigInteger[][] getGSA(){
        return genomicSequenceTriple.getLeft();
    }

    public BigInteger[][] getGSB(){
        return genomicSequenceTriple.getRight();
    }

    public BigInteger[][] getOriginalGS(){
        return genomicSequenceTriple.getMiddle();
    }

    public BigInteger[] getQueryA(){
        return queryTriple.getLeft();
    }

    public BigInteger[] getQueryB(){
        return queryTriple.getRight();
    }

    public BigInteger[] getOriginalQuery(){
        return queryTriple.getMiddle();
    }

    /**
     * QueryA, Query(original), and QueryB
     * @return
     */
    private Triple<BigInteger[], BigInteger[], BigInteger[]> genQueryTriple(){
        ArrayList<Triple<BigInteger, BigInteger,BigInteger>> bigTripleQShare = genRealQuery();
        BigInteger[] QA = new BigInteger[arraySize];
        BigInteger[] QB = new BigInteger[arraySize];
        BigInteger[] Q = new BigInteger[arraySize];

        for(int i=0; i< arraySize;i++ ){
            QA[i]= bigTripleQShare.get(i).getLeft();
            QB[i]= bigTripleQShare.get(i).getRight();
            Q[i]= bigTripleQShare.get(i).getMiddle();
        }
        return new ImmutableTriple<>(QA,Q,QB);
    }


    /**
     * Genomic Sequence A, Sequence (original), Sequence B
     * @return
     */
    private Triple<BigInteger[][], BigInteger[][], BigInteger[][]> genGS(){
        BigInteger[][] SA = new BigInteger[records][arraySize];
        BigInteger[][] SB = new BigInteger[records][arraySize];
        BigInteger[][] S = new BigInteger[records][arraySize];

        for(int i= 0; i< records; i++){
            ArrayList<Triple<BigInteger, BigInteger,BigInteger>> bigTripleSShare = genRealShares();

            for(int j=0; j< arraySize;j++){
                SA[i][j] = bigTripleSShare.get(j).getLeft();
                S[i][j] = bigTripleSShare.get(j).getMiddle();
                SB[i][j] = bigTripleSShare.get(j).getRight();
            }
        }

        return new ImmutableTriple<>(SA,S, SB);

    }

    /**
     * Generate random shared number < share1, original-number, share2>
     * @param bitSize
     * @param arraySize
     * @return
     */
    private ArrayList<Triple<BigInteger, BigInteger,BigInteger>> genShares(int bitSize, int arraySize) {
        SecureRandom srand = new SecureRandom();
        BigInteger[] arr = new BigInteger[2];
        BigInteger m = this.twoToL;

        ArrayList<Triple<BigInteger, BigInteger, BigInteger>> bigTripleArray = new ArrayList<>();
        for(int i=0; i< arraySize; i++){
            BigInteger sum = new BigInteger(bitSize, srand);
            arr[0] = new BigInteger(bitSize, srand);
            arr[1] = (sum.subtract(arr[0])).mod(m);
            bigTripleArray.add(new ImmutableTriple(arr[0], sum, arr[1]));
        }
        return bigTripleArray;
    }

    private ArrayList<Triple<BigInteger, BigInteger,BigInteger>> genRealShares(){

        ArrayList<Triple<BigInteger, BigInteger, BigInteger>> bigTripleArray = new ArrayList<>();

        for (int i = 0; i < arraySize; i++){
            BigInteger origin = BigInteger.valueOf(random.nextInt(4) + 1);
            bigTripleArray.add(new ImmutableTriple<>(BigInteger.ZERO, origin, origin));
        }
        return bigTripleArray;
    }

    private ArrayList<Triple<BigInteger, BigInteger,BigInteger>> genRealQuery(){
        SecureRandom srand = new SecureRandom();
        BigInteger[] arr = new BigInteger[2];
        BigInteger m = this.twoToL;
        ArrayList<Triple<BigInteger, BigInteger, BigInteger>> bigTripleArray = new ArrayList<>();

        for (int i = 0; i < arraySize; i++){
            BigInteger sum = BigInteger.valueOf(random.nextInt(4) + 1);
            arr[0] = new BigInteger(bitSize, srand);
            arr[1] = (sum.subtract(arr[0])).mod(m);
            bigTripleArray.add(new ImmutableTriple(arr[0], sum, arr[1]));
        }
        return bigTripleArray;
    }




}
