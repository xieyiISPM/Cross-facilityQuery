package helper;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;

@Service
public class WagnerFisher {

    public BigInteger[][] getEditDistance(BigInteger[] x, BigInteger[] y){

        Assert.noNullElements(x, x.getClass()+ "is null!");
        Assert.noNullElements(y, y.getClass()+" is null!");

        int n1 = x.length;
        int n2 = y.length;


        BigInteger[][] distance = new BigInteger[n1 + 1][n2 +1];

        for(int i =0; i<=n1; i++){
            distance[i][0]= BigInteger.valueOf(i);
        }
        for(int j = 0; j<=n2; j++ ){
            distance[0][j] = BigInteger.valueOf(j);
        }
        BigInteger cDel = BigInteger.ONE;
        BigInteger cIns = BigInteger.ONE;
        BigInteger cSub;
        for (int i = 1; i<=n1; i++ ){
            for(int j= 1; j <=n2; j++){
                if(x[i-1].compareTo(y[j-1])==0){
                    cSub = BigInteger.ZERO;
                }
                else {
                    cSub = BigInteger.ONE;
                }
                distance[i][j] = min(distance[i-1][j].add(cDel), distance[i][j-1].add(cIns),distance[i-1][j-1].add(cSub));
            }
        }

        return distance;
    }

    private BigInteger min(BigInteger big1, BigInteger big2, BigInteger big3){
        if(big1.compareTo(big2) < 0){
            if(big1.compareTo(big3) < 0){
                return big1;
            }
            else{
                return big3;
            }
        }
        else{
            if(big3.compareTo(big2)<0){
                return big3;
            }
            else{
                return big2;
            }
        }
    }
}
