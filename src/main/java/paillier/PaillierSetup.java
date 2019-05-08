package paillier;

import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.math.BigInteger;

public class PaillierSetup {
    private int paillerKeySize;
    private PaillierPrivateKey sk;
    private PaillierPublicKey pk;

    public PaillierSetup(int paillierKeySize){
        this.paillerKeySize = paillierKeySize;
        genKeyPair();
    }

    private void genKeyPair(){
        this.sk = PaillierPrivateKey.create(paillerKeySize);
        this.pk = sk.getPublicKey();
    }

    public PaillierPrivateKey getPrivateKey(){
        return sk;
    }

    public PaillierPublicKey getPublicKey(){
        return pk;
    }

    public BigInteger getGenerator(){
        return pk.getGenerator();
    }

    public BigInteger getModulus(){
        return pk.getModulus();
    }





}
