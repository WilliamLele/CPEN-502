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

    NeuralNet nn = new NeuralNet(); // need to pass parameters

    public NeuralNetRunner(boolean isBipolar) {
        totalError = 0;
        if(!isBipolar){
          trainingSet = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
          targetSet = new double[]{0, 1, 1, 0};
        } else{
        trainingSet = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        targetSet = new double[]{-1, 1, 1, -1};
        }
    }

    private void printInfo(){

    }

    public void run(){
        // outer loop: to check whether meet the break condition
        while(...){
            // (1) reach the number of epochs we want (2) total error is acceptable
            totalError = 0;
            // inner loop: train the training set once
            for (int i = 0; i < targetSet.length; ++i) {
                totalError += nn.train(trainingSet[i], targetSet[i]);
                printInfo();
            }
        }
    }


    public static void main(String[] args) {
        NeuralNetRunner runner = new NeuralNetRunner(false);
        runner.run();
    }
}
