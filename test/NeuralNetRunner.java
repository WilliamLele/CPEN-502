/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
 */
public class NeuralNetRunner {
    static final double THRESHOLD = 0.05;

    double[][] trainingSet;
    double[] targetSet;
    double totalError;

    NeuralNet nn = new NeuralNet(
            2,
            4,
            0.2,
            0,
            0,
            1,
            false
    ); // need to pass parameters

    public NeuralNetRunner(boolean isBipolar) {
        totalError = 0;
        if(!isBipolar){
          trainingSet = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
          targetSet = new double[]{0, 1, 1, 0};
        } else{
        trainingSet = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        targetSet = new double[]{-1, 1, 1, -1};
        }

        nn.initializeWeights();
    }

    private void printInfo(){

    }

    private void train(){

    }

    public void run(){
        totalError = THRESHOLD + 1;
        // outer loop: to check whether meet the break condition
        int epoch = 0;
        while(totalError > THRESHOLD && epoch < 5000){
            // (1) reach the number of epochs we want (2) total error is acceptable
            totalError = 0;
            // inner loop: train the training set once
            for (int i = 0; i < targetSet.length; ++i) {
                double error = nn.train(trainingSet[i], targetSet[i]);
                totalError += error;
            }
            System.out.println("The total error of " + ++epoch +" epoch: " + totalError);
        }
    }

    public static void main(String[] args) {
        NeuralNetRunner runner = new NeuralNetRunner(false);
        runner.run();
    }
}
