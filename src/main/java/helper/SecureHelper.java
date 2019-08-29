package helper;

import org.bouncycastle.pqc.math.linearalgebra.Permutation;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class SecureHelper {
    int bitSize;
    BigInteger twoToL;
    public SecureHelper(int bitSize){
        this.bitSize = bitSize;
        twoToL = BigInteger.TWO.pow(bitSize);
    }

    /**
     * Generate a random array
     * @param arraySize array size
     * @param srand secureRandom object
     * @return random array
     */
    public BigInteger[] genRandomArray(int arraySize, SecureRandom srand){
        BigInteger[] randArray = new BigInteger[arraySize];

        for (int i = 0; i < arraySize; i++) {
            randArray[i] = new BigInteger(bitSize, srand);
        }
        return randArray;
    }

    /**
     * Generate pi function : Create a random permutation of the given size;
     * @param arrSize array size
     * @return permuted array index
     */
    public Integer[] genPi(int arrSize){
        Permutation perm = new Permutation(arrSize, new SecureRandom());
        int[] temp = perm.getVector();
        return Arrays.stream(temp).boxed().toArray( Integer[]::new );
    }

    /**
     * Permutate random array by using pre-generated pi function
     * @param arr array to be randomized
     * @param pi permutation function
     * @return return permuted array
     */
    public BigInteger[] permRandomArray(BigInteger[] arr, Integer[] pi){
        if(arr.length != pi.length) {
            System.err.println("Array size does not match permutation function size");
            return null;
        }
        BigInteger[] permRand = new BigInteger[arr.length];
        for(int i = 0; i< arr.length; i++){
            permRand[i] = arr[pi[i]];
        }
        return permRand;
    }

    /**
     * Generate r array, twoToL must be carefully chosen,
     * @param arraySize r array's size
     * @return
     */
    private BigInteger[] genRArray(int arraySize, SecureRandom srand){
        BigInteger[] rArray = new BigInteger[arraySize];
        for(int i = 0; i< arraySize; i++){
            BigInteger temp = new BigInteger(bitSize, srand);
            rArray[i] = temp.multiply(twoToL);
        }
        return rArray;
    }
}
