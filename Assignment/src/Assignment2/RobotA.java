package Assignment2;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class RobotA extends AdvancedRobot {

    public enum operation {scan, performAction};

    //Bonus and penalty
    private final double immediateBonus = 0.5;
    private final double terminalBonus = 1.0;
    private final double immediatePenalty = -0.3;
    private final double terminalPenalty = -1.0;

    //Total rounds, win rounds and win percentage
    public static int totalRounds = 0;
    public static int winRounds = 0;
    public static double winPercentage = 0.0;

    //Initialize the current and previous state
    private State currentState = new State(State.xPos.left, State.yPos.top, State.energy.high, State.distance.medium, State.energy.high, State.Action.down);
    private State previousState = new State(State.xPos.left, State.yPos.top, State.energy.high, State.distance.medium, State.energy.high, State.Action.down);

    //Initialize operation mode
    private operation operationMode = operation.scan;

    public double myX = 0.0;
    public double myY = 0.0;
    public double myEnergy = 100;
    public double distanceToEnemy = 0.0;
    public double enemyEnergy = 100;

    public static boolean immediateReward = true;
    public static boolean onPolicy = true;

    private double gamma = 0.0; //discount factor
    private double alpha = 0.0; //learning rate
    private double epsilon = 0.0; //random number for the next move
    private double reward = 0.0;
    private double qValue = 0.0;

    //static LUT
    public static LUT lut = new LUT(
            State.XPOS_NUM, State.YPOS_NUM,
            State.ENERGY_NUM, State.DISTANCE_NUM,
            State.ENERGY_NUM, State.ACTION_NUM);

    public void run(){
        while (true){
            ahead(200);
            turnGunRight(180);
            back(200);
            turnGunLeft(360);
        }
    }
    public void onScannedRobot(ScannedRobotEvent e){

        fire(1);

    }
}
