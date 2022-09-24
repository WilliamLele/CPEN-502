import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
 */
public class NeuralNet implements NeuralNetInterface{
    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double targetOutput) {
        // for each pattern of an epoch
        // 1. forward

        // 2. backward

        // 3. update weights

        // return the error of current input
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

    /**
     * Performs a forward propagation.
     * That is, computes weighted sums Si and activations yi for all cells
     * @param X
     * @return The output for the input vector
     */
    private double forward(double[] X){
        // return outputFor(X);
        return 0;
    }

    /**
     * Performs a backword propagation
     * That is, computes error signal \delta for each neuron
     * @param X The input vector
     * @param targetOutput The target output
     */
    public void backward(double[] X, double targetOutput){

    }

    /**
     * Updates weights with momentum
     */
    public void updateWeights(){

    }

    private double calculateError(double realOutput, double targetOutput){
        // return E = sum((yi-Ci)^2)/2
        return 0;
    }
}
