package helper;

import config.appConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import paillier.PaillierPair;

import java.math.BigInteger;

@ContextConfiguration(classes={appConfiguration.class})
public class Helper {

    @Autowired
    private  PaillierPair paillierPair;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
}
