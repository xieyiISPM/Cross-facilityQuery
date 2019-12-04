import com.google.common.base.Stopwatch;
import config.AppConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import paillier.PaillierPair;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={AppConfiguration.class})
public class paillierTests {
    @Autowired
    private PaillierPair paillierPair;

    private Stopwatch stopwatch;
    private TimeUnit timeUnit = TimeUnit.MICROSECONDS;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void paillierParam(){
        Assert.assertNotNull(paillierPair);
        String numString = RandomStringUtils.random(10, "0123456789");
        stopwatch = Stopwatch.createStarted();
        BigInteger plainText = new BigInteger(numString);
        BigInteger cypher = paillierPair.getPaillierPublicKey().raw_encrypt(plainText);
        logger.info("Encrytion-Decrytion time: {} ms" ,stopwatch.elapsed(timeUnit) );
        Assert.assertEquals(plainText, paillierPair.getPaillierPrivateKey().raw_decrypt(cypher));
    }
}
