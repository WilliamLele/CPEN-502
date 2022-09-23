package Assignment1;

public class Perceptron {
    private int numOfInputs;
    private int numOfHiddens;
    private int numOfOutputs;
    private double[] inputs;
    private double[][] weightInputToHidden;
    private double[] weightHiddenToOutput;

    /**Constructor*/
    public Perceptron(int numOfInputs, int numOfHiddens, int numOfOutputs){
        this.numOfInputs = numOfInputs;
        this.numOfHiddens = numOfHiddens;
        this.numOfInputs = numOfOutputs;
        inputs = new double[numOfInputs];
        weightInputToHidden = new double[numOfInputs - 1][numOfHiddens]; // -1 is for bias
        weightHiddenToOutput = new double[numOfHiddens * numOfOutputs];
    }

    /**Set Inputs*/
    public void setInputs(double[] inputVector){
        if(inputVector.length != inputs.length-1){
            throw new ArrayIndexOutOfBoundsException();
        }
        else{
            for(int i=0; i<inputVector.length; i++){
                inputs[i] = inputVector[i];
            }
        }
    }

    /**Get Inputs*/
    public double[] getInputs(){
        return inputs;
    }

    /**Set Weight Input To Hidden*/
    public void setWeightInputToHidden(double[][] weightVector){
        if(weightInputToHidden.length != weightVector.length || weightInputToHidden[0].length !=weightVector[0].length){
            throw new ArrayIndexOutOfBoundsException();
        }
        for(int i=0; i<weightVector.length; i++){
            for(int j=0; j<weightVector[0].length; j++){
                weightInputToHidden[i][j] = weightVector[i][j];
            }
        }
    }

    /**Set Weight Hidden To Output*/
    public void setWeightHiddenToOutput(double[] weightVector){
        if(weightHiddenToOutput.length != weightVector.length){
            throw new ArrayIndexOutOfBoundsException();
        }
        for(int i=0; i<weightVector.length; i++){
            weightHiddenToOutput[i] = weightVector[i];
        }
    }

    /**Get Weight Input To hidden*/
    public double[][] getWeightInputToHidden(){
        return weightInputToHidden;
    }

    /**Get Weight Hidden To Output*/
    public double[] getWeightHiddenToOutput(){
        return weightHiddenToOutput;
    }

    public double forwardPropagation(Perceptron perceptron){
        //Get perception's information
        double[] inputVector = perceptron.getInputs();
        double[][] weightInputToHidden = perceptron.getWeightInputToHidden();
        double[] weightHiddenToOutput = perceptron.getWeightHiddenToOutput();

        double result = 0.0;
        double[] sumInputToHidden = new double[weightInputToHidden[0].length];
        double sumHiddenToOutput = 0.0; // In assignment 1 there is only one output
        double[] hiddenValue = new double[weightInputToHidden[0].length]; // In assignment1, it's 4
        //Calculate the sum for hidden
        for(int i=0; i<weightInputToHidden.length; i++){
            for(int j=0; j<weightInputToHidden[0].length; j++){
                sumInputToHidden[j] += inputVector[i] * weightInputToHidden[i][j];  // sum of wx
            }
        }
        //Calculate y value by applying Sigmoid function
        for(int i=0; i<sumInputToHidden.length; i++){
            hiddenValue[i] = (1/( 1 + Math.pow(Math.E,(-1*(sumInputToHidden[i] + 1))))); // +1 is for bias
        }
        for(int j=0; j<weightHiddenToOutput.length; j++){
            sumHiddenToOutput += hiddenValue[j] * weightHiddenToOutput[j];
        }
        result = (1/( 1 + Math.pow(Math.E,(-1*(sumHiddenToOutput + 1)))));

        return result;
    }

    public void train(double[][] trainingVector, double[] trainingTarget){

    }
}
