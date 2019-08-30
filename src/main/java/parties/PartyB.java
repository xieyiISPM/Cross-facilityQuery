package parties;

import com.n1analytics.paillier.PaillierPublicKey;
import helper.SecureHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import paillier.PaillierPair;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PartyB implements PartyInterface {

    private int bitSize;

    private Map<Integer, BigInteger[]> VArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();

    private Map<Integer, BigInteger[]> L0Pool = new HashMap<>();
    private Map<Integer, BigInteger[]> L2Pool = new HashMap<>();

    @Autowired
    private SecureHelper sh;

    @Autowired
    private PaillierPair paillierPair;

    @Getter
    private BigInteger twoToL  = BigInteger.TWO.pow(bitSize);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public  PartyB(){

    }
    public PartyB(int bitSize){
        this.bitSize = bitSize;
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    public PaillierPublicKey distributePublicKey(){
        return paillierPair.getPaillierPublicKey();
    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
        VArrayPool.put(arrSize, sh.genRandomArray(arrSize, srand));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return VArrayPool.get(key);
    }

    private void addToL0PooL(int arraySize){
        if(L0Pool.containsKey(arraySize)){
            logger.warn(this.getClass() + "L0Pool has " + arraySize + " key");
            return;
        }
        else{
            addToRandomArrayPool(arraySize);
            BigInteger[] vArray = getRandomArray(arraySize);
            BigInteger L0[] = new BigInteger[arraySize];
            for(int i = 0; i< L0.length; i++){
                L0[i] = paillierPair.getPaillierPublicKey().raw_encrypt(vArray[i]);
            }
            L0Pool.put(arraySize,L0);
            logger.info(this.getClass() + "L0Pool has been add new random u array, with " + arraySize + " key");
        }
    }

    private void addToL2Pool(BigInteger[] L1Prime){
        if(L2Pool.containsKey(L1Prime.length)){
            logger.warn(this.getClass() + "L2Pool has " + L1Prime.length + " key");
            return;
        }
        BigInteger[] L2 = new BigInteger[L1Prime.length];

        for(int i= 0; i< L1Prime.length; i++){
            L2[i] = paillierPair.getPaillierPrivateKey().raw_decrypt(L1Prime[i]);
            L2[i] = (L2[i].mod(twoToL)).negate();
        }
        L2Pool.put(L1Prime.length, L2);
    }

    public BigInteger[] getL2(BigInteger[] partyAL1Prime){
        addToL2Pool(partyAL1Prime);

        return L2Pool.get(partyAL1Prime.length);
    }

    public BigInteger[] getL0(Integer key){
        addToL0PooL(key);
        return L0Pool.get(key);
    }

    public BigInteger[] getVArray(Integer key){
        return VArrayPool.get(key);
    }



}