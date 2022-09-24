package Assignment1;

import java.io.File;
import java.io.IOException;

public class NeuralNet implements NeuralNetInterface{

    int numInputs, numHidden;
    double argLearningRate, argMmomentumTerm, argA, argB;

    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double argValue) {
        return 0;
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }

    @Override
    public double sigmoid(double x) {
        return 0;
    }

    @Override
    public double customSigmoid(double x) {
        return 0;
    }

    @Override
    public void initializeWeights() {

    }

    @Override
    public void zeroWeights() {

    }
}
