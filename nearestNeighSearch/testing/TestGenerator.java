import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;

/**
 * Created by New on 14/04/2017.
 */
public class TestGenerator {

    public static void main(String[] args) {
        StringBuilder testSet = new StringBuilder();
        int setSize;
        String outputFileName;
        String inputFileName = null;
        Boolean testOne = true;
        ArrayList<datapoint> datapoints = new ArrayList<datapoint>();
        Formatter fmt = new Formatter();

        // Takes four arguments. One specifies test set size. Two specifies output file. Three specifies test type. Four specifies data set file (so that we can delete properly)
        for (String arg : args) {
            System.out.println(arg);
        }

        setSize = Integer.parseInt(args[0]);
        outputFileName = args[1];

        if (args.length == 4)
            inputFileName = args[3];

        if (args[2].equals("2")) {
            System.out.println("Going test 2");
            testOne = false;
        }

        // Build a representation of our dataset which we can use for deletions and additions.
        if (inputFileName != null) {
            try {
                File input = new File(inputFileName);
                BufferedReader reader = new BufferedReader(new FileReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] datas = line.split(" ");
                    datapoints.add(new datapoint(Integer.parseInt(datas[0].substring(2)), Float.parseFloat(datas[2]), Float.parseFloat(datas[3]), datas[1]));
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("Input file not found");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        Random randomer = new Random();

        if (!testOne) {

            for (int i = 0; i < setSize; i++) {
                int randomID = randomer.nextInt(datapoints.size());
                datapoint testPoint = datapoints.get(randomID - 1);

                testSet.append("D ");
                testSet.append("id" + testPoint.getId() + " ");
                testSet.append(testPoint.getCat() + " ");
                testSet.append(testPoint.getLat() + " " + testPoint.getLon() + "\r\n");
            }

            for (int i = 0; i < setSize; i++) {
                testSet.append("A ");
                testSet.append("id" + i + "a ");

                // float minX = -50.0f;
                // float maxX = 50.0f;

                // float lat = randomer.nextFloat() * (maxX - minX) + minX;
                // float lon = randomer.nextFloat() * (maxX - minX) + minX;

                // testSet.append(lat + " " + lon + "\r\n");
                
                Random rand = new Random();
            
                double lat = -(35 + 5*rand.nextDouble());
                double lon = 138 + 12*rand.nextDouble();
        
                fmt = new Formatter();
                testSet.append(fmt.format("%4.10f", lat) + " ");
        
                fmt = new Formatter();
                testSet.append(fmt.format("%4.10f", lon) + "\r\n");
            }
        }

        for (int i = 0; i < setSize; i++) {
            testSet.append("S ");

            int type = randomer.nextInt(3);
            if (type == 0)
                testSet.append("restaurant ");
            else if (type == 1)
                testSet.append("hospital ");
            else
                testSet.append("education ");

            // http://stackoverflow.com/questions/6078157/random-nextfloat-is-not-applicable-for-floats
            // float minX = -50.0f;
            // float maxX = 50.0f;

            // float lat = randomer.nextFloat() * (maxX - minX) + minX;
            // float lon = randomer.nextFloat() * (maxX - minX) + minX;
            // testSet.append(lat + " " + lon + " ");

            Random rand = new Random();
            
            double lat = -(35 + 5*rand.nextDouble());
            double lon = 138 + 12*rand.nextDouble();

            fmt = new Formatter();
            testSet.append(fmt.format("%4.10f", lat) + " ");

            fmt = new Formatter();
            testSet.append(fmt.format("%4.10f", lon) + " ");

            int k = randomer.nextInt(setSize);
            testSet.append((k + 1) + "\r\n");
        }

        try {
            File outputFile = new File(outputFileName);
            PrintWriter writer = new PrintWriter(outputFile);

            writer.print(testSet.toString());
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Output file not found. Somehow. We're making it, right?");
        }
    }
}
