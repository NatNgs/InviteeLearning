package nb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nathael on 02/12/16.
 */
public class Probability {
    public static final double MIN_ROT = -Math.PI;
    public static final double MAX_ROT = Math.PI;
    public static final int    INTERVAL_ROT = 256;
    public static final double MIN_ACC = -100.;
    public static final double MAX_ACC = 100.;
    public static final int    INTERVAL_ACC = 2048;

    private final int ms; // 0..499
    private final int type; // 0=rotx, 1=roty, 2=rotz, 3=rel_accx, 4=rel_accy, 5=rel_accz
    private final int interval; // [min;max[ interval rank

    private int nbYes = 0;
    private int nbNo = 0;
    private final Set<String> consideredFiles = new HashSet<>();

    public Probability(final int ms, final int type, final int interval) {
        this.ms = ms;
        this.type = type;
        this.interval = interval;
    }

    public double getProbability(final boolean yes) {
        return (double)(yes?nbYes:nbNo)/(nbYes+nbNo);
    }

    public void addFileAs(final String file, final boolean yes) {
        if(!consideredFiles.contains(file)) {
            consideredFiles.add(file);
            if (yes)
                nbYes++;
            else
                nbNo++;
        }
    }

    public int getMs() {
        return ms;
    }

    public int getType() {
        return type;
    }

    /**
     * @return Minimum acceleration valid for this probability
     */
    public double getMin() {
        if(type<=2) // is a rotation
            return interval *(MAX_ROT-MIN_ROT)/INTERVAL_ROT +MIN_ROT;
        else // is an acceleration
            return interval *(MAX_ACC-MIN_ACC)/INTERVAL_ACC +MIN_ACC;
    }
    /**
     * @return First invalid acceleration after this probability interval
     */
    public double getMax() {
        if(type<=2) // is a rotation
            return (interval +1)*(MAX_ROT-MIN_ROT)/INTERVAL_ROT +MIN_ROT;
        else // is an acceleration
            return (interval +1)*(MAX_ACC-MIN_ACC)/INTERVAL_ACC +MIN_ACC;
    }

    static int intervalIndexFor(int type, double value) {
        if(type <= 2) {
            return (int)((value - MIN_ROT)/(MAX_ROT - MIN_ROT));
        }
        else {
            return (int)((value - MIN_ACC)/(MAX_ACC - MIN_ACC));
        }
    }
}
