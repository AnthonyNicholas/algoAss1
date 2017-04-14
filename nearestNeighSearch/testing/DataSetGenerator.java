import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.*;

/**
 * Created by New on 14/04/2017.
 */
public class DataSetGenerator {

    public static void main(String[] args) {
        StringBuilder dataSet = new StringBuilder();
        int setSize;
        String outputFileName;
        Formatter fmt = new Formatter();


        // Takes two arguments. One specifies dataset size. Two specifies output file.
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments.");
        }

        for (String arg : args) {
            System.out.println(arg);
        }

        setSize = Integer.parseInt(args[0]);
        outputFileName = args[1];

        Random randomer = new Random();

        for (int i = 0; i < setSize; i++) {
            dataSet.append("id" + i + " ");

            int type = randomer.nextInt(3);
            if (type == 0)
                dataSet.append("restaurant ");
            else if (type == 1)
                dataSet.append("hospital ");
            else
                dataSet.append("education ");


            // http://stackoverflow.com/questions/6078157/random-nextfloat-is-not-applicable-for-floats
            // float minX = -50.0f;
            // float maxX = 50.0f;
            // double lat = fmt.format("%4.10f", newLat);
            // double lon = randomer.nextFloat() * (maxX - minX) + minX;;

            Random rand = new Random();
            
            double lat = -(35 + 5*rand.nextDouble());
            double lon = 138 + 12*rand.nextDouble();

            fmt = new Formatter();
            dataSet.append(fmt.format("%4.10f", lat) + " ");

            fmt = new Formatter();
            dataSet.append(fmt.format("%4.10f", lon) + "\r\n");

        }

        try {
            File outputFile = new File(outputFileName);
            PrintWriter writer = new PrintWriter(outputFile);

            writer.print(dataSet.toString());
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Output file not found. Somehow. We're making it, right?");
        }
    }
}
