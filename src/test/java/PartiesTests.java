import config.appConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={appConfiguration.class})
public class PartiesTests {
    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Test
    public void simpleTest(){
        int bitSize = 5;
        int arraySize = 10;
        partyA.setBitSize(bitSize);
        partyA.setPk(paillierPair.getPaillierPublicKey());
        partyA.addToRandomArrayPool(arraySize);

        BigInteger[] uArray = partyA.getRandomArray(arraySize);
        Assert.assertEquals(arraySize, uArray.length);
        for(int i = 0; i< uArray.length;i++){
            System.out.print(uArray[i] + " ");
        }
        System.out.println();
    }
}
