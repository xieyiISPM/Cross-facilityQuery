package parties;

import helper.SecureHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import paillier.PaillierPair;

@Component
public class PartyFactory {
    @Autowired
    private SecureHelper secureHelper;

    @Autowired
    private PaillierPair paillierPair;

    @Value("${party.bitSize}")
    private int bitSize;


    public PartyB hospitalBuilder(int idNum){
        PartyB partyB =  new PartyB();
        partyB.setHospitalId(idNum);
        partyB.setPaillierPair(paillierPair);
        partyB.setSecureHelper(secureHelper);
        partyB.setBitSize(bitSize);
        partyB.setupTwoToL();
        return partyB;

    }

    public PartyA cloudSandboxBuilder(int idNum){
        PartyA partyA =  new PartyA();
        partyA.setHospitalId(idNum);
        partyA.setPaillierPair(paillierPair);
        partyA.setSecureHelper(secureHelper);
        partyA.setBitSize(bitSize);
        partyA.setupTwoToL();
        partyA.setPublicKey();
        return partyA;
    }
}
