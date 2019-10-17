import config.TestConfiguration;
import helper.GSHelper;
import helper.Helper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class GSHelperTests {

    @Autowired
    private GSHelper gsHelper;

    @Autowired
    private Helper helper;

    @Value("${genomic.records}")
    int records;

    @Test
    public void testNotNull(){
        Assert.assertNotNull(gsHelper.getGSA());
        Assert.assertNotNull(gsHelper.getGSB());
        Assert.assertNotNull(gsHelper.getOriginalGS());
        Assert.assertNotNull(gsHelper.getQueryA());
        Assert.assertNotNull(gsHelper.getQueryB());
        Assert.assertNotNull(gsHelper.getOriginalQuery());
    }

    @Test
    public void testRecover(){

        for(int i = 0; i< records; i++ ) {
            BigInteger[] gsA = gsHelper.getGSA()[i];

            BigInteger[] gsB = gsHelper.getGSB()[i];
            BigInteger[] gsOriginal = gsHelper.getOriginalGS()[i];
            BigInteger[] reconstructedGS = helper.reconstruct(gsA, gsB);

            Assert.assertArrayEquals(gsOriginal, reconstructedGS);

        }

        BigInteger[] queryA = gsHelper.getQueryA();

        BigInteger[] queryB = gsHelper.getQueryB();
        BigInteger[] originalQuery = gsHelper.getOriginalQuery();
        BigInteger[] reconstructedQuery = helper.reconstruct(queryA, queryB);

        Assert.assertArrayEquals(originalQuery, reconstructedQuery);
    }


}
