package Assignment3;

import Assignment1.NeuralNet;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.stream.*;
/**
 * This class trains the neural network using the contents of the LUT from Assignment 2
 * The aim is to find a set of hyper-parameters (i.e. momentum, learning rate, # of hidden neurons)
 */
public class LutTrainNN {
    static final double THRESHOLD = 0.05;

    int maxTrainSet = 1215; //Total number of entries in lut 3x3x3x3x3x5
    int numTrainSet = 0;
    double[][] trainingInput = new double[maxTrainSet][20];
    double[] trainingOutput = new double[maxTrainSet];
    double totalError;

    List<String[]> dataList = new ArrayList<>();

    NeuralNet lutNN;

    //Constuctor
    public LutTrainNN(double learningRate, double momentum, int numHidden){
        lutNN = new NeuralNet(trainingInput[0].length, numHidden, learningRate, momentum, -1, 1, true);
        lutNN.initializeWeights();
    }

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

        LutTrainNN trainer = new LutTrainNN(learningRate, momentum, numHidden);
        try {
            trainer.load();
        }catch (IOException e){
            e.printStackTrace();
        }

        trainer.run();
    }

    /**
     * Read lut.txt and load data into trainingInput & trainingOutput
     * @throws IOException
     */
    public void load() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("/Users/Backspace/robocode/robots/Assignment3/RobotLUT.data/lut.txt"));
        String line = reader.readLine();

        int row = 0;

        try {
            for (int i = 0; i < trainingInput.length; i++) {
                String splitLine[] = line.split("\t");           //lut.txt format:    000000 tab 0.0 tab 0
                int visitCount = Integer.parseInt(splitLine[2]); //If visits in lut.txt is 0, then it will not be used to train
                //if (visitCount > 0) {
                    oneHotEncoding(row, 0, Double.parseDouble(splitLine[0].substring(0, 1)));
                    oneHotEncoding(row, 1, Double.parseDouble(splitLine[0].substring(1, 2)));
                    oneHotEncoding(row, 2, Double.parseDouble(splitLine[0].substring(2, 3)));
                    oneHotEncoding(row, 3, Double.parseDouble(splitLine[0].substring(3, 4)));
                    oneHotEncoding(row, 4, Double.parseDouble(splitLine[0].substring(4, 5)));
                    oneHotEncoding(row, 5, Double.parseDouble(splitLine[0].substring(5, 6)));

                    trainingOutput[row] = Double.parseDouble(splitLine[1]);

                    row++;
                //}
                line = reader.readLine();
            }
            //Normalize/rescale the Q-value to {-1, 1}
            for (int i = 0; i < row; i++) {
                trainingOutput[i] = lutNN.bipolarSigmoid(trainingOutput[i]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            reader.close();
        }
        /**
         * Debug
         */
        /*for(int i=0; i<1215; i++) {
            for(int j=0; j<20; j++) {
                System.out.print(trainingInput[i][j] + " ");
            }
            System.out.println("  Qvalue:"+trainingOutput[i]);
        }
        System.out.println("LENGTH: "+trainingInput[0].length);
         */
    }

    public void run(){
        totalError = THRESHOLD + 1;
        double RMSError = Double.MAX_VALUE;
        // outer loop: to check whether meet the break condition
        int epoch = 0;
        while(RMSError > THRESHOLD){
            // (1) reach the number of epochs we want (2) total error is acceptable
            totalError = 0;
            // inner loop: train the training set once
            for (int i = 0; i < trainingOutput.length; i++) {
                double error = lutNN.train(trainingInput[i], trainingOutput[i]);
                totalError += error * 2; //Since previously we calculate the total error, now is RMSError
                RMSError = Math.sqrt(totalError/trainingOutput.length);
            }
            System.out.println("The total error of " + totalError + " RMSError " + RMSError + " epoch: " + ++epoch);
            dataList.add(new String[]{Double.toString(totalError), Double.toString(RMSError), Integer.toString(epoch)});
            writeCsvFile("../Assignment 3", dataList);
        }
    }

    public void oneHotEncoding(int rowIndex, int columnIndex, double inputValue){
        if(columnIndex < 5){        //means state, every state has 3 inputs
            if(inputValue == 0) {
                trainingInput[rowIndex][columnIndex * 3] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
            }
            else if(inputValue == 1){
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
            }
            else{
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = 1.0;
            }
        }
        else{                       //columnIndex == 5, this means action, every action has 5 inputs
            if(inputValue == 0){
                trainingInput[rowIndex][columnIndex * 3] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 4] = -1.0;
            }
            else if(inputValue == 1){
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 4] = -1.0;
            }
            else if(inputValue == 2){
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 4] = -1.0;
            }
            else if(inputValue == 3){
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 3] = 1.0;
                trainingInput[rowIndex][columnIndex * 3 + 4] = -1.0;
            }
            else{
                trainingInput[rowIndex][columnIndex * 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 1] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 2] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 3] = -1.0;
                trainingInput[rowIndex][columnIndex * 3 + 4] = 1.0;
            }
        }
    }

    //Convert a string into csv content, separated by comma
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    //Special character, e.g., comma
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    //Write csv file
    public void writeCsvFile(String filePath, List<String[]> dataLineList) {
        File csvOutputFile = new File(filePath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLineList.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
