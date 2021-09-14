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


public class PartyA extends CloudHospital implements PartyInterface {

    @Setter
    private int bitSize;

    private Map<Integer, BigInteger[]> UArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();
    private Map<Integer, BigInteger[]> L1Pool = new HashMap<>();
    private Map<Integer, BigInteger[]> L1PrimePool = new HashMap<>();
    private Map<Integer, BigInteger[]> rArrayPool = new HashMap<>();

    @Getter
    private Map<Integer, BigInteger[]> L4PrimeArrayPool = new HashMap<>();



    @Setter
    private SecureHelper secureHelper;

    @Setter
    private PaillierPair paillierPair;

    @Getter
    @Setter
    private PaillierPublicKey pk;
    private Map<Integer, Integer[]> piPool = new HashMap<>();

    @Getter
    private BigInteger twoToL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PartyA(){
    }


    public void setupTwoToL(){
        twoToL = BigInteger.TWO.pow(bitSize);

    }

/*    public PartyA(int bitSize, PaillierPublicKey pk){
        this.bitSize = bitSize;
        this.pk = pk;
        twoToL = BigInteger.TWO.pow(bitSize);

    }*/


    public void setPublicKey(){
        pk = paillierPair.getPaillierPublicKey();
    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
            UArrayPool.put(arrSize, secureHelper.genRandomArray(arrSize, srand));
            piPool.put(arrSize, secureHelper.genPi(arrSize));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return UArrayPool.get(key);
    }

    /**
     * Generate L1'  C offline get L1'
     * @param L0
     */
    private void addToL1Pool(BigInteger[] L0){
        Integer arraySize = L0.length;
        if(L1Pool.containsKey(arraySize)){
       //     logger.warn(this.getClass() + "L1Pool has " + arraySize + " key");
            return;
        }
        if(!UArrayPool.containsKey(arraySize)){
            addToRandomArrayPool(arraySize);
        }
        else{
         //   logger.warn(this.getClass()+"UArrayPool has "+ arraySize + " key");
        }
        BigInteger[] U = getRandomArray(arraySize);

        BigInteger[] rArray = genRArray(arraySize);
        rArrayPool.put(arraySize,rArray);

        BigInteger L1[] = new BigInteger[arraySize];
        for(int i=0; i< arraySize; i++){
            BigInteger uPlusR = pk.raw_encrypt((U[i].add(rArray[i])).mod(twoToL));
            L1[i] = pk.raw_add(L0[i], uPlusR);
            //L1[i] = (L0[i].multiply(uPlusR)).mod(paillierPublicKey.getModulusSquared());
        }

        L1Pool.put(arraySize,L1);
        if(!piPool.containsKey(arraySize)){
            piPool.put(arraySize, secureHelper.genPi(arraySize));
        //    logger.warn(this.getClass()+" piPool add "+ arraySize + " key");

        }
        else{
          //  logger.info(this.getClass()+" piPool has "+ arraySize + " key");
        }
        Integer[] pi = piPool.get(arraySize);
        L1PrimePool.put(arraySize, secureHelper.permRandomArray(L1,pi));
    }

    public BigInteger[] getL1(Integer key){
        return L1Pool.get(key);
    }
    public BigInteger[] getL1Prime(BigInteger[] L0) {
        addToL1Pool(L0);
        return L1PrimePool.get(L0.length);
    }

    /**
     * Online C get L4'
     * @param partyAHalf
     * @param L3
     * @return
     */
    public BigInteger[] getL4Prime(BigInteger[] partyAHalf, BigInteger[] L3){
      //  logger.info("Online phase of secure Shuffling starting for " + this.getClass() + " !");

        Assert.notNull(partyAHalf, "party A 's input can not be null.");
        Assert.notEmpty(partyAHalf, "Party A can not be empty.");
        Assert.notNull(L3, "Array L3 can not be null");

        if(partyAHalf.length!=L3.length){
           // logger.error("Array size between L3 and party A's input do not match!");
        }

        if(!UArrayPool.containsKey(partyAHalf.length)){
         //   logger.error("Can not retrieve related U array with size; " + partyAHalf.length + " " +
         //           " \n Make sure you have offline Secure protocols first! ");
            return null;
        }

        BigInteger[] uArray = UArrayPool.get(partyAHalf.length);
        BigInteger[] L4 = new BigInteger[partyAHalf.length];
        BigInteger[] L4Prime = new BigInteger[partyAHalf.length];
        for(int i = 0; i< partyAHalf.length; i++){
            L4[i] = (partyAHalf[i].add(uArray[i])).add(L3[i]).mod(twoToL);
        }
        L4Prime = secureHelper.permRandomArray(L4,piPool.get(partyAHalf.length));
        L4PrimeArrayPool.put(partyAHalf.length, L4Prime);
        return L4Prime;
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

    private BigInteger[] genRArray(int arraySize){
        BigInteger[] rArray = new BigInteger[arraySize];
        for(int i = 0; i< arraySize;i++){
            BigInteger temp = new BigInteger(bitSize, srand);
            rArray[i] = temp.multiply(twoToL);
        }
        return rArray;
    }


}