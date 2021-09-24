import config.TestConfiguration;
import helper.GSHelper;
import helper.Helper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import parties.PartyA;
import parties.PartyB;
import parties.PartyFactory;
import protocols.OfflineShuffling;
import protocols.OnlineShuffling;
import protocols.SecureTopKSequenceQuery;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
@ComponentScan(basePackages = "test")
public class SecureTopKSequenceQueryTests {

    @Autowired
    private GSHelper gsHelper;

    @Autowired
    private Helper helper;

    @Autowired
    private SecureTopKSequenceQuery secureTopKSequenceQuery;

    @Autowired
    private PartyFactory partyFactory;

    @Autowired
    private OnlineShuffling onlineShuffling;

    @Autowired
    private OfflineShuffling offlineShuffling;

    int records;

    private BigInteger[] queryA;
    private BigInteger[] queryB;
    private BigInteger[][] genomicSequenceA;
    private BigInteger[][] genomicSequenceB;

    private PartyA partyA;
    private PartyB partyB;

    @Before
    public void init(){
        int id = 0;
        partyA = partyFactory.cloudSandboxBuilder(id);
        partyB = partyFactory.hospitalBuilder(id);
        offlineShuffling.setPartyB(partyB);
        offlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyA(partyA);
        onlineShuffling.setPartyB(partyB);

        queryA = gsHelper.getQueryA();
        queryB = gsHelper.getQueryB();
        genomicSequenceA = gsHelper.getGSA();
        genomicSequenceB = gsHelper.getGSB();
        records = genomicSequenceA.length;
    }


    @Test
    public void setSecureTopKSequenceQuery(){

        int k = 10;
        secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA,queryB,genomicSequenceB, k);

        Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleA());
        Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleB());
        Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleA());
        Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleB());
/*
        System.out.println("Index reconstructing:");
        for(int i=0;i < records; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getIndexDistTupleA()[i].getLeft(), secureTopKSequenceQuery.getIndexDistTupleB()[i].getLeft()) + " ");
        }
        System.out.println();

        System.out.println("distance reconstructing:");
        for(int i=0;i < records; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getIndexDistTupleA()[i].getRight(), secureTopKSequenceQuery.getIndexDistTupleB()[i].getRight()) + " ");
        }
        System.out.println();*/


        System.out.println("Top " + k + " index");
        for(int i=0;i <k; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getTopKIndexDistTupleA()[i].getLeft(), secureTopKSequenceQuery.getTopKIndexDistTupleB()[i].getLeft()) + " ");
        }
        System.out.println();

        System.out.println("Top " + k + " distance");
        for(int i=0;i <k; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getTopKIndexDistTupleA()[i].getRight(), secureTopKSequenceQuery.getTopKIndexDistTupleB()[i].getRight()) + " ");
        }
        System.out.println();
    }

    @Test
    public void fileReadTest() throws IOException {
        BigInteger[][] gs = gsHelper.readGsFile();
        for(int i = 0; i< 20;i++) {
            System.out.print(gs[0][i]);
        }
        System.out.println();
        System.out.println(gs.length);
        System.out.println(gs[0].length);
    }
}
