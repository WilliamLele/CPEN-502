package ece.cpen502.Assignment2;

import ece.cpen502.Interface.LUTInterface;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.util.Random;

public class LUT implements LUTInterface {

    private final int myXSize;
    private final int myYSize;
    private final int myEnergySize;
    private final int distanceToEnemySize;
    private final int enemyEnergySize;
    private final int actionSize;

    //LUT
    private final double[][][][][][] lookUpTable;
    //visits is used for tracking used actions
    private final int[][][][][][] visits;

    //Constructor
    public LUT(int myXSize, int myYSize, int myEnergySize, int distanceToEnemySize, int enemyEnergySize, int actionSize){
        this.myXSize = myXSize;
        this.myYSize = myYSize;
        this.myEnergySize = myEnergySize;
        this.distanceToEnemySize = distanceToEnemySize;
        this.enemyEnergySize = enemyEnergySize;
        this.actionSize = actionSize;

        lookUpTable = new double[myXSize][myYSize][myEnergySize][distanceToEnemySize][enemyEnergySize][actionSize];
        visits = new int[myXSize][myYSize][myEnergySize][distanceToEnemySize][enemyEnergySize][actionSize];
        this.initialiseLUT();
    }

    @Override
    public void initialiseLUT() {
        for(int a = 0; a < myXSize; a++){
            for(int b = 0; b < myYSize; b++){
                for(int c = 0; c < myEnergySize; c++){
                    for(int d = 0; d < distanceToEnemySize; d++){
                        for(int e = 0; e < enemyEnergySize; e++){
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

    public int explore(){
        Random r = new Random();
        return r.nextInt(actionSize);
    }

    public int greedy(int xpos, int ypos, int mEnergy, int distance, int eEnergy){
        double max = -1.0;
        int actionIndex = -1;
        for(int i = 0; i < actionSize; i++){
            if(lookUpTable[xpos][ypos][mEnergy][distance][eEnergy][i] > max){
                max = lookUpTable[xpos][ypos][mEnergy][distance][eEnergy][i];
                actionIndex = i;
            }
        }
        return actionIndex;
    }

    //Return the Q value based on state index array x
    @Override
    public double outputFor(double[] X) {
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
            return lookUpTable[a][b][c][d][e][f];
        }
    }

    //Update LUT
    @Override
    public double train(double[] X, double targetOutput) {
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
        return 0;
    }

    @Override
    public void save(File argFile) throws IOException {

    }

    @Override
    public void load(File argFileName) throws IOException {

    }

    public int visit(double[] x) throws ArrayIndexOutOfBoundsException {
        if(x.length != 6){
            throw new ArrayIndexOutOfBoundsException();
        }
        else {
            int a = (int)x[0];
            int b = (int)x[1];
            int c = (int)x[2];
            int d = (int)x[3];
            int e = (int)x[4];
            int f = (int)x[5];
            return visits[a][b][c][d][e][f];
        }
    }

    public void print(){
        for(int a = 0; a < myXSize; a++){
            for(int b = 0; b < myYSize; b++){
                for(int c = 0; c < myEnergySize; c++){
                    for(int d = 0; d < distanceToEnemySize; d++){
                        for(int e = 0; e < enemyEnergySize; e++){
                            for(int f = 0; f < actionSize; f++){
                                System.out.printf("+++{%d, %d, %d, %d, %d, %d} = %2.3f, visits %d\n",
                                        a,b,c,d,e,f, lookUpTable[a][b][c][d][e][f], visits[a][b][c][d][e][f]);
                            }
                        }
                    }
                }
            }
        }
    }
}
