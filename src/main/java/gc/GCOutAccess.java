package gc;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class GCOutAccess {



    public GCOutAccess(){

    }

    public int readResult(String fileName){
        try {
            Stream<String> lines = Files.lines(Paths.get(fileName));
            String line10 =lines.skip(9).findFirst().get();
            //System.out.println(line10);
            String delims="[ ]+";

            String[] token = line10.split(delims);
            return Integer.parseInt(token[2]);
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        return -1;
    }

}
