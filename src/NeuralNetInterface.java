/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
 */
public interface NeuralNetInterface extends CommonInterface{

    /**
     * Implements a bipolar sigmoid
     * @param x The input
     * @return f(x) = 2/(1+e^(-x)) - 1
     */
    public double sigmoid(double x);

    /**
     * Implements a general sigmoid with asymptotes bounded by (a,b)
     * @param x The input
     * @return f(x) = b_minus_a / (1 + e^(-x)) - minus_a
     */
    public double customSigmoid(double x);

    /**
     * Initialize the weights to random values
     */
    public void initializeWeights();

    /**
     * Initialize the weights to 0
     */
    public void zeroWeights();
}
