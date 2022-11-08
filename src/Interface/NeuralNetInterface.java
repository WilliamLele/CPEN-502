package Interface;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
 */
public interface NeuralNetInterface extends CommonInterface{

    public static final double BIAS_INPUT = 1.0; // The input for each neuron's bias weight

    /**
     * Implements a binary sigmoid
     * @param x The input
     * @return f(x) = 1/(1+e^(-x))
     */
    public double binarySigmoid(double x);

    /**
     * Implements a bipolar sigmoid
     * @param x The input
     * @return f(x) = 2/(1+e^(-x)) - 1
     */
    public double bipolarSigmoid(double x);

    /**
     * Implements a general sigmoid with asymptotes bounded by (a,b)
     * @param x The input
     * @return f(x) = b_minus_a / (1 + e^(-x)) - minus_a
     */
    public double customSigmoid(double x);

    /**
     * Used for calculate the error signal for each neuron
     * @param x
     * @return
     */
    public double derivativeOfCustomSigmoid(double x);

    /**
     * Initialize the weights to random values
     */
    public void initializeWeights();

    /**
     * Initialize the weights to 0
     */
    public void zeroWeights();
}
