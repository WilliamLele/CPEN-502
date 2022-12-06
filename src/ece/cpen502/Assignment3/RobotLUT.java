package ece.cpen502.Assignment3;

import ece.cpen502.Assignment2.State;
import ece.cpen502.utils.LogFile;
import robocode.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class RobotLUT extends AdvancedRobot {

    public enum Operation {SCAN, PERFORM_ACTION};

    public static double terminalReward = 1.0;
    public static int targetNumRound = 8000;

    // Total rounds, win rounds and win percentage
    public static int totalRounds = 0;
    public static int countRounds = 0;
    public static int winRounds = 0;
    public static int roundsToCount = 100;
    public static double totalRewardsPerCount = 0;
    public static List<Double> winRateList = new LinkedList<>();
    public static List<Double> totalRewardsPerCountList = new LinkedList<>();
    public static double highestWinRate = 0;

    // Initialize the current and previous state
    private State currentState = new State();
    private State.Action currentAction = currentState.action;
    private State previousState = new State();
    private State.Action previousAction = previousState.action;

    private double enemyBearing = 0;

    private Operation operationMode = Operation.SCAN;

    private double currentReward;

    private double gamma = 1;
    private double alpha = 0;
    private double epsilon_initial = 1;
    private double epsilon = 1;

    public static boolean immediateReward = true;
    public static boolean onPolicy = false;
    public static boolean decayEpsilon = true;

    public static LUT_new lut = new LUT_new(
            State.XPOS_NUM, State.YPOS_NUM,
            State.ENERGY_NUM, State.DISTANCE_NUM,
            State.ENERGY_NUM, State.ACTION_NUM
    );

    static boolean startBattle = true;

    static LogFile log = null;
    int[] actionCount = new int[5];
    static String fileInfo = null;
    static String enemyName = null;

    public void run(){

        /**
         * A battle contains multiple rounds
         * run() will be called at start of each round
         * Therefore, only load the LUT file at the start of battle, other than start of each round
         */

        if(startBattle){
            if(log == null){
                fileInfo = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                log = new LogFile(getDataFile(this.getClass().getSimpleName()+"-"+fileInfo+".log"));
            }
            log.stream.println("++++parameters++++");
            log.stream.println("alpha="+alpha+"\ngamma="+gamma+"\nepsilon="+epsilon+"\nimmediate_reward="+immediateReward+
                    "\non_policy="+onPolicy+"\ndecay_epsilon="+decayEpsilon+"\nrounds_per_count="+roundsToCount);
            log.stream.println("++++results++++");
            log.stream.println("total_rounds, wins_per_count, total_rewards_per_count, up, down, left, right, fire");
            log.stream.flush();
        }
        startBattle = false;

        if(decayEpsilon && epsilon > 0){
            if(totalRounds <= targetNumRound){
                epsilon = epsilon_initial * (1 - totalRounds * 1.0 / targetNumRound);
            }
            else{
                epsilon = 0;
            }
        }
        else{
            if(totalRounds > targetNumRound){
                epsilon = 0;
            }
        }

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while(true){
            switch (operationMode){
                case SCAN:{
                    currentReward = 0;
                    setTurnRadarRight(360);//turnRadarLeft(90);
                    execute();
                    break;
                }
                case PERFORM_ACTION:{
                    if(Math.random() < epsilon){
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
                            ++actionCount[0];
                            setAhead(100);
                            execute();
                            break;
                        }
                        case down:{
                            ++actionCount[1];
                            setBack(100);
                            execute();
                            break;
                        }
                        case left:{
                            ++actionCount[2];
                            setTurnLeft(90);
                            setAhead(100); //？
                            execute();
                            break;
                        }
                        case right:{
                            ++actionCount[3];
                            setTurnRight(90);
                            setAhead(100); //？
                            execute();
                            break;
                        }
                        case fire:{
                            ++actionCount[4];
                            turnGunRight(getHeading() - getGunHeading() + enemyBearing);
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
        totalRewardsPerCount += currentReward;
        return priorQ + alpha * (currentReward + gamma * currentQ - priorQ);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        if(enemyName == null){
            enemyName = e.getName();
        }
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
        enemyBearing = e.getBearing();

        // switch operation mode
        operationMode = Operation.PERFORM_ACTION;
    }

    @Override
    public void onWin(WinEvent e) {
        ++winRounds;
        currentReward = terminalReward;
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
            currentReward = terminalReward/4;
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if(immediateReward){
            currentReward = -terminalReward/4;
        }
    }

    @Override
    public void onDeath(DeathEvent e) {
        currentReward = -terminalReward;

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
    public void onBulletMissed(BulletMissedEvent event) {
        if(immediateReward){
            currentReward = -terminalReward/4;
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if(immediateReward){
            currentReward = -terminalReward/4;
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        // statistics
        ++totalRounds;
        ++countRounds;
        if(countRounds == roundsToCount){
            double currentWinRate = winRounds*1.0/roundsToCount;
            System.out.println(totalRounds+ "-->win rate: " + currentWinRate + " per "+roundsToCount + ", total rewards: "+ totalRewardsPerCount);
            log.stream.printf("%4d, %2d, %2.3f, %2d, %2d, %2d, %2d, %2d\n",
                    totalRounds, winRounds, totalRewardsPerCount, actionCount[0], actionCount[1], actionCount[2], actionCount[3], actionCount[4]);
            log.stream.flush();

            // save the lut that has the highest win rate
            if(currentWinRate > highestWinRate){
                highestWinRate = currentWinRate;
                lut.save(getDataFile(this.getClass().getSimpleName()+"-"+fileInfo+".txt"));
            }

            winRateList.add(winRounds*1.0/roundsToCount);
            totalRewardsPerCountList.add(totalRewardsPerCount);
            countRounds = 0;
            winRounds = 0;
            totalRewardsPerCount = 0;
            Arrays.fill(actionCount, 0);
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        log.stream.println("++++statistics++++");
        log.stream.println("highest_win_rate="+highestWinRate);
        log.stream.println("enemy_name="+enemyName);
        ListIterator<Double> it = winRateList.listIterator();
        while (it.hasNext()) {
            log.stream.print(it.next() + " ");
            log.stream.flush();

        }
        log.stream.print("\n");
        log.stream.flush();
        it = totalRewardsPerCountList.listIterator();
        while (it.hasNext()) {
            log.stream.print(it.next() + " ");
            log.stream.flush();
        }
        log.stream.print("\n");
        log.stream.flush();
        log.stream.close();

        if(highestWinRate == 0){
            lut.save(getDataFile(this.getClass().getSimpleName()+"-"+fileInfo+".txt"));
        }
    }
}