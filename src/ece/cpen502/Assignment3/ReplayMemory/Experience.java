package ece.cpen502.Assignment3.ReplayMemory;

import ece.cpen502.Assignment2.State;

public class Experience {

    public State previousState;
    public State.Action previousAction;
    public double currentReward;
    public State currentState;

    public Experience(State previousState, State.Action previousAction, double currentReward, State currentState){
        this.previousState = previousState;
        this.previousAction = previousAction;
        this.currentReward = currentReward;
        this.currentState = currentState;
    }
}
