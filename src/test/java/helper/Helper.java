package helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import paillier.PaillierPair;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

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

    public void print2DArray(BigInteger[][] distance){
        int n1 = distance.length ;
        int n2 = distance[0].length;

        for(int i = 0; i< n1;i++){
            for(int j= 0; j< n2; j++){
                System.out.print(distance[i][j] + " ");
            }
            System.out.println();
        }
        System.out.print("distance = ");
        System.out.println(distance[n1-1][n2-1]);
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

    public BigInteger[] reconstruct(BigInteger[] aHalf, BigInteger[] bHalf){
        if(aHalf.length != bHalf.length){
            logger.error(aHalf.getClass() + " and " + bHalf.getClass() + " array length does not match. Reconstruction failed!");
            return null;
        }

        int arraySize = aHalf.length;

        BigInteger[] full = new BigInteger[arraySize];
        for (int i = 0; i< arraySize; i++){
            full[i] = reconstruct(aHalf[i], bHalf[i]);
        }
        return full;
    }

    public BigInteger[] strToBigInt(String str){
        char[] chars = str.toCharArray();
        BigInteger[] array = new BigInteger[chars.length];

        for(int i =0; i < chars.length; i++){
            array[i] =BigInteger.valueOf(chars[i]);
        }

        return array;
    }

    public BigInteger findMin(BigInteger[] arr){
        BigInteger min = arr[0];
        for(int i = 1; i< arr.length; i++){
            if(min.compareTo(arr[i]) > 0){
                min = arr[i];
            }
        }
        return min;
    }

    public String generateGenomicSeq(int length){
        Random rand = new Random();
        char [] characters = new char[length];
        int randNum;
        for(int i = 0; i< length;i++){
            randNum = rand.nextInt(4);
            switch(randNum){
                case 0:
                    characters[i] = 'A';
                    break;
                case 1:
                    characters[i] = 'C';
                    break;
                case 2:
                    characters[i] = 'G';
                    break;
                case 3:
                    characters[i] = 'T';
                    break;
                default:
                    break;
            }
        }
        return new String(characters);
    }

}
