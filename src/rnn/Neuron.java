package rnn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by nathael on 29/11/16.
 */
class Neuron implements Serializable {
    /**
     * Learning ratio, should be in ]0;1[ (0 AND 1 excluded)
     * how many error has to be corrected at each learning
     */
    private static final double RATIO_SYNAPSE = .1;
    /**
     * Ratio of data reinforcing, should be in ]0;1[ (0 AND 1 excluded)
     */
    private static final double RATIO_NEURON = .9999;
    private static int idNumber = -1;

    private final Map<Neuron, Double> parents = new HashMap<>();
    private boolean hasCalculated;
    private double computedValue;
    private final int id = (++idNumber);

    void addParent(Neuron... newParents) {
        for (Neuron parent : newParents)
            addParent(parent, Math.random() * 2 - 1);
    }
    void addParent(Neuron newParent, double synapseValue) {
        parents.put(newParent, limitValue(synapseValue));
    }

    double getOutput() throws ExecutionException, InterruptedException {
        if (!hasCalculated) {
            double total = 0;

            Set<FutureTask<Double>> futures = new HashSet<>();
            for (final Map.Entry<Neuron, Double> entry : parents.entrySet()) {
                FutureTask<Double> future = new FutureTask<>(() -> entry.getKey().getOutput() * entry.getValue());
                futures.add(future);
                //Thread t = new Thread(future); // TODO for now, doing all in parallelled thread
                //t.run();
                future.run();
            }

            for (FutureTask<Double> futureTask : futures) {
                total += futureTask.get();
            }

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

    private double limitValue(double input) {
        if((input < 1e100 && input > 1e-100)
            || (input > -1e100 && input < 1e-100))
            return input;
        else if(input > 1)
            return 1e100;
        else if(input < -1)
            return -1e100;
        else if(input >= 0)
            return 1e-100;
        else
            return -1e-100;
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
        double correctedOutput = neededValue * RATIO_SYNAPSE + output * (1 - RATIO_SYNAPSE);
        double reversedCorrectedOutput = reverseFunction(correctedOutput);

        double synapsesSum = 0;
        for (double synapseValue : parents.values()) {
            synapsesSum += limitValue(synapseValue * synapseValue); // limit value to avoid 0
        }

        for (Map.Entry<Neuron, Double> entry : parents.entrySet()) {
            // do nothing for this synapse if it previous neuron value is 0
            if(entry.getValue()==0)
                continue;
            double value = reversedCorrectedOutput * (entry.getValue() * entry.getValue() + 1e-100) / synapsesSum;

            // correcting synapses values
            value /= entry.getKey().getOutput();
            parents.put(entry.getKey(), limitValue(value));
        }
    }

    private void propagateLearning(double neededValue) throws ExecutionException, InterruptedException {
        double output = getOutput();
        double correctedOutput = (neededValue - output) * RATIO_SYNAPSE + output;
        double reversedCorrectedOutput = reverseFunction(correctedOutput);

        double neuronsSum = 0;
        for (Neuron n : parents.keySet()) {
            neuronsSum += n.getOutput() + 1e-100; // +1e-100 to avoid 0
        }

        for (Map.Entry<Neuron, Double> entry : parents.entrySet()) {
            double value = reversedCorrectedOutput * (entry.getKey().getOutput() + 1e-100) / neuronsSum;

            // make parents neurons learn
            value /= parents.get(entry.getKey());
            entry.getKey().learn(value > 1 ? 1 : value < 0 ? 0 : value);
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