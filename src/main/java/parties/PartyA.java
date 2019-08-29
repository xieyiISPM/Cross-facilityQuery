package parties;

import com.n1analytics.paillier.PaillierPublicKey;
import helper.SecureHelper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import paillier.PaillierPair;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PartyA implements PartyInterface {

    private int bitSize;

    private Map<Integer, BigInteger[]> UArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();
    private Map<Integer, BigInteger[]> L1Pool = new HashMap<>();
    private Map<Integer, BigInteger[]> rArrayPool = new HashMap<>();

    @Autowired
    private SecureHelper sh;

    @Autowired
    private PaillierPair paillierPair;

    @Setter
    @Getter
    private PaillierPublicKey pk;
    private Map<Integer, Integer[]> piPool = new HashMap<>();

    @Getter
    private BigInteger twoToL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PartyA(int bitSize){
        this.bitSize = bitSize;
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    public PartyA(int bitSize, PaillierPublicKey pk){
        this.bitSize = bitSize;
        this.pk = pk;
        twoToL = BigInteger.TWO.pow(bitSize);

    }

    @PostConstruct
    private void setPublicKey(){
        pk = paillierPair.getPaillierPublicKey();
    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
            UArrayPool.put(arrSize, sh.genRandomArray(arrSize, srand));
            piPool.put(arrSize, sh.genPi(arrSize));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return UArrayPool.get(key);
    }

    /**
     * Generate L1'
     * @param L0
     */
    public void addToL1Pool(BigInteger[] L0){
        Integer arraySize = L0.length;
        if(L1Pool.containsKey(arraySize)){
            logger.warn(this.getClass() + "L1Pool has " + arraySize + " key");
            return;
        }
        if(!UArrayPool.containsKey(arraySize)){
            addToRandomArrayPool(arraySize);
        }
        else{
            logger.warn(this.getClass()+"UArrayPool has "+ arraySize + " key");
        }
        BigInteger[] U = getRandomArray(arraySize);

        BigInteger[] rArray = sh.genRandomArray(arraySize, srand);
        rArrayPool.put(arraySize,rArray);

        BigInteger L1[] = new BigInteger[arraySize];
        for(int i=0; i< arraySize; i++){
            BigInteger uPlusR = pk.raw_encrypt((U[i].add(rArray[i])).mod(twoToL));
            L1[i] = pk.raw_add(L0[i], uPlusR);
            //L1[i] = (L0[i].multiply(uPlusR)).mod(paillierPublicKey.getModulusSquared());
        }

        if(!piPool.containsKey(arraySize)){
            piPool.put(arraySize,sh.genPi(arraySize));
        }
        else{
            logger.warn(this.getClass()+" piPool has "+ arraySize + " key");
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

    public BigInteger[] getRArray(Integer key){
        return rArrayPool.get(key);
    }

    public BigInteger[] getUArray(Integer key){
        return UArrayPool.get(key);
    }
}