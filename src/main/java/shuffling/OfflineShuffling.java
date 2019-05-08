package shuffling;

import parties.PartyA;
import parties.PartyB;

import java.math.BigInteger;

public class OfflineShuffling {
    private PartyA partyA;
    private PartyB partyB;
    private int arraySize;
    private BigInteger[] L0;
    private BigInteger[] L1;
    private BigInteger[] L2;
    public OfflineShuffling(PartyA partyA, PartyB partyB, int arraySize){
        this.partyA = partyA;
        this.partyB = partyB;
        this.arraySize = arraySize;

    }

    public BigInteger[] getL0FromPartyB(){
        L0 = partyB.getL0(arraySize);
        return L0;
    }

    public BigInteger[] getL1FromPartyA(){
        partyA.addToL1Pool(L0);
        L1 = partyA.getL1(arraySize);
        return L1;
    }

    public BigInteger[] getL2FromPartyB(){
        partyB.addToL2Pool(L1);
        L2 = partyB.getL2(arraySize);
        return L2;
    }



}
