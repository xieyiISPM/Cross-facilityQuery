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
import protocols.SecureTopKSequenceQuery;

import java.math.BigInteger;

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

    @Value("${genomic.records}")
    int records;

    private BigInteger[] queryA;
    private BigInteger[] queryB;
    private BigInteger[][] genomicSequenceA;
    private BigInteger[][] genomicSequenceB;

    @Before
    public void init(){
        queryA = gsHelper.getQueryA();
        queryB = gsHelper.getQueryB();
        genomicSequenceA = gsHelper.getGSA();
        genomicSequenceB = gsHelper.getGSB();
    }


    @Test
    public void setSecureTopKSequenceQuery(){
        int k = 3;
        secureTopKSequenceQuery.genTopKIndexDistTuple(queryA, genomicSequenceA,queryB,genomicSequenceB, k);

        Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleA());
        Assert.assertNotNull(secureTopKSequenceQuery.getIndexDistTupleB());
        Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleA());
        Assert.assertNotNull(secureTopKSequenceQuery.getTopKIndexDistTupleB());

        System.out.println("Index reconstructing:");
        for(int i=0;i < records; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getIndexDistTupleA()[i].getLeft(), secureTopKSequenceQuery.getIndexDistTupleB()[i].getLeft()) + " ");
        }
        System.out.println();

        System.out.println("distance reconstructing:");
        for(int i=0;i < records; i++){
            System.out.print(helper.reconstruct(secureTopKSequenceQuery.getIndexDistTupleA()[i].getRight(), secureTopKSequenceQuery.getIndexDistTupleB()[i].getRight()) + " ");
        }
        System.out.println();


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
}
