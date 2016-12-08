package ann;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by nathael on 29/11/16.
 */
class Neuron implements Serializable {
    /**
     * Learning ratio, should be in ]0;1[ (0 AND 1 excluded)
     * how many error has to be corrected at each learning
     */
    private static final double RATIO_SYNAPSE = .001;
    /**
     * Ratio of data reinforcing, should be in ]0;1[ (0 AND 1 excluded)
     */
    private static final double RATIO_NEURON = .001;
    private static int idNumber = -1;

    private final Map<Neuron, Double> parents = new HashMap<>();
    private boolean hasCalculated = false;
    private double computedValue;
    private final int id = (++idNumber);

    void addParent(Neuron... newParents) {
        for (Neuron parent : newParents)
            addParent(parent, Math.random() * 2 - 1);
    }
    void addParent(Neuron newParent, double synapseValue) {
        if(newParent == null)
            throw new RuntimeException();
        parents.put(newParent, limit100(synapseValue));
    }

    double getOutput() throws ExecutionException, InterruptedException {
        if (!hasCalculated) {
            double total = 0;

            //Set<FutureTask<Double>> futures = new HashSet<>();
            for (final Map.Entry<Neuron, Double> entry : parents.entrySet()) {
                /*FutureTask<Double> future = new FutureTask<>(() -> entry.getKey().getOutput() * entry.getValue());
                futures.add(future);
                //Thread t = new Thread(future); // TODO for now, doing all in parallelled thread
                //t.run();
                future.run();*/
                total += entry.getKey().getOutput() * entry.getValue();
            }

            /*for (FutureTask<Double> futureTask : futures) {
                total += futureTask.get();
            }*/

            hasCalculated = true;
            computedValue = activationFunction(total);
        }
        return computedValue;
    }

    private double activationFunction(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    private double reverseFunction(double input) {
        if (input <= 0 || input >= 1 || Double.isNaN(input)) {
            throw new RuntimeException("Reverse function called with value:" + input + " not in ]0;1[.");
        }
        if(Double.isInfinite(input))
            return 0;
        else
            return -Math.log(1 / input - 1);
    }

    private double limit100(double input) {
        if((input < 1e10 && input > 1e-10)
                || (input > -1e10 && input < 1e-10))
            return input;
        else if(input > 1)
            return 1e10;
        else if(input < -1)
            return -1e10;
        else if(input >= 0)
            return 1e-10;
        else
            return -1e-10;
    }
    private double limit01(double input) {
        if(input >= 1)
            return 1-1e-10;
        else if(input <= 0)
            return 1e-10;
        else
            return input;
    }

    private void prepareAlone() {
        this.hasCalculated = false;
    }

    void prepareAll() {
        if (!hasCalculated)
            return;

        prepareAlone();
        parents.keySet().forEach(Neuron::prepareAll);
    }

    void learn(double neededValue) throws ExecutionException, InterruptedException {
        if (neededValue < -1 || neededValue > 1) {
            throw new IllegalArgumentException("NeededValue should be between -1 and 1 (" + neededValue + ")");
        }

        prepareAlone(); // force recompute
        if (neededValue == getOutput())
            return;

        synapsesLearning(neededValue);
        propagateLearning(neededValue);
    }


    private void synapsesLearning(double neededValue) throws ExecutionException, InterruptedException {
        double output = getOutput();
        double correctedOutput = limit01((neededValue - output) * RATIO_SYNAPSE+ output);
        double reversedCorrectedOutput = limit100(reverseFunction(correctedOutput));

        double synapsesSum = 0;
        for (double synapseValue : parents.values()) {
            synapsesSum += limit100(synapseValue * synapseValue); // limit value to avoid 0
        }

        for (Map.Entry<Neuron, Double> entry : parents.entrySet()) {
            if(entry.getKey().getOutput() == 0)
                continue;

            // do nothing for this synapse if it previous neuron value is 0
            double value = reversedCorrectedOutput * limit100(entry.getValue() * entry.getValue()) / synapsesSum;

            // correcting synapses values
            value /= entry.getKey().getOutput();
            parents.put(entry.getKey(), limit100(value));
        }
    }

    private void propagateLearning(double neededValue) throws ExecutionException, InterruptedException {
        double output = getOutput();
        double correctedOutput = limit01((neededValue - output) * RATIO_NEURON + output);
        double reversedCorrectedOutput = limit100(reverseFunction(correctedOutput));

        double neuronsSum = 0;
        for (Neuron n : parents.keySet()) {
            neuronsSum += limit01(n.getOutput());
        }

        for (Map.Entry<Neuron, Double> entry : parents.entrySet()) {
            double value = reversedCorrectedOutput * limit01(entry.getKey().getOutput()) / neuronsSum;

            // make parents neurons learn
            value /= parents.get(entry.getKey());
            value = limit01(value);
            if(value != entry.getKey().getOutput())
                entry.getKey().learn(value);
        }
    }

    Map<Neuron, Double> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        return "N"+String.format("%04X",id);
    }
}