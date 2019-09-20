package helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import paillier.PaillierPair;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Service
public class Helper {

    @Value("${party.bitSize}")
    private int bitSize;

    private BigInteger twoToL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private  PaillierPair paillierPair;

    @PostConstruct
    private void init(){
         twoToL = BigInteger.TWO.pow(bitSize);
    }


    public void printList(BigInteger[] list){
        for (BigInteger item: list){
            System.out.print(item + " ");
        }
        System.out.println();
    }

    public void printList(Integer[] list){
        for (Integer item: list){
            System.out.print(item + " ");
        }
        System.out.println();
    }

    public BigInteger[] getDecryptedArray(BigInteger[] list, BigInteger twoToL){
        int arraySize = list.length;
        BigInteger[] decryptedList = new BigInteger[arraySize];
        for(int i = 0; i< arraySize; i++){
            decryptedList[i] = (paillierPair.getPaillierPrivateKey().raw_decrypt(list[i])).mod(twoToL);
        }

        return decryptedList;
    }

    public BigInteger[] permutedArrayOrder(BigInteger[] requestArray, Integer[] pi){
        if(requestArray.length != pi.length){
            logger.error(requestArray.getClass() + "length = "+ requestArray.length + "does not match pi function length = "+pi.length);
            return null;
        }
        int arraySize = requestArray.length;
        BigInteger[] targetArray = new BigInteger[arraySize];
        for(int i = 0; i< arraySize; i++){
            targetArray[i] = requestArray[pi[i]];
        }

        return targetArray;
    }

    public BigInteger reconstruct(BigInteger a, BigInteger b){
        return (a.add(b)).mod(twoToL);
    }

}
