package Assignment2;

import robocode.*;
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

    public double previousReward = 0.0; //r
    public double currentReward = 0.0; //r t+1

    public static boolean immediateReward = true;
    public static boolean onPolicy = true;

    private double gamma = 0.0; //discount factor
    private double alpha = 0.0; //learning rate
    private double epsilon = 0.0; //random number for the next move
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

    public double Qvalue(){
        double Q = 0.0;
        double previousQ; //Q(s, a)
        double currentQ;  //Q(s', a')
        int[] tempPrevious = {
                previousState.myX.ordinal(),
                previousState.myY.ordinal(),
                previousState.myEnergy.ordinal(),
                previousState.distanceToEnemy.ordinal(),
                previousState.enemyEnergy.ordinal(),
                previousState.action.ordinal()
        };
        previousQ = lut.outputFor(tempPrevious);

        int[] tempCurrent = {
                currentState.myX.ordinal(),
                currentState.myY.ordinal(),
                currentState.myEnergy.ordinal(),
                currentState.distanceToEnemy.ordinal(),
                currentState.enemyEnergy.ordinal(),
                currentState.action.ordinal()
        };
        currentQ = lut.outputFor(tempCurrent);

        //Sarsa (on-policy)
        if(onPolicy){
            Q = previousQ + alpha * (previousReward + gamma * currentQ - previousQ);
        }
        //Q-learning (off-policy)
        else{
            //Greedy algorithm
            int index = lut.greedy(
                    currentState.myX.ordinal(), currentState.myY.ordinal(),
                    currentState.myEnergy.ordinal(), currentState.distanceToEnemy.ordinal(),
                    currentState.enemyEnergy.ordinal()
            );
            int[] inputVector = {
                    currentState.myX.ordinal(),
                    currentState.myY.ordinal(),
                    currentState.myEnergy.ordinal(),
                    currentState.distanceToEnemy.ordinal(),
                    currentState.enemyEnergy.ordinal(),
                    index
            };
            double max = lut.outputFor(inputVector);
            Q = previousQ + alpha * (currentReward + gamma * max - previousQ);
        }
        return Q;
    }

    public void onScannedRobot(ScannedRobotEvent e){
        fire(1);
    }

    //This method will be called when one of your bullets hits another robot
    public void onBulletHit(BulletHitEvent e){
        if(immediateReward) {
            if (currentReward != previousReward) {
                previousReward = currentReward;
            }
            currentReward += immediateBonus;
        }
    }

    //This method will be called when your robots is hit by a bullet
    public void onHitByBullet(HitByBulletEvent e){
        if(immediateReward) {
            if (currentReward != previousReward) {
                previousReward = currentReward;
            }
            currentReward += immediateBonus;
        }
    }

    //This method will be called when one of your bullets misses (hits a wall)
    public void onBulletMissed(BulletMissedEvent e){
        if(immediateReward) {
            if (currentReward != previousReward) {
                previousReward = currentReward;
            }
            currentReward += immediateBonus;
        }
    }

    //This method will be called if the robot wins a battle
    public void onWin(WinEvent e){
        if(immediateReward) {
            if (currentReward != previousReward) {
                previousReward = currentReward;
            }
            currentReward = terminalBonus;
        }
        totalRounds++;
        winRounds++;
    }

    //This method will be called if the robot dies
    public void onDeath(DeathEvent e){
        if(immediateReward) {
            if (currentReward != previousReward) {
                previousReward = currentReward;
            }
            currentReward += terminalPenalty;
        }
        totalRounds++;
    }
}
