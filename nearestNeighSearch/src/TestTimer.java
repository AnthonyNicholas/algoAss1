import testNN.*;

import java.util.*;
import java.io.*;
import java.util.Random;


// /**
//  * Class for generating test files
//  *
//  */
public class TestTimer {

    public static void main(String args[])
        throws IOException{

        //Create Array to Store Data
        
        Double [][] KDresultsArray = new Double [7][11];
        KDresultsArray = setupResultsArray(KDresultsArray);
        int [] pointNums = {0,1000,3000,6000,9000,15000,30000};


        //SETUP TEST FILES
        // InsFileGenerator iGen = new InsFileGenerator();
        // iGen.makeInsFile(10);

        //Time the execution of kdTree and naive
        for (int i = 1; i < 7; i++){
            timeExecutionKDTree(10, "kdtree", pointNums[i], KDresultsArray,i);  
        }
        System.out.println(Arrays.deepToString(KDresultsArray));

        return;
    }

    private static void timeExecutionKDTree(int k, String approach, int numPoints, Double [][] resultsArray, int rowNum) 
        throws IOException{

        long start, end;
        double timeElapsed;
        Process p = null;
        Runtime r = Runtime.getRuntime();
        FileWriter fOut = null;
        String s, s1, s2, s3, s4;
        Formatter fmt;

        // s = "rm ./testResults/OutputFiles/*.txt";
        // s4 = "rm ./testResults/InstructionFiles/*.txt";
        // try {
        //     p = r.exec(s);
        //     System.out.println("Removed Output Files.\n");
        //     p = r.exec(s4);
        //     System.out.println("Removed Instruction Files.\n");
        // } catch (Exception e) {
        //     System.out.println("Error executing NN.");
        // }

        //TIME EXECUTION OF PROGRAM FOR NN FOR EACH INST FILE
        System.out.println("Timing execution of NN");
        
        s1 = "./testResults/Reports/report1.txt";
        fOut = new FileWriter(s1, true);
        fOut.write("\n\n");
        fOut.write("Test of KDTree with " + numPoints + " Points\n");
        fOut.write("============================================================= \n");

        for (int i = 1; i<k; i++){

            // "./testResults/DataFiles/" +
            s2 = "java NearestNeighFileBased " + approach;
            s2 +=" data" + numPoints + ".txt";
            s2 += " ./testResults/InstructionFiles/test" + i + ".in";
            s2 += " ./testResults/OutputFiles/output" + i + ".txt";
            
            // System.out.println(s2);
            
            start = System.nanoTime(); // get starting time

            try {
               p = r.exec(s2);
             } catch (Exception e) {
               System.out.println("Error executing NN.");
            }
            
            end = System.nanoTime(); // get ending time

            timeElapsed = ((double)(end-start))/1000000;
            resultsArray[rowNum][k] = timeElapsed;
            
            //Write times to Report File
            fmt = new Formatter();
            s3 = "Search for " + i + " points: elapsed time: " + fmt.format("%4.5f", timeElapsed) + " milliseconds \n";
            fOut.write(s3);

        }
        fOut.close();
        
        return;
    }
        
    private static Double [][] setupResultsArray(Double [][] resultsArray) {

        resultsArray [1][0] = 1000.0;
        resultsArray [2][0] = 3000.0;
        resultsArray [3][0] = 6000.0;
        resultsArray [4][0] = 9000.0;
        resultsArray [5][0] = 15000.0;
        resultsArray [6][0] = 30000.0;
        // resultsArray [7][0] = 100000.0;

        resultsArray [0][1] = 1.0;
        resultsArray [0][2] = 2.0;
        resultsArray [0][3] = 3.0;
        resultsArray [0][4] = 4.0;
        resultsArray [0][5] = 5.0;
        resultsArray [0][6] = 6.0;
        resultsArray [0][7] = 7.0;
        resultsArray [0][8] = 8.0;
        resultsArray [0][9] = 9.0;
        resultsArray [0][10] = 10.0;
        
        return resultsArray;
    }
    
} //End of class