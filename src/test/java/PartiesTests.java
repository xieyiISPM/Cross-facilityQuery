import config.appConfiguration;
import helper.Helper;
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
        Helper.printList(uArray);
    }

    @Test
    public void partBL0Generation(){
        int bitSize = 5;
        int arraySize = 5;
        partyB.setBitSize(bitSize);
        partyB.addToRandomArrayPool(arraySize);

        partyB.addToL0PooL(partyB.getRandomArray(arraySize));
        Assert.assertNotNull(partyB.getL0(arraySize));
        Assert.assertEquals(arraySize,partyB.getL0(arraySize).length);
        Helper.printList(partyB.getL0(arraySize));

    }
}
