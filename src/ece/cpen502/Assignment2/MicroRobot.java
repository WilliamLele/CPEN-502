package ece.cpen502.Assignment2;

import robocode.*;

import java.util.Random;

public class MicroRobot extends AdvancedRobot {

    public enum Operation {SCAN, PERFORM_ACTION};

    // Total rounds, win rounds and win percentage
    public static int totalRounds = 0;
    public static int winRounds = 0;

    // Initialize the current and previous state
    private State currentState = new State();
    private State.Action currentAction = currentState.action;
    private State previousState = new State();
    private State.Action previousAction = previousState.action;

    private Operation operationMode = Operation.SCAN;

    private double currentReward;

    private double gamma = 0.3;
    private double alpha = 0.3;
    private double epsilon_inital = 0.8;
    private double epsilon = 0.8;

    public static boolean immediateReward = true;
    public static boolean onPolicy = true;
    public static boolean decayEpsilon = true;

    public static LUT lut = new LUT(
            State.XPOS_NUM, State.YPOS_NUM,
            State.ENERGY_NUM, State.DISTANCE_NUM,
            State.ENERGY_NUM, State.ACTION_NUM
    );

    public void run(){

        // Todo:anything need to initialize?
        ++totalRounds;
        if(decayEpsilon && epsilon > 0){
            if(totalRounds <= 5000){
                epsilon = epsilon_inital * (1 - totalRounds * 1.0 / 5000);
            }
            else{
                epsilon = 0;
            }
        }
        else{
            if(totalRounds > 100000){
                epsilon = 0;
            }
        }

        while(true){
            switch (operationMode){
                case SCAN:{
                    currentReward = 0;
                    turnRadarLeft(90);
                    break;
                }
                case PERFORM_ACTION:{
                    if(Math.random() <= epsilon){
                        currentAction = selectRandomAction();
                    }
                    else{
                        currentAction = selectBestAction(
                                currentState.myX,
                                currentState.myY,
                                currentState.myEnergy,
                                currentState.distanceToEnemy,
                                currentState.enemyEnergy
                                );
                    }
                    switch (currentAction){
                        case up:{
                            setAhead(100);
                            execute();
                            break;
                        }
                        case down:{
                            setTurnLeft(180);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case left:{
                            setTurnLeft(90);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case right:{
                            setTurnRight(90);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case fire:{
                            fire(3);
                            execute();
                            break;
                        }
                    }

                    // Update Q(S,a)
                    double[] x = new double[]{
                            previousState.myX.ordinal(),
                            previousState.myY.ordinal(),
                            previousState.myEnergy.ordinal(),
                            previousState.distanceToEnemy.ordinal(),
                            previousState.enemyEnergy.ordinal(),
                            previousAction.ordinal()
                    };
                    lut.train(x, computeQ(previousState, currentState, currentReward));
                    // switch operation mode
                    operationMode = Operation.SCAN;
                    break;
                }
            }
        }
    }

    private State.Action selectRandomAction() {
        Random rand = new Random();
        int r = rand.nextInt(State.ACTION_NUM);
        return State.Action.values()[r];
    }

    private State.Action selectBestAction(
            State.xPos myX, State.yPos myY, State.energy myEnergy, State.distance distanceToEnemy, State.energy enemyEnergy
    ) {
        double maxQ = -Double.MAX_VALUE;
        State.Action bestAction = null;
        for(int i=0; i<State.ACTION_NUM; ++i){
            double[] x = new double[]{
                    myX.ordinal(),
                    myY.ordinal(),
                    myEnergy.ordinal(),
                    distanceToEnemy.ordinal(),
                    enemyEnergy.ordinal(),
                    i
            };
            double tempQ = lut.outputFor(x);
            if(tempQ > maxQ){
                maxQ = tempQ;
                bestAction = State.Action.values()[i];
            }
        }
        return bestAction;
    }

    private double computeQ(State previousState, State currentState, double currentReward) {
        double[] previousX = new double[]{
                previousState.myX.ordinal(),
                previousState.myY.ordinal(),
                previousState.myEnergy.ordinal(),
                previousState.distanceToEnemy.ordinal(),
                previousState.enemyEnergy.ordinal(),
                previousState.action.ordinal()
        };
        double priorQ = lut.outputFor(previousX);

        double[] currentX = null;
        if(onPolicy){
            currentX = new double[]{
                currentState.myX.ordinal(),
                currentState.myY.ordinal(),
                currentState.myEnergy.ordinal(),
                currentState.distanceToEnemy.ordinal(),
                currentState.enemyEnergy.ordinal(),
                currentState.action.ordinal()
            };
        }
        else{
            State.Action bestAction = selectBestAction(
                    currentState.myX,
                    currentState.myY,
                    currentState.myEnergy,
                    currentState.distanceToEnemy,
                    currentState.enemyEnergy
            );
            currentX = new double[]{
                    currentState.myX.ordinal(),
                    currentState.myY.ordinal(),
                    currentState.myEnergy.ordinal(),
                    currentState.distanceToEnemy.ordinal(),
                    currentState.enemyEnergy.ordinal(),
                    bestAction.ordinal()
            };
        }
        double currentQ = lut.outputFor(currentX);
        return priorQ + alpha * (currentReward + gamma * currentQ - priorQ);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // capture previous state before updating
        previousState.myX = currentState.myX;
        previousState.myY = currentState.myY;
        previousState.myEnergy = currentState.myEnergy;
        previousState.distanceToEnemy = currentState.distanceToEnemy;
        previousState.enemyEnergy = currentState.enemyEnergy;
        previousAction = currentAction;

        // update current state
        currentState.myX = State.getXPosLevel(getX());
        currentState.myY = State.getYPosLevel(getY());
        currentState.myEnergy = State.getEnergyLevel(getEnergy());
        currentState.distanceToEnemy = State.getDistanceLevel(e.getDistance());
        currentState.enemyEnergy = State.getEnergyLevel(e.getEnergy());

        // switch operation mode
        operationMode = Operation.PERFORM_ACTION;
    }

    @Override
    public void onWin(WinEvent e) {
        ++winRounds;
        currentReward = 1;
        // update Q
        double[] x = new double[]{
                previousState.myX.ordinal(),
                previousState.myY.ordinal(),
                previousState.myEnergy.ordinal(),
                previousState.distanceToEnemy.ordinal(),
                previousState.enemyEnergy.ordinal(),
                previousAction.ordinal()
        };
        lut.train(x, computeQ(previousState, currentState, currentReward));
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        if(immediateReward){
            currentReward = 0.5;
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if(immediateReward){
            currentReward = 0.5;
        }
    }

    @Override
    public void onDeath(DeathEvent e) {
        currentReward = -1;

        // update Q
        double[] x = new double[]{
                previousState.myX.ordinal(),
                previousState.myY.ordinal(),
                previousState.myEnergy.ordinal(),
                previousState.distanceToEnemy.ordinal(),
                previousState.enemyEnergy.ordinal(),
                previousAction.ordinal()
        };
        lut.train(x, computeQ(previousState, currentState, currentReward));
    }
}