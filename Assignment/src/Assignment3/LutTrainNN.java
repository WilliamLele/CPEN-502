package Assignment3;

import Assignment1.NeuralNet;
import java.util.Scanner;
import java.io.*;
/**
 * This class trains the neural network using the contents of the LUT from Assignment 2
 * The aim is to find a set of hyper-parameters (i.e. momentum, learning rate, # of hidden neurons)
 */
public class LutTrainNN {
    static final double THRESHOLD = 0.05;

    int maxTrainSet = 1215; //Total number of entries in lut 3x3x3x3x3x5
    int numTrainSet = 0;
    double[][] trainingInput = new double[maxTrainSet][6];
    double[] trainingOutput = new double[maxTrainSet];
    double totalError;

    //Constuctor


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

        //Create and initialize NN
        //NeuralNet lutNN = new NeuralNet(6, numHidden,learningRate, momentum, -1, 1, true); //Bipolar
        //lutNN.initializeWeights();

    }


    public void load() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("lut.txt"));
        String line = reader.readLine();

        int row = 0;

        double minQ = Double.MIN_VALUE;
        double maxQ = Double.MAX_VALUE;

        try {
            for (int i = 0; i < trainingInput.length; i++) {
                String splitLine[] = line.split("\t");           //lut.txt format:    000000 tab 0.0 tab 0
                int visitCount = Integer.parseInt(splitLine[2]); //If visits in lut.txt is 0, then it will not be used to train
                if (visitCount > 0) {
                    trainingInput[row][0] = Double.parseDouble(splitLine[0].substring(0, 1)) + 1; // +1 because lut.txt records the index
                    trainingInput[row][1] = Double.parseDouble(splitLine[0].substring(1, 2)) + 1;
                    trainingInput[row][2] = Double.parseDouble(splitLine[0].substring(2, 3)) + 1;
                    trainingInput[row][3] = Double.parseDouble(splitLine[0].substring(3, 4)) + 1;
                    trainingInput[row][4] = Double.parseDouble(splitLine[0].substring(4, 5)) + 1;
                    trainingInput[row][5] = Double.parseDouble(splitLine[0].substring(5, 6)) + 1;
                    trainingOutput[row] = Double.parseDouble(splitLine[1]);
                    if (trainingOutput[row] < minQ) {
                        minQ = trainingOutput[row];
                    }
                    if (trainingOutput[row] > maxQ) {
                        maxQ = trainingOutput[row];
                    }
                    row++;
                }
                line = reader.readLine();
            }
            //Normalize the Q-value to {-1, 1}
            for (int i = 0; i < row; i++) {
                trainingOutput[i] = (trainingOutput[i] - minQ) * 2 / (maxQ - minQ) - 1;
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }
}
