import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
 */
public class NeuralNet implements NeuralNetInterface{

    static final double RANDOM_MIN = -0.5;
    static final double RANDOM_MAX = 0.5;

    private int numInputs;
    private int numHidden;
    private double rho;
    private double alpha;
    private double minQ;
    private double maxQ;

    double[] hiddenValue;
    private double[][] weightsInputToHidden;
    private double[] weightHiddenToOutput;
    private double[][] previousWeightsInputToHidden;
    private double[] previousWeightHiddenToOutput;

    boolean bipolar;

    /**
     *
     * @param numInputs The number of inputs in input vector
     * @param numHidden The number of hidden neurons in hidden layer. Only a single hidden layer is supported
     * @param learningRate The learning rate coefficient
     * @param momentumTerm The momentum coefficient
     * @param lowerBound The lower bound of sigmoid used by the output neuron only ??????
     * @param upperBound The upper bound of sigmoid used by the output neuron only ??????
     * @param isBipolar Flag to indicate if it is binary xor or bipolar xor
     */
    public NeuralNet(
            int numInputs,
            int numHidden,
            double learningRate,
            double momentumTerm,
            double lowerBound,
            double upperBound,
            boolean isBipolar){

            this.numInputs = numInputs;
            this.numHidden = numHidden;
            this.rho = learningRate;
            this.alpha = momentumTerm;
            this.minQ = lowerBound;
            this.maxQ = upperBound;

            this.hiddenValue = new double[numHidden];
            this.weightsInputToHidden = new double[numHidden][numInputs + 1]; // +1 for bias input
            this.weightHiddenToOutput = new double[numHidden + 1]; // +1 for bias input

            this.previousWeightsInputToHidden = new double[numHidden][numInputs + 1]; // +1 for bias input
            this.previousWeightHiddenToOutput = new double[numHidden + 1]; // +1 for bias input

            this.bipolar = isBipolar;
    }

    @Override
    public double outputFor(double[] X) {
        // input to hidden
        for(int i=0; i<weightsInputToHidden.length; ++i){
            double sumInputToHidden = weightsInputToHidden[i][0] * BIAS_INPUT;
            for(int j=1; j<weightsInputToHidden[0].length; ++j){
                    sumInputToHidden += weightsInputToHidden[i][j] * X[j-1];
            }
            hiddenValue[i] = customSigmoid(sumInputToHidden);
        }
        // hidden to output
        double sumHiddenToOutput = weightHiddenToOutput[0] * BIAS_INPUT;
        for(int i=1; i<weightHiddenToOutput.length; ++i){
                sumHiddenToOutput += weightHiddenToOutput[i] + hiddenValue[i-1];
        }

        return customSigmoid(sumHiddenToOutput);
    }

    @Override
    public double train(double[] X, double targetOutput) {
        // for each pattern of an epoch
        // 1. forward
        double realOutput = outputFor(X);
        // 2. backward: calculate error signal for each neuron
        double deltaForOutput;
        double[] deltaForHidden = new double[numHidden]; // no need to +1 for bias

        deltaForOutput = (realOutput - targetOutput) * derivativeOfCustomSigmoid(realOutput);
        for(int i=0; i<deltaForHidden.length; ++i){
            deltaForHidden[i] = derivativeOfCustomSigmoid(hiddenValue[i]) * deltaForOutput;
        }

        // 3. update current weights & previous weights
        for(int i=0; i<weightsInputToHidden.length; ++i){
            for(int j=0; j<weightsInputToHidden[0].length; ++j){
                double curr = weightsInputToHidden[i][j];
                double prev = previousWeightsInputToHidden[i][j];
                double x = (j == 0) ? BIAS_INPUT : X[j-1];
                weightsInputToHidden[i][j] += alpha * (curr - prev) + rho * deltaForHidden[i] * x;
                previousWeightsInputToHidden[i][j] = curr;
            }
        }
        for(int i=0; i<weightHiddenToOutput.length; ++i){
            double curr = weightHiddenToOutput[i];
            double prev = previousWeightHiddenToOutput[i];
            double x = (i == 0) ? BIAS_INPUT : hiddenValue[i-1];
            weightHiddenToOutput[i] += alpha * (curr - prev) + rho * deltaForOutput * x;
        }
        // return the error of current input
        return 0.5 * (realOutput - targetOutput) * (realOutput - targetOutput);
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }

    @Override
    public double binarySigmoid(double x) {
        return 1/( 1 + Math.pow(Math.E, -x));
    }

    @Override
    public double bipolarSigmoid(double x) {
        return 2/ (1+Math.pow(Math.E, -x)) - 1;
    }

    @Override
    public double customSigmoid(double x) {
        return (maxQ-minQ)/(1+Math.pow(Math.E, -x)) + minQ;
    }

    @Override
    public double derivativeOfCustomSigmoid(double f){
        return (f - minQ) * ((maxQ - f) / (maxQ - minQ));
    }

    @Override
    public void initializeWeights() {
        // initialize weights to random values in [RANDOM_MIN, RANDOM_MAX)
        Random r = new Random();
        //Initialize the weights from inputs to hidden
        for(int i=0; i<weightsInputToHidden.length; ++i){
            for(int j=0; j<weightsInputToHidden[0].length; ++i){
                weightsInputToHidden[i][j] = r.nextDouble() * (RANDOM_MAX - RANDOM_MIN) + RANDOM_MIN;
            }
        }
        //Initialize the weights from hidden to output
        for(int i=0; i<weightHiddenToOutput.length; ++i){
            weightHiddenToOutput[i] = r.nextDouble() * (RANDOM_MAX - RANDOM_MIN) + RANDOM_MIN;
        }


    }

    @Override
    public void zeroWeights() {
        //Initialize the weights from inputs to hidden
        for(double[] weights: weightsInputToHidden){
            Arrays.fill(weights, 0);
        }
        //Initialize the weights from hidden to output
        Arrays.fill(weightHiddenToOutput, 0);
    }

//    /**
//     * Performs a forward propagation.
//     * That is, computes weighted sums Si and activations yi for all cells
//     * @param X
//     * @return The output for the input vector
//     */
//    private double forward(double[] X){
//        // return outputFor(X);
//        return 0;
//    }
//
//    /**
//     * Performs a backword propagation
//     * That is, computes error signal \delta for each neuron
//     * @param X The input vector
//     * @param targetOutput The target output
//     */
//    public void backward(double[] X, double targetOutput){
//
//    }
//
//    /**
//     * Updates weights with momentum
//     */
//    public void updateWeights(){
//
//    }
//
//    private double calculateError(double realOutput, double targetOutput){
//        // return E = sum((yi-Ci)^2)/2
//        return 0.5 * (realOutput - targetOutput) * (realOutput - targetOutput);
//    }
}
