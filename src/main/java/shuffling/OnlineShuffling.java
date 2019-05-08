package shuffling;

import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

public class OnlineShuffling {
    PartyA partyA;
    PartyB partyB;
    int arraySize;
    BigInteger[] L3;
    BigInteger[] L4;

    public OnlineShuffling(PartyA partyA, PartyB partyB, Integer arraySize){
        this.partyA = partyA;
        this.partyB = partyB;
        this.arraySize = arraySize;
    }

    public BigInteger[] getL3FromPartyB(){
        return null;
    }

    public BigInteger[] getL4FromParyA(){
        return null;
    }
}
