package parties;

import com.n1analytics.paillier.PaillierPublicKey;
import helper.SecureHelper;
import paillier.PaillierSetup;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PartyB implements PartyInterface {
    private int bitSize;
    private Map<Integer, BigInteger[]> randomArrayPool = new HashMap<>();
    private SecureRandom srand = new SecureRandom();

    private Map<Integer, BigInteger[]> L0Pool = new HashMap<>();
    private Map<Integer, BigInteger[]> L2Pool = new HashMap<>();

    private SecureHelper sh = new SecureHelper(bitSize);
    private PaillierSetup ps;
    private BigInteger twoToL;


    public PartyB(int bitSize){
        this.bitSize = bitSize;
        ps = new PaillierSetup(bitSize);
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    public PaillierPublicKey distributePublicKey(){
        return ps.getPublicKey();
    }


    @Override
    public void addToRandomArrayPool(Integer arrSize){
        randomArrayPool.put(arrSize, sh.genRandomArray(arrSize, srand));
    }

    @Override
    public BigInteger[] getRandomArray(Integer key){
        return randomArrayPool.get(key);
    }

    public void addToL0PooL(BigInteger[] arr){
        if (arr == null){
            throw new NullPointerException();
        }
        BigInteger L0[] = new BigInteger[arr.length];
        for(int i = 0; i< L0.length; i++){
            L0[i] = ps.getPublicKey().raw_encrypt(arr[i]);
        }
        //System.out.println("L0: ");
        //printList(L0);
        L0Pool.put(arr.length, L0);
    }

    public void addToL2Pool(BigInteger[] L1){
        BigInteger[] L2 = new BigInteger[L1.length];

        for(int i= 0; i< L1.length; i++){
            L2[i] = ps.getPrivateKey().raw_decrypt(L1[i]);
            L2[i] = (L2[i].mod(twoToL)).negate();
        }
        //System.out.println("L2");
        //printList(L2);
        L2Pool.put(L1.length, L2);
    }

    public BigInteger[] getL2(Integer key){

        return L2Pool.get(key);
    }

    public BigInteger[] getL0(Integer key){
        if(!L0Pool.containsKey(key)){
            addToRandomArrayPool(key);
            addToL0PooL(randomArrayPool.get(key));
        }
        return L0Pool.get(key);
    }



}