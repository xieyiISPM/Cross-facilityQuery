package parties;

import com.n1analytics.paillier.PaillierPublicKey;
import helper.SecureHelper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import paillier.PaillierPair;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class PartyB extends CloudHospital implements PartyInterface {

    @Setter
    private int bitSize;

    private Map<Integer, BigInteger[]> VArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();

    private Map<Integer, BigInteger[]> L0Pool = new HashMap<>();
    private Map<Integer, BigInteger[]> L2Pool = new HashMap<>();

    @Setter
    private SecureHelper secureHelper;

    @Setter
    private PaillierPair paillierPair;

    @Getter
    private BigInteger twoToL  = BigInteger.TWO.pow(bitSize);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public  PartyB(){

    }

    public void setupTwoToL(){
        twoToL = BigInteger.TWO.pow(bitSize);

    }

    public PaillierPublicKey distributePublicKey(){
        return paillierPair.getPaillierPublicKey();
    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
        VArrayPool.put(arrSize, secureHelper.genRandomArray(arrSize, srand));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return VArrayPool.get(key);
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

    public BigInteger[] getL3(BigInteger[] partyBHalf){
        logger.info("Online phase of secure Shuffling starting for " + this.getClass() + " !");

        Assert.notNull(partyBHalf, "Party B's input is null!");
        Assert.notEmpty(partyBHalf, "Party B has no elements inside");


        if(!VArrayPool.containsKey(partyBHalf.length)){
            logger.error("Can not retrieve related v array with size; " + partyBHalf.length + " " +
                    " \n Make sure you have offline Secure protocols first! ");
            return null;
        }

        BigInteger [] vArray = VArrayPool.get(partyBHalf.length);

        BigInteger [] L3 = new BigInteger[partyBHalf.length];

        int i = 0;
        for(BigInteger x: partyBHalf){
            L3[i] = x.add(VArrayPool.get(partyBHalf.length)[i]).mod(twoToL);
            i++;
        }
        return L3;
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



}