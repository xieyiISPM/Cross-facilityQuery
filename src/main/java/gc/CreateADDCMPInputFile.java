package gc;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.math.BigInteger;

@Service
public class CreateADDCMPInputFile {
    @Value("${gc.parser.path}")
    String path;

    @Setter
    String clientInputFile;
    @Setter
    String serverInputFile;

    public CreateADDCMPInputFile(){

    }

    public void setADDCMPClientVar(BigInteger bigIntA1, BigInteger bigIntA2) throws Exception{
        PrintWriter pw = new PrintWriter(clientInputFile);
        pw.println("a1 " + bigIntA1);
        pw.println("a2 " + bigIntA2);
        pw.close();
    }

    public void setADDCMPSeverVar(BigInteger bigIntB1, BigInteger bigIntB2) throws Exception{
        PrintWriter pw = new PrintWriter(serverInputFile);
        pw.println("b1 " + bigIntB1);
        pw.println("b2 " + bigIntB2);

        pw.close();
    }

    public void setCMPClientVar(BigInteger bigIntA) throws Exception{
        PrintWriter pw = new PrintWriter(clientInputFile);
        pw.println("a " + bigIntA);
        pw.close();
    }

    public void setCMPSeverVar(BigInteger bigIntB) throws Exception{
        PrintWriter pw = new PrintWriter(serverInputFile);
        pw.println("b " + bigIntB);

        pw.close();
    }

}
