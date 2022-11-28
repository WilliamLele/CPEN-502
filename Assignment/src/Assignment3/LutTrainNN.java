package Assignment3;

import java.util.Scanner;
/**
 * This class trains the neural network using the contents of the LUT from Assignment 2
 * The aim is to find a set of hyper-parameters (i.e. momentum, learning rate, # of hidden neurons)
 */
public class LutTrainNN {
    static final double THRESHOLD = 0.05;

    double[][] trainingSet;
    double[] targetSet;
    double totalError;

    public static void main(String[] args){

        double learningRate, momentum;
        int numHidden;

        //User input on training parameters
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter Learning Rate: ");
        learningRate = userInput.nextDouble();

        System.out.print("Enter Momentum: ");
        momentum = userInput.nextDouble();

        System.out.print("Enter number of hidden nodes: ");
        numHidden = userInput.nextInt();


    }

}
