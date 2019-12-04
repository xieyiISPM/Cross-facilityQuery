import gc.GarbledCircuit;
import config.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Random;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class GCTests {

    @Autowired
    GarbledCircuit garbledCircuit;

    @Test
    public void gcTest(){
        BigInteger bigA =  new BigInteger(10, new Random());
        BigInteger bigB = new BigInteger(10, new Random());
        try {
            int result = garbledCircuit.GCCMPOutPut(bigA, bigB);
            int realResult = bigA.compareTo(bigB);
            if(realResult ==1 || realResult == 0) {
                realResult = 1;
            }
            else {
                realResult = 0;
            }
            Assert.assertEquals(realResult, result);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
