import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Scanner;

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
    boolean bipolar;
    ArrayList<String> errors = new ArrayList<>();
    NeuralNet nn;
    // to mark whether we can get a convergence
    boolean convergence;

    public NeuralNetRunner(boolean isBipolar) {
        totalError = 0;
        if(!isBipolar){
            trainingSet = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            targetSet = new double[]{0, 1, 1, 0};
            nn = new NeuralNet(
                    2,
                    4,
                    0.2,
                    0,
                    0,
                    1,
                    false
            );
        } else{
            trainingSet = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            targetSet = new double[]{-1, 1, 1, -1};
            nn = new NeuralNet(
                    2,
                    4,
                    0.2,
                    0.9,
                    -1,
                    1,
                    true
            );
        }
        bipolar = isBipolar;
        nn.initializeWeights();
    }

    private void printInfo(){

    }

    private void train(){

    }

    private void saveErrors(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".csv"));
        StringBuilder sb = new StringBuilder();
        for(String error: errors){
            sb.append(error);
            sb.append("\n");
        }

        bw.write(sb.toString());
        bw.close();
    }

    public void run() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.print("How many attempts you want to make: ");
        int attempts = input.nextInt();
        // (1) reach the number of epochs we want
        int sum = 0;
        int n = 0;
        for(int k=0; k<attempts; ++k) {
            totalError = THRESHOLD + 1;
            convergence = true;
            // outer loop: to check whether meet the break condition
            int epoch = 0;
            while (totalError > THRESHOLD && convergence) {
                // (2) total error is acceptable
                totalError = 0;
                // inner loop: train the training set once
                for (int i = 0; i < targetSet.length; ++i) {
                    double error = nn.train(trainingSet[i], targetSet[i]);
                    totalError += error;
//                System.out.println(error);
                }
                System.out.println("Attempt: " + (k+1) +" The total error of " + totalError + " epoch: " + ++epoch);
                errors.add(epoch + "," + totalError);
                // to mark if we cannot get a convergence, and then break the loop
                if(epoch > 20000){
                    convergence = false;
                }
            }

            if(convergence){
                sum += epoch;
                ++n;
            }
            saveErrors("statistics" + File.separator + (bipolar ? "bipolar_":"binary_") + "total_error_" + (k+1));
            errors.clear();
            nn.initializeWeights();
        }
        System.out.println("Perform " + attempts + " trials and get a convergence at " + n
                + " trials. On average, it takes " + sum/n + " epochs each time.");
    }

    public static void main(String[] args) throws IOException {
        NeuralNetRunner runner = new NeuralNetRunner(false);
        runner.run();
    }
}
