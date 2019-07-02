package paillier;

import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

public class PaillierPair {
    @Getter
    private PaillierPublicKey paillierPublicKey;

    @Getter
    private PaillierPrivateKey paillierPrivateKey;

    @Getter
    @Setter
    private int paillierKeySize;

    @Getter
    private BigInteger generator;
    @Getter
    private BigInteger modulus;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PaillierPair(){}

    public PaillierPair(int paillierKeySize){
        this.paillierKeySize = paillierKeySize;
    }

    @PostConstruct
    public void genKeyPair(){
        logger.info("Generating Paillier private key, public key...");
        this.paillierPrivateKey = PaillierPrivateKey.create(paillierKeySize);
        this.paillierPublicKey = paillierPrivateKey.getPublicKey();
        generator = paillierPublicKey.getGenerator();
        modulus = paillierPublicKey.getModulus();
    }
}
