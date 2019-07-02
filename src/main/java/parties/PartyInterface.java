package parties;

import java.math.BigInteger;

public interface PartyInterface {
    void addToRandomArrayPool(Integer arraySize);

    BigInteger[] getRandomArray(Integer key);


}
