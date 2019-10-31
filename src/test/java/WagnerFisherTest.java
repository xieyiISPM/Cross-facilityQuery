import config.TestConfiguration;
import config.appConfiguration;
import helper.Helper;
import helper.WagnerFisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.security.SecureRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={appConfiguration.class, TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")

@ComponentScan(basePackages = "test")
public class WagnerFisherTest {

    @Autowired
    private WagnerFisher wagnerFisher;

    @Autowired
    private Helper helper;

    @Test
    public void testWagnerFisher(){


        /*xA: 18 16 9 10 16 1 15 14 16 17
        xB: 16 20 23 2 9 23 24 13 9 30
        x: 2 4 0 12 25 24 7 27 25 15
        yA: 4 5 27 27 22 28 7 22 22 20
        yB: 28 25 28 23 15 2 23 16 7 31
        y: 0 30 23 18 5 30 30 6 29 19
        dED_A = -5
        dED_B = 7
        reconstructed dED = 2
*/

        String strX = "CTAGA";
        String strY = "ATGAATKTKKJKJKJ";
        char[] charX = strX.toCharArray();
        char[] charY = strY.toCharArray();
        BigInteger[] x  = new BigInteger[charX.length];
        BigInteger[] y = new BigInteger[charY.length];
        int n1 = charX.length ;
        int n2 = charY.length;
        /*int[] charX = {4, 9, 28, 20, 14, 11, 5, 1, 31, 22, 18, 21, 11, 28, 31, 21, 10, 5, 16, 17, 5, 4, 25};
        int[] charY ={18, 15, 5, 4, 26, 18, 15, 30, 27, 9, 9, 17, 5, 14, 27, 10, 30, 11, 10, 24, 24, 23, 19};
        BigInteger[] x  = new BigInteger[charX.length];
        BigInteger[] y = new BigInteger[charY.length];
        int n1 = charX.length ;
        int n2 = charY.length;*/


        for (int i = 0; i< n1; i++){
            x[i] = BigInteger.valueOf(charX[i]);
        }
        for (int i = 0; i< n2; i++){
            y[i] = BigInteger.valueOf(charY[i]);
        }

        BigInteger[][] editDistance = wagnerFisher.getEditDistance(x, y);
        helper.print2DArray(editDistance);

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

    @Test
    public void testMin(){
        SecureRandom srd = new SecureRandom();
        int testRound = 30;
        int bitSize = 5;
        for (int i = 0; i < testRound; i++){
            BigInteger big1 = new BigInteger(bitSize, srd);
            BigInteger big2 = new BigInteger(bitSize, srd);
            BigInteger big3 = new BigInteger(bitSize, srd);
            System.out.println("big1 = " + big1 + " big2 = " + big2 + " big3 = " + big3);
            System.out.println("minimum number = " + min(big1, big2 , big3));

        }
    }
}
