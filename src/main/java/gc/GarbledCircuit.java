package gc;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class GarbledCircuit {
    @Value("${add.cmp.cir}")
    private String addCmpcircuitFile;

    @Value("${cmp.cir}")
    private String cmpcircuitFile;

    @Value("${b.input.file}")
    private String serverInputFile;

    @Value("${a.input.file}")
    private String clientInputFile;

    @Value("${run.test.gc.parser}")
    private String cmd;

    @Value("${gc.dir}")
    private String gcDir;
    @Value("${client.out}")
    private String clientFileName;

    @Value("${server.out}")
    private String serverFileName;

    @Value("${gc.parser.path}")
    private String path;

    @Autowired
    CreateADDCMPInputFile createADDCMPInputFile;

    @Autowired
    GCOutAccess gcOutAccess;

    private Stopwatch stopwatch;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public GarbledCircuit(){

    }

    public int GCADDCMPOutPut(BigInteger xA, BigInteger xB, BigInteger yA, BigInteger yB) throws Exception{
        stopwatch = Stopwatch.createStarted();
        createADDCMPInputFile.setClientInputFile(path + clientInputFile);

        createADDCMPInputFile.setServerInputFile(path + serverInputFile);

        createADDCMPInputFile.setADDCMPClientVar(xA, xB);
        createADDCMPInputFile.setADDCMPSeverVar(yA, yB);

        //must have absolute path here!!!
        ProcessBuilder gcProcess = new ProcessBuilder(cmd, addCmpcircuitFile,serverInputFile,clientInputFile );
        gcProcess.directory(new File(gcDir));

        Process p = gcProcess.start();

        p.waitFor();
        BufferedReader reader=new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== GC add and Comp: " + mills + " ==========");
        int thetaClient = gcOutAccess.readResult(clientFileName);
        int thetaServer =  gcOutAccess.readResult(serverFileName);

        Assert.isTrue(thetaClient==thetaServer, "Theta client should be equal to theta server");


        return thetaClient;

    }

    public int GCCMPOutPut(BigInteger A, BigInteger B) throws Exception{

        stopwatch = Stopwatch.createStarted();

        createADDCMPInputFile.setClientInputFile(path + clientInputFile);

        createADDCMPInputFile.setServerInputFile(path + serverInputFile);

        createADDCMPInputFile.setCMPClientVar(A);
        createADDCMPInputFile.setCMPSeverVar(B);

        //must have absolute path here!!!
        ProcessBuilder gcProcess = new ProcessBuilder(cmd, cmpcircuitFile,serverInputFile,clientInputFile );
        gcProcess.directory(new File(gcDir));

        Process p = gcProcess.start();

        p.waitFor();
        BufferedReader reader=new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("======== GC Comp: " + mills + " ==========");

        int thetaClient = gcOutAccess.readResult(clientFileName);
        int thetaServer =  gcOutAccess.readResult(serverFileName);
        Assert.isTrue(thetaClient==thetaServer, "Theta client should be equal to theta server");


        return thetaClient;

    }




}
