import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import java.math.BigInteger;


import com.google.common.base.Stopwatch;
import paillier.PaillierSetup;


public class PaillierTest {
    @Test
    void paillierTest(){

        Stopwatch stopwatch = Stopwatch.createStarted();
        TimeUnit timeUnit = TimeUnit.MICROSECONDS;
        int paillierKeySize = 1024;
        PaillierSetup  ps = new PaillierSetup(paillierKeySize);

        System.out.println("public key generator:" + ps.getGenerator());
        System.out.println("Modular : " + ps.getModulus());

        BigInteger plainText = new BigInteger("154545");

        BigInteger cypher = ps.getPublicKey().raw_encrypt(plainText);
        System.out.println("cypher: " + cypher);
        BigInteger pt = ps.getPrivateKey().raw_decrypt(cypher);
        System.out.println("plainText: " + pt);
        System.out.println("timer(ms): " + stopwatch.elapsed(timeUnit) + " ms");
    }
}
