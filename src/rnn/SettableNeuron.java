package rnn;

import java.util.concurrent.ExecutionException;

/**
 * Created by nathael on 29/11/16.
 */
class SettableNeuron extends Neuron {
    private double value = 1;

    @Override
    void addParent(Neuron ... newParent) {
        throw new IllegalAccessError();
    }

    void setValue(double value) {
        this.value = value;
    }

    @Override
    double getOutput() throws ExecutionException, InterruptedException {
        return value;
    }

    @Override
    void learn(double neededValue) throws ExecutionException, InterruptedException {
        // nothing to learn
    }
}
