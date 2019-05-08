package parties;

import java.math.BigInteger;
import java.security.SecureRandom;

public interface PartyInterface {
    void addToRandomArrayPool(Integer arraySize);

    BigInteger[] getRandomArray(Integer key);


}
