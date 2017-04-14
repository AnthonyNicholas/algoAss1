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
        
        Double [][] KDresultsArray = new Double[7][11];
        KDresultsArray = setupResultsArray(KDresultsArray);
        int [] pointNums = {0,1000,3000,6000,9000,15000,30000};
        String s1, s2;
        FileWriter fOut = null;
        String approach = "kdtree";


        //SETUP TEST FILES
        // InsFileGenerator iGen = new InsFileGenerator();
        // iGen.makeInsFile(10);

        //Time the execution of kdTree and naive
        for (int i = 1; i < 7; i++){
            timeExecutionKDTree(10, approach, pointNums[i], KDresultsArray,i);  
        }
        
        // s1 = "./testResults/Reports/report2.txt";
        // fOut = new FileWriter(s1, true);
        // s2 = Arrays.deepToString(KDresultsArray);
        // System.out.println(s2);

        // System.out.println(KDresultsArray[1][1]);

        writeArrayToFile(KDresultsArray, approach); 

        return;
    }

    private static void timeExecutionKDTree(int k, String approach, int numPoints, Double [][] resultsArray, int rowNum) 
        throws IOException{

        Long start, end;
        Double timeElapsed;
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
        
        s1 = "./testResults/Reports/reportKDTree_raw_data.txt";
        fOut = new FileWriter(s1, true);
        fOut.write("\n\n");
        fOut.write("Test of " + approach + " with " + numPoints + " Points\n");
        fOut.write("============================================================= \n");

        for (int i = 1; i<(k+1); i++){

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
            resultsArray[rowNum][i] = timeElapsed;
            
            //Write times to Report File
            fmt = new Formatter();
            s3 = "Search for " + i + " points: elapsed time: " + fmt.format("%4.5f", timeElapsed) + " milliseconds \n";
            fOut.write(s3);

        }
        fOut.close();
        
        return;
    }
        
    private static Double[][] setupResultsArray(Double [][] resultsArray) {

        resultsArray [1][0] = (Double)1000.0;
        resultsArray [2][0] = (Double)3000.0;
        resultsArray [3][0] = (Double)6000.0;
        resultsArray [4][0] = (Double)9000.0;
        resultsArray [5][0] = (Double)15000.0;
        resultsArray [6][0] = (Double)30000.0;
        // resultsArray [7][0] = (Double)100000;

        resultsArray [0][1] = (Double)1.0;
        resultsArray [0][2] = (Double)2.0;
        resultsArray [0][3] = (Double)3.0;
        resultsArray [0][4] = (Double)4.0;
        resultsArray [0][5] = (Double)5.0;
        resultsArray [0][6] = (Double)6.0;
        resultsArray [0][7] = (Double)7.0;
        resultsArray [0][8] = (Double)8.0;
        resultsArray [0][9] = (Double)9.0;
        resultsArray [0][10] = (Double)10.0;
        
        return resultsArray;
    }

    private static void writeArrayToFile(Double [][] resultsArray, String approach) 
        throws IOException {

        Formatter fmt;
        Date date = new Date();

        // s1 = "./testResults/Reports/report2.txt";
        // fOut = new FileWriter(s1, true);
        // s2 = Arrays.deepToString(KDresultsArray);
        // System.out.println(s2);

        // System.out.println(KDresultsArray[1][1]);

        // writeArrayToFile(KDresultsArray) 


        // http://stackoverflow.com/questions/34958829/how-to-save-a-2d-array-into-a-text-file-with-bufferedwriter
        
        int row = resultsArray.length;
        int col = resultsArray[0].length;

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < row; i++)//for each row
        {
           for(int j = 0; j < col; j++)//for each column
           {
                fmt = new Formatter();
                builder.append(fmt.format("%10.5f", resultsArray[i][j])+"\t");//append to the output string
              // if(j < col - 1)//if this is not the last row element
              //    builder.append(",");//then add comma (if you don't like commas you can use spaces)
           }
           builder.append("\n");//append new line at the end of the row
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter("./testResults/Reports/reportKDTree.txt", true));

        writer.write("\n\n");
        writer.write("Test of " + approach + ": K is 1 to 10, Number of Points is 1000 to 30000\n");
        writer.write("\n\n");//save the string representation of the board
        writer.write(builder.toString());//save the string representation of the board
        writer.close();
        
        return;
    }

    
} //End of class