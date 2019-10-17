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
    private SecureTopKSequenceQuery stsq;

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
    public void secureQueryPreCompute(){
        stsq.secureQueryPreCompute(queryA, genomicSequenceA,queryB,genomicSequenceB);
        Assert.assertNotNull(stsq.getIndexDistTupleA());
        Assert.assertNotNull(stsq.getIndexDistTupleB());

        System.out.println(stsq.getIndexDistTupleA()[0].getLeft());
        System.out.println(stsq.getIndexDistTupleA()[0].getRight());

    }
}
