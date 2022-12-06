package Assignment3;

import Assignment1.NeuralNet;
import Assignment2.State;
import Assignment3.ReplayMemory.Experience;
import Assignment3.ReplayMemory.ReplayMemory;
import robocode.*;
import utils.LogFile;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.11.29
 */
public class RobotNN extends AdvancedRobot {

    public enum Operation {SCAN, PERFORM_ACTION};

    public static double terminalReward = 1.0;
    public static int targetNumRound = 9000;

    // Total rounds, win rounds and win percentage
    public static int totalRounds = 0;
    public static int countRounds = 0;
    public static int winRounds = 0;
    public static int roundsToCount = 100;
    public static double totalRewardsPerCount = 0;
    public static int totalWinRounds = 0;
    public static List<Double> winRateList = new LinkedList<>();
    public static List<Double> totalRewardsPerCountList = new LinkedList<>();

    // Initialize the current and previous state
    private State currentState = new State();
    private State.Action currentAction = currentState.action;
    private State previousState = new State();
    private State.Action previousAction = previousState.action;

    private double enemyBearing = 0;

    private Operation operationMode = Operation.SCAN;

    private double currentReward;

    private double gamma = 0;
    private double alpha = 0.1;
    private double epsilon_initial = 0.9;
    private double epsilon = 0.9;

    public static boolean immediateReward = true;
    public static boolean onPolicy = false;
    public static boolean decayEpsilon = true;
    public static boolean replayMemoryMode = true; //5e true
    public static boolean monitorNNLearningMode = true;


    public static int memorySize = 10;
    public final int MAX_SAMPLE_SIZE = 20; //5e n
    public static ReplayMemory<Experience> replayMemory = new ReplayMemory<>(memorySize);

    public static int[] inputDim = {State.XPOS_NUM, State.YPOS_NUM, State.ENERGY_NUM, State.DISTANCE_NUM, State.ENERGY_NUM, State.ACTION_NUM};
    public static int inputNum = State.XPOS_NUM + State.YPOS_NUM + State.ENERGY_NUM + State.DISTANCE_NUM + State.ENERGY_NUM + State.ACTION_NUM;;
    public final double DELTA = 0.000005;

    public static NeuralNet nn = new NeuralNet(
            inputNum, 18, 0.01, 0.01, -1, 1, true
    );

    // monitor nn learning
    public static final double[] nnLearningMonitorInput = {
            0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0
    };
    public static final List<Double> nnLearningMonitorList = new ArrayList<>();
    public static double previousOutput = 0;

    static boolean startBattle = true;

    static LogFile log = null;
    int[] actionCount = new int[5];

    public void run(){

        /**
         * A battle contains multiple rounds
         * run() will be called at start of each round
         * Therefore, only load the LUT file at the start of battle, other than start of each round
         */

        if(startBattle){
            if(log == null){
                String fileInfo = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                log = new LogFile(getDataFile(this.getClass().getSimpleName()+"-"+fileInfo+".log"));
            }
            log.stream.println("++++robot parameters++++");
            log.stream.println("alpha="+alpha+"\ngamma="+gamma+"\nepsilon="+epsilon+"\nimmediate_reward="+immediateReward+
                    "\non_policy="+onPolicy+"\ndecay_epsilon="+decayEpsilon+"\nrounds_per_count="+roundsToCount+
                    "\nreplay_memory_mode="+replayMemoryMode + "\nmonitor_nn_learning="+monitorNNLearningMode);
            log.stream.println("replay_size="+Math.min(memorySize, MAX_SAMPLE_SIZE));
            log.stream.println("++++nn parameters++++");
            log.stream.println("num_inputs="+nn.getNumInputs()+"\nnum_hidden_neurons="+nn.getNumHidden()+
                    "\nrho="+nn.getRho()+"\nalpha="+nn.getAlpha()+"\nmin_q="+nn.getMinQ()+"\nmax_q="+nn.getMaxQ());
            log.stream.println("++++results++++");
            log.stream.println("total_rounds, wins_per_count, total_rewards_per_count, up, down, left, right, fire");
            log.stream.flush();
            nn.initializeWeights();
        }
        startBattle = false;

        System.out.println("++++++++"+totalRounds+"++++++++"+countRounds);
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

        setAdjustRadarForGunTurn(true);
        while(true){
            switch (operationMode){
                case SCAN:{
                    currentReward = 0;
                    turnRadarLeft(90);
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
                            //setAhead(100);
                            execute();
                            break;
                        }
                        case right:{
                            ++actionCount[3];
                            setTurnRight(90);
                            //setAhead(100);
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
                    // todo
                    double[] xScaledOneHotEncoded = scaleVector(oneHotEncodingFor(x));
                    nn.train(xScaledOneHotEncoded, computeQ(previousState, currentState, currentReward));
                    if(replayMemoryMode){
                        replayMemory.add(new Experience(previousState, previousAction, currentReward, currentState));
                        replayExperience();
                    }
                    // switch operation mode
                    operationMode = Operation.SCAN;
                    break;
                }
            }
        }
    }

    private void replayExperience() {
        int memorySize = replayMemory.sizeOf();
        int requestedSampleSize = (memorySize < MAX_SAMPLE_SIZE) ? memorySize : MAX_SAMPLE_SIZE;

        Object[] samples = replayMemory.sample(requestedSampleSize);
        for(Object item: samples){
            Experience e = (Experience) item;

            double[] x = new double[]{
                    e.previousState.myX.ordinal(),
                    e.previousState.myY.ordinal(),
                    e.previousState.myEnergy.ordinal(),
                    e.previousState.distanceToEnemy.ordinal(),
                    e.previousState.enemyEnergy.ordinal(),
                    e.previousAction.ordinal()
            };

            double[] xScaledOneHotEncoded = scaleVector(oneHotEncodingFor(x));
            nn.train(xScaledOneHotEncoded, computeQ(e.previousState, e.currentState, e.currentReward));
        }
    }

    public double[] scaleVector(double[] x) {
        double lowerBound = nn.getMinQ();
        double upperBound = nn.getMaxQ();
        for(int i=0; i<x.length; ++i){
            if(Math.abs(x[i])<DELTA){
                x[i] = lowerBound;
            }else{
                x[i] = upperBound;
            }
        }
        return x;
    }

    public double[] oneHotEncodingFor(double[] x) {
        double[] xONeHotEncoded = new double[inputNum];
        int offset = 0;
        for(int i=0; i<x.length; ++i){
            int pos = (int)x[i];
            xONeHotEncoded[pos+offset] = 1;
            offset += inputDim[i];
        }
        return xONeHotEncoded;
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
            // todo
            double tempQ = nn.outputFor(scaleVector(oneHotEncodingFor(x)));
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
        // todo
        double priorQ = nn.outputFor(scaleVector(oneHotEncodingFor(previousX)));

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
        // todo
        double currentQ = nn.outputFor(scaleVector(oneHotEncodingFor(currentX)));
        totalRewardsPerCount += currentReward;
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
        // todo
        double[] xScaledOneHotEncoded = scaleVector(oneHotEncodingFor(x));
        nn.train(xScaledOneHotEncoded, computeQ(previousState, currentState, currentReward));
        if(replayMemoryMode){
            replayMemory.add(new Experience(previousState, previousAction, currentReward, currentState));
            replayExperience();
        }
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
        // todo
        double[] xScaledOneHotEncoded = scaleVector(oneHotEncodingFor(x));
        nn.train(xScaledOneHotEncoded, computeQ(previousState, currentState, currentReward));
        if(replayMemoryMode){
            replayMemory.add(new Experience(previousState, previousAction, currentReward, currentState));
            replayExperience();
        }
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
        if(monitorNNLearningMode){
            double currentOutput = nn.outputFor(nnLearningMonitorInput);
            if(nnLearningMonitorList.isEmpty()){
                nnLearningMonitorList.add(-1.0);
            } else{
                nnLearningMonitorList.add(Math.abs(currentOutput - previousOutput));
            }
            previousOutput = currentOutput;
        }
        if(countRounds == roundsToCount){
            System.out.println(totalRounds+ "-->win rate: " + winRounds*1.0/roundsToCount + " per "+roundsToCount + ", total rewards: "+ totalRewardsPerCount);
            log.stream.printf("%4d, %2d, %2.3f, %2d, %2d, %2d, %2d, %2d\n",
                    totalRounds, winRounds, totalRewardsPerCount, actionCount[0], actionCount[1], actionCount[2], actionCount[3], actionCount[4]);
            log.stream.flush();

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
        ListIterator<Double> it = winRateList.listIterator();
        while (it.hasNext()) {
            log.stream.print(it.next() + " ");

        }
        log.stream.print("\n");
        it = totalRewardsPerCountList.listIterator();
        while (it.hasNext()) {
            log.stream.print(it.next() + " ");
        }
        log.stream.print("\n");
        log.stream.flush();

        if(monitorNNLearningMode){
            log.stream.println("++++nn learning monitor++++");
            it = winRateList.listIterator();
            while(it.hasNext()){
                log.stream.print(it.next() + " ");
            }
            log.stream.print("\n");
            log.stream.flush();
        }
        log.stream.close();
    }
}