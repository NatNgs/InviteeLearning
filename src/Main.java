import rnn.RNN;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by nathael on 29/11/16.
 */
public class Main {
    private static RNN rnn;

    public static void main(String[] args) {
        // obtaining RNN save (if not, create new RNN)
        try {
            rnn = getRNN();
            System.out.println("RNN successfully loaded.");

            try {
                System.out.println("Computing...");
                compute();
            } catch (Exception e) {
                System.err.println("An exception occurs while computing result.");
                e.printStackTrace();
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("No understandable save found, creating a new RNN...");
            rnn = new RNN(500);
        }

        try {
            System.out.println("Saving RNN...");
            storeRNN();
            System.out.println("RNN successfully saved.");
        } catch (IOException e) {
            System.err.println("An exception occurs while saving RNN.");
            e.printStackTrace();
        }
    }

    private static RNN getRNN() throws IOException, ClassNotFoundException {
        //deserialize objects sarah and sam
        FileInputStream fis = new FileInputStream("saves/rnn.save");
        GZIPInputStream gs = new GZIPInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(gs);
        List<String> lines = new ArrayList<>();
        String o = null;
        try {
            while ((o = (String) ois.readObject()) != null) {
                lines.add(o);
            }
        } catch (EOFException ignored) {}

        //print the records after reconstruction of state
        ois.close();
        fis.close();

        try {
            rnn = RNN.load(lines);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return rnn;
    }

    private static void storeRNN() throws IOException {
        List<String> lines = new ArrayList<>();
        //Files.write(Paths.get("res/rnn.save"),rnn.save());

        FileOutputStream fos = new
                FileOutputStream("saves/rnn.save");
        GZIPOutputStream gz = new GZIPOutputStream(fos);
        ObjectOutputStream os = new ObjectOutputStream(gz);

        List<String> save = rnn.save();
        for(String s : save)
            os.writeObject(s);

        // serialize the objects sarah and sam
        os.flush();
        os.close();
        fos.close();
    }

    private static void compute() throws Exception {
        //Ouverture du fichier
        FileReader input = new FileReader("res/input.txt");
        BufferedReader bufRead = new BufferedReader(input);
        String line = bufRead.readLine(); // first line containing data names, ignoring it

        List<String> lines = new ArrayList<>();
        while((line = bufRead.readLine()) != null) {
            lines.add(line + "");
        }

        // convert String to float[]
        float[][] dataTable = new float[lines.size()][9];
        int sizeI=0;
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for(final String aline : lines) {
            String[] split = aline.replaceAll(" ","").split(",");

            int sizeJ=0;
            for(final String s : split) {
                if(sizeJ==0) {
                    // do not use timestamp
                    sizeJ++;
                    continue;
                }
                dataTable[sizeI][sizeJ-1] = Float.parseFloat(s);
                if(dataTable[sizeI][sizeJ-1] < min)
                    min = dataTable[sizeI][sizeJ-1];
                if(dataTable[sizeI][sizeJ-1]> max)
                    max = dataTable[sizeI][sizeJ-1];
                sizeJ++;
            }
            sizeI++;
        }

        max = max-min; // speed-up (max is not maximum now)
        // normalize min/max
        for(int i=0; i<dataTable.length; i++) {
            float[] ft = dataTable[i];
            for (int j = 0; j < ft.length; j++) {
                dataTable[i][j] = (ft[j] - min) / max;
                //System.out.print(dataTable[i][j]+" ");
            }
            //System.out.println();
        }

        double value = rnn.getOutputFor(dataTable);
        System.out.printf("This was a "+(value>0?"good":"bad")+" cut ! (%5.2f %% good)\n", value*100);
        double error = 1;
        for(int iter=0; iter<50 && error > 0; iter++) {
            System.out.print("Iterating " + (iter + 1) + "/50");
            error = rnn.learn(dataTable, 1);
            System.out.printf(", Error: %2.5f%%\n", error * 100);
        }
    }
}
