import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by nathael on 06/02/17.
 */
public class Maplist extends ArrayList<Map<Maplist.DataType, Integer>> {

    private Maplist() {}
    public Maplist(File in) throws FileNotFoundException {
        this();
        Scanner sc = new Scanner(in);

        // timer,roll,pitch,yaw,accel_x,accel_y,accel_z,posx,posy,posz
        while(sc.hasNextLine()) {
            String newline = sc.nextLine();
            String[] tab = newline.replaceAll(" ","").split(",");

            try {
                Map<DataType, Integer> line = new HashMap<>();
                line.put(DataType.Timer, (int) (Float.valueOf(tab[0]) * 100000));
                line.put(DataType.RotX, (int) (Float.valueOf(tab[1]) * 100000));
                line.put(DataType.RotY, (int) (Float.valueOf(tab[2]) * 100000));
                line.put(DataType.RotZ, (int) (Float.valueOf(tab[3]) * 100000));
                line.put(DataType.AccX, (int) (Float.valueOf(tab[4]) * 100000));
                line.put(DataType.AccY, (int) (Float.valueOf(tab[5]) * 100000));
                line.put(DataType.AccZ, (int) (Float.valueOf(tab[6]) * 100000));
                line.put(DataType.PosX, (int) (Float.valueOf(tab[7]) * 100000));
                line.put(DataType.PosY, (int) (Float.valueOf(tab[8]) * 100000));
                line.put(DataType.PosZ, (int) (Float.valueOf(tab[9]) * 100000));
                this.add(line);
            } catch(NumberFormatException ignored){}
        }
    }

    public int get(DataType dataType, int frameNumber) {
        return this.get(frameNumber).get(dataType);
    }

    @Override
    public Maplist subList(int i, int i1) {
        Maplist sublist = new Maplist();
        sublist.addAll(super.subList(i, i1));

        return sublist;
    }

    enum DataType {
        Timer,
        RotX, RotY, RotZ,
        AccX, AccY, AccZ,
        PosX, PosY, PosZ
    }
}
