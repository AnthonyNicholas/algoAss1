package testNN;

import java.util.*;
import java.io.*;
import java.util.Random;

// /**
//  * Class for generating test files
//  *
//  */
public class InsFileGenerator {

     /**
     * Constructors
     */
    public InsFileGenerator() {
    }

    // /**
    //  * @param args the command line arguments
    //  */

    public void makeInsFile(int numInstFiles) 
        throws IOException{
            

        FileWriter fOut = null;
        String s1, s2;
        
        int k;

        for (k = 1; k<numInstFiles; k++){
            s1 = "./testResults/InstructionFiles/test" + k + ".in";
            fOut = new FileWriter(s1,true);
            writeSearch(fOut, "restaurant", k); 
            fOut.close();
        }
        
        return;

    }  


    public void writeSearch(FileWriter fOut, String category, int k) 
        throws IOException{
            
            // System.out.println("Hi there 2"); 
    //     // Generate Instruction
    //     // S restaurant -33.60 144.87 10

        Double searchLat; 
        Double searchLon; 
        Random rand = new Random();
        String s2;

        for (int i = 0; i < 10; i++){

            searchLat = -(35 + 5*rand.nextDouble());
            searchLon = 138 + 12*rand.nextDouble();

            s2 = "S" + " " + category + " " + searchLat + " " + searchLon + " " +  k + "\n";
            fOut.write(s2);
        
        }


        return;

    }  

} // End Class

