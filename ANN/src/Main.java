import ann.ANN;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by nathael on 29/11/16.
 */
public class Main {
    private static ANN ANN;

    public static void main(String[] args) {
        // obtaining ANN save (if not, create new ANN)
        try {
            ANN = getANN();
            System.out.println("ANN successfully loaded.");

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("No understandable save found, creating a new ANN...");
            ANN = new ANN(500, 5);
            //System.exit(0);
        }

        try {
            System.out.println("Computing...");
            compute();
        } catch (Exception e) {
            System.err.println("An exception occurs while computing result.");
            e.printStackTrace();
        }

        try {
            System.out.println("Saving ANN...");
            storeANN();
            System.out.println("ANN successfully saved.");
        } catch (IOException e) {
            System.err.println("An exception occurs while saving ANN.");
            e.printStackTrace();
        }
    }

    private static ANN getANN() throws IOException, ClassNotFoundException {
        //deserialize objects sarah and sam
        FileInputStream fis = new FileInputStream("ANN/saves/ann.save");
        GZIPInputStream gs = new GZIPInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(gs);
        List<String> lines = new ArrayList<>();
        String o = null;
        try {
            while ((o = (String) ois.readObject()) != null) {
                lines.add(o.replaceAll(" ",""));
            }
        } catch (EOFException ignored) {}

        //print the records after reconstruction of state
        ois.close();
        fis.close();

        try {
            ANN = ANN.load(lines);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return ANN;
    }

    private static void storeANN() throws IOException {
        FileOutputStream fos = new
                FileOutputStream("ANN/saves/ann.save");
        GZIPOutputStream gz = new GZIPOutputStream(fos);
        ObjectOutputStream os = new ObjectOutputStream(gz);

        List<String> save = ANN.save();
        for(String s : save)
            os.writeObject(s);

        // serialize the objects sarah and sam
        os.flush();
        os.close();
        fos.close();
    }

    private static void compute() throws Exception {
        // Get file list
        String[] files = new File("ANN/res/").list((file, s) -> s.matches(".*\\.[01234]\\.invitee"));

        float[][] dataTable = readFile("ANN/res/" + files[(int)(Math.random()*files.length)]);

        double[] values = ANN.getOutputFor(dataTable);
        double lastError = ANN.getLastComputedError(new double[]{0,0,0,1,0});

        int rk = 0;
        for(int i=1; i<values.length; i++) {
            if(values[i] > values[rk])
                rk = i;
        }

        double error = 1;
        for(int iter=0; iter<10 && error > 0; iter++) {
            String filename = files[(int)(Math.random()*files.length)];
            int level = Integer.parseInt(filename.substring(filename.length()-"x.invitee".length(), filename.length()-".invitee".length()));
            System.out.print("Iterating " + (iter + 1)+" with "+filename);
            dataTable = readFile("ANN/res/" + filename);
            error = ANN.learn(dataTable, new double[]{level==0?1:0,level==1?1:0,level==2?1:0,level==3?1:0,level==4?1:0});
            System.out.printf(", Error: %2.5f%% ( %+1.4e )\n", error * 100, error-lastError);
            lastError = error;
        }
    }

    private static float[][] readFile(String filePath) throws IOException {
        //Ouverture du fichier
        FileReader input = new FileReader(filePath);
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

        return dataTable;
    }
}