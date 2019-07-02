package parties;

import com.n1analytics.paillier.PaillierPublicKey;
import helper.SecureHelper;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PartyA implements PartyInterface {
    @Getter
    @Setter
    private int bitSize;

    private Map<Integer, BigInteger[]> UArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();
    private Map<Integer, BigInteger[]> L1Pool = new HashMap<>();
    private SecureHelper sh;

    @Setter
    @Getter
    private PaillierPublicKey pk;
    private Map<Integer, Integer[]> piPool = new HashMap<>();

    @Getter
    private BigInteger twoToL;

    public PartyA(int bitSize){
        this.bitSize = bitSize;
        twoToL = BigInteger.TWO.pow(bitSize);
        sh = new SecureHelper(bitSize);
    }

    public PartyA(int bitSize, PaillierPublicKey pk){
        this.bitSize = bitSize;
        this.pk = pk;
        twoToL = BigInteger.TWO.pow(bitSize);

    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
        UArrayPool.put(arrSize, sh.genRandomArray(arrSize, srand));
        piPool.put(arrSize,sh.genPi(arrSize));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return UArrayPool.get(key);
    }

    public void addToL1Pool(BigInteger[] L0){
        Integer arraySize = L0.length;
        if(!UArrayPool.containsKey(arraySize)){
            addToRandomArrayPool(arraySize);
        }
        BigInteger[] U = getRandomArray(arraySize);

        BigInteger[] rArray = sh.genRandomArray(arraySize, srand);

        BigInteger L1[] = new BigInteger[arraySize];
        for(int i=0; i< arraySize; i++){
            BigInteger uPlusR = pk.raw_encrypt((U[i].add(rArray[i])).mod(twoToL));
            L1[i] = pk.raw_add(L0[i], uPlusR);
            //L1[i] = (L0[i].multiply(uPlusR)).mod(paillierPublicKey.getModulusSquared());
        }

        if(!piPool.containsKey(arraySize)){
            piPool.put(arraySize,sh.genPi(arraySize));
        }
        Integer[] pi = piPool.get(arraySize);
        L1Pool.put(arraySize,sh.permRandomArray(L1,pi));
    }

    public BigInteger[] getL1(Integer key){
        return L1Pool.get(key);
    }

    public Integer[] getPi(Integer key){
        return piPool.get(key);
    }
}