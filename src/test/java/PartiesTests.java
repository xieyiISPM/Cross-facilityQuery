import config.appConfiguration;
import helper.Helper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import paillier.PaillierPair;
import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={appConfiguration.class})
@TestPropertySource(properties = {"party.arraySize=10"})
public class PartiesTests {
    @Autowired
    private PaillierPair paillierPair;

    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Value("${party.arraySize}")
    private int arraySize;

    @Test
    public void simpleTest(){
        partyA.setPk(paillierPair.getPaillierPublicKey());
        partyA.addToRandomArrayPool(arraySize);

        BigInteger[] uArray = partyA.getRandomArray(arraySize);
        Assert.assertEquals(arraySize, uArray.length);
        Helper.printList(uArray);
    }

    @Test
    public void partBL0Generation(){
        partyB.addToRandomArrayPool(arraySize);
        partyB.addToL0PooL(partyB.getRandomArray(arraySize));
        Assert.assertNotNull(partyB.getL0(arraySize));
        Assert.assertEquals(arraySize,partyB.getL0(arraySize).length);
        BigInteger[] L0 = partyB.getL0(arraySize);
        BigInteger[] decryptedL0 = new BigInteger[L0.length];
        int i = 0;
        for(BigInteger bigInteger: L0){
            decryptedL0[i] = paillierPair.getPaillierPrivateKey().raw_decrypt(bigInteger);
            i++;
        }
        Helper.printList(decryptedL0);

        Assert.assertArrayEquals(partyB.getRandomArray(arraySize), decryptedL0);
    }

    @Test
    public void partyAL1Test(){
        partyB.addToRandomArrayPool(arraySize);
        partyB.addToL0PooL(partyB.getRandomArray(arraySize));
        BigInteger[] partyBL0 = partyB.getL0(arraySize);
        partyA.addToL1Pool(partyBL0);
        BigInteger[] partyAUArray = partyA.getUArray(arraySize);
        Assert.assertNotNull(partyBL0);
        Assert.assertNotNull(partyAUArray);
        Assert.assertNotNull(partyA.getL1(arraySize));
    }

}
