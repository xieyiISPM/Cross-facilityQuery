package helper;

import java.math.BigInteger;

public class GeneralHelper {

    public static int thetaHelper(BigInteger x0A, BigInteger x0B, BigInteger x1A, BigInteger x1B, BigInteger twoToL){
        int result = (x0A.add(x0B).mod(twoToL)).compareTo(x1A.add(x1B).mod(twoToL));
        if(result >=0){
            return 1;
        }
        else return 0;
    }

    public static BigInteger[] genArray(BigInteger... args){
        BigInteger[] arr = new BigInteger[args.length];
        for(int i = 0; i< args.length; i++ ){
            arr[i] =  args[i];
        }
        return arr;
    }

    public static int getMax(int n1, int n2){
        int winner = n1>=n2? n1:n2;
        return winner;
    }
}
