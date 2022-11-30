package ece.cpen502.Assignment3;
/**
 * This LUT_new class maps {states, action} to Q-value
 * Total number of entries in LUT = 3 x 3 x 3 x 3 x 3 x 5 = 1215
 */
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import robocode.*;

public class LUT_new{

    private int myX;                //Dimension for myX
    private int myY;                //Dimension for myY
    private int myEnergy;           //Dimension for myEnergy
    private int distanceToEnemy;    //Dimension for distanceToEnemy
    private int enemyEnergy;        //Dimension for enemyEnergy
    private int actionSize;         //Dimension for actionSize

    //LUT Q-value
    private double[][][][][][] lookUpTable;
    //visits is used for tracking used actions
    private int[][][][][][] visits;

    //Constructor
    public LUT_new(int myX, int myY, int myEnergy, int distanceToEnemy, int enemyEnergy, int actionSize){
        this.myX = myX;
        this.myY = myY;
        this.myEnergy = myEnergy;
        this.distanceToEnemy = distanceToEnemy;
        this.enemyEnergy = enemyEnergy;
        this.actionSize = actionSize;

        lookUpTable = new double[myX][myY][myEnergy][distanceToEnemy][enemyEnergy][actionSize];
        visits = new int[myX][myY][myEnergy][distanceToEnemy][enemyEnergy][actionSize];
        this.initialiseLUT();
    }

    /**
     * Initialize the LUT array to random number between {0, 1} or zero
     * and initialize the visited array to 0
     */
    public void initialiseLUT() {
        for(int a = 0; a < myX; a++){
            for(int b = 0; b < myY; b++){
                for(int c = 0; c < myEnergy; c++){
                    for(int d = 0; d < distanceToEnemy; d++){
                        for(int e = 0; e < enemyEnergy; e++){
                            for(int f = 0; f < actionSize; f++){
                                lookUpTable[a][b][c][d][e][f] = Math.random();
                                visits[a][b][c][d][e][f] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param x The input {state, action} vector
     * @return LUT entry
     */
    public int getVisits(double[] x){
        return visits[(int)x[0]][(int)x[1]][(int)x[2]][(int)x[3]][(int)x[4]][(int)x[5]];
    }

    /**
     * Return the Q value based on state index array x
     * @param X The input {state, action} vector
     * @return The Q-value of the LUT entry
     */
    public double outputFor(double[] X) {
        return lookUpTable[(int)X[0]][(int)X[1]][(int)X[2]][(int)X[3]][(int)X[4]][(int)X[5]];
    }

    //Update LUT
    public void train(double[] X, double targetOutput) {
        if(X.length != 6){
            throw new ArrayIndexOutOfBoundsException();
        }
        else {
            int a = (int)X[0];
            int b = (int)X[1];
            int c = (int)X[2];
            int d = (int)X[3];
            int e = (int)X[4];
            int f = (int)X[5];
            lookUpTable[a][b][c][d][e][f] = targetOutput;
            ++visits[a][b][c][d][e][f];
        }
    }

    /**
     * Write the current LUT to output file
     * @param filename
     */
    public void save(File filename){
        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(filename));
            for (int a = 0; a < myX; a++) {
                for (int b = 0; b < myY; b++) {
                    for (int c = 0; c < myEnergy; c++) {
                        for (int d = 0; d < distanceToEnemy; d++) {
                            for (int e = 0; e < enemyEnergy; e++) {
                                for(int f = 0; f < actionSize; f++){
                                    w.println(a + "" + b + "" + c + "" + d + "" + e + "" + f + "\t" +
                                            lookUpTable[a][b][c][d][e][f] + "\t" +
                                            visits[a][b][c][d][e][f]);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            w.flush();
            w.close();
        }
    }

    /**
     * Read the saved LUT file into the LUT
     * @param filename
     * @throws IOException
     */
    public void load(File filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        try {
            int zz = 0;
            while (line != null) {
                String splitLine[] = line.split("\t");
                int a = Character.getNumericValue(splitLine[0].charAt(0));
                int b = Character.getNumericValue(splitLine[0].charAt(1));
                int c = Character.getNumericValue(splitLine[0].charAt(2));
                int d = Character.getNumericValue(splitLine[0].charAt(3));
                int e = Character.getNumericValue(splitLine[0].charAt(4));
                int f = Character.getNumericValue(splitLine[0].charAt(5));
                lookUpTable[a][b][c][d][e][f] = Double.valueOf(splitLine[1]);
                visits[a][b][c][d][e][f] = Integer.valueOf(splitLine[2]);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }
}