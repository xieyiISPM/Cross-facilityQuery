package parties;

import java.math.BigInteger;
import java.security.SecureRandom;

public interface PartyInterFace {
    void addToRandomArrayPool(Integer arraySize);

    BigInteger[] getRandomArray(Integer key);


}
