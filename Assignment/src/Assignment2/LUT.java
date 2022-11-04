package Assignment2;

import Interface.LUTInterface;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class LUT implements LUTInterface {

    private int myX;
    private int myY;
    private int myEnergy;
    private int distanceToEnemy;
    private int enemyEnergy;
    private int actionSize;

    //LUT
    private double[][][][][][] lookUpTable;
    //visits is used for tracking used actions
    private int[][][][][][] visits;

    //Constructor
    public LUT(int myX, int myY, int myEnergy, int distanceToEnemy, int enemyEnergy, int actionSize){
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

    @Override
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


    public double outputFor(double[] X) {
        return 0;
    }

    //Update LUT
    public void train(int[] inputVector, double targetOutput){
        lookUpTable[inputVector[0]][inputVector[1]][inputVector[2]][inputVector[3]][inputVector[4]][inputVector[5]] = targetOutput;
    }


    public void save(File argFile) throws IOException {

    }


    public void load(String argFileName) throws IOException {

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
}
