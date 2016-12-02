package nb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathael on 02/12/16.
 */
public class ProbaSet {
    // [ms][type][interval]
    private List<List<List<Probability>>> probabilities = new ArrayList<>();

    public void learn(String fileName, boolean isACut, double data[/*500*/][/*6*/]) {
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

    public double test(double data[/*500*/][/*6*/]) {
        double probaMTrue = 1;
        double probaMFalse = 1;
        double probaATrue = 0;
        double probaAFalse = 0;

        for(int ms=0; ms<data.length; ms++) {
            if(probabilities.size() <= ms) {
                continue; // unknown probabilities
            }
            List<List<Probability>> l_ms = probabilities.get(ms);

            double ms_data[/*6*/] = data[ms];
            for(int type=0; type<ms_data.length; type++) {
                if(l_ms.size() <= type) {
                    continue; // unknown probabilities
                }
                List<Probability> l_type = l_ms.get(ms);

                double value = ms_data[type];
                int interval = Probability.intervalIndexFor(type, value);
                if(l_type.size() <= interval) {
                    continue; // unknown probabilities
                }

                Probability p = l_type.get(interval);

                value = p.getProbability(true);
                probaATrue += value;
                probaMTrue *= value;

                value = p.getProbability(false);
                probaAFalse += value;
                probaMFalse *= value;
            }
        }

        if(probaMTrue==probaMFalse) {
            if(probaATrue == probaAFalse) {
                return .5; // we don't now
            }
            else {
                return probaATrue/(probaATrue+probaAFalse);
            }
        } else {
            return probaMTrue/(probaMTrue+probaMFalse);
        }
    }
}
