package Assignment1;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class NeuralNetRunner {
    static final double THRESHOLD = 0.05;

    double[][] trainingSet;
    double[] targetSet;
    double totalError;

    List<String[]> dataList = new ArrayList<>();

    NeuralNet nn;

    public NeuralNetRunner(boolean isBipolar) {
        totalError = 0;
        if(!isBipolar){
            trainingSet = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            targetSet = new double[]{0, 1, 1, 0};
            nn = new NeuralNet(
                    2,
                    4,
                    0.2,
                    0.0,
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
        while(totalError > THRESHOLD){
            // (1) reach the number of epochs we want (2) total error is acceptable
            totalError = 0;
            // inner loop: train the training set once
            for (int i = 0; i < targetSet.length; ++i) {
                double error = nn.train(trainingSet[i], targetSet[i]);
                totalError += error;
//                System.out.println(error);
            }
            System.out.println("The total error of " + totalError +" epoch: " + ++epoch);
            dataList.add(new String[]{Double.toString(totalError),Integer.toString(epoch)});
            writeCsvFile("../Assignment 1", dataList);
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

    public static void main(String[] args) {
        NeuralNetRunner runner = new NeuralNetRunner(false);
        runner.run();
    }
}