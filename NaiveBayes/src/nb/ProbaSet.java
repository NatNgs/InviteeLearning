package nb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathael on 02/12/16.
 */
public class ProbaSet {
    // [ms][type][interval]
    private List<List<List<Probability>>> probabilities = new ArrayList<>();

    public void treatFile(String fileName, boolean isACut, double data[/*500*/][/*6*/]) {

        for(int ms=0; ms<data.length; ms++) {
            while(probabilities.size() <= ms) {
                probabilities.add(new ArrayList<>());
            }
            List<List<Probability>> l_ms = probabilities.get(ms);

            double ms_data[/*6*/] = data[ms];
            for(int type=0; type<ms_data.length; type++) {
                while(l_ms.size() <= type) {
                    l_ms.add(new ArrayList<>());
                }
                List<Probability> l_type = l_ms.get(ms);

                double value = ms_data[type];
                int interval = Probability.intervalIndexFor(type, value);
                while(l_type.size() <= interval) {
                    l_type.add(new Probability(ms,type,interval));
                }
                Probability p = l_type.get(interval);

                p.addFileAs(fileName,isACut);
            }
        }

    }
}
