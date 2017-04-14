import java.util.*;
import java.io.*;
import java.util.Random;

// /**
//  * Class for generating test files
//  *
//  */
public class DataFileGenerator {

     /**
     * Constructors
     */
    // public DateFileGenerator() {
    // }


    // /**
    //  * @param args the command line arguments
    //  */
    public static void main(String args[]) 
        throws IOException {

        FileWriter fOut = new FileWriter("data100000.txt");
        Formatter fmt;
        Random rand = new Random();


        Double newLat; 
        Double newLong; 
        String s;

        int pointNum = 33333;

        writeCategory(fOut, "restaurant", 0, pointNum); 
        writeCategory(fOut, "hospital", pointNum, (2*pointNum)); 
        writeCategory(fOut, "education", (2*pointNum), (3*pointNum)); 

        fOut.close();

        return;
    }

    public static void writeCategory(FileWriter fOut, String category, int idStart, int idFin) 
        throws IOException{
            
        Double newLat; 
        Double newLong; 
        String s;
        Formatter fmt;
        Random rand = new Random();
    
        for (int i = idStart; i < idFin; i++){
            newLat = -(35 + 5*rand.nextDouble());
            newLong = 138 + 12*rand.nextDouble();
            fmt = new Formatter();
            s = "id" + i + " " + category + " " + fmt.format("%4.10f", newLat);
            fmt = new Formatter();    
            s += " " + fmt.format("%4.10f", newLong) + "\n";
            fOut.write(s);
        }
            
        return;

    }

        

} // End Class

