package Assignment2;

public class State {
    //Enum type
    public enum xPos {left, middle, right};
    public enum yPos {top, center, bottom};
    public enum energy {low, medium, high};
    public enum distance {close, medium, far};
    public enum Action {up, down, left, right, fire};

    public static final int XPOS_NUM = xPos.values().length;
    public static final int YPOS_NUM = yPos.values().length;
    public static final int ENERGY_NUM = energy.values().length;
    public static final int DISTANCE_NUM = distance.values().length;
    public static final int ACTION_NUM = Action.values().length;

    public xPos myX;
    public yPos myY;
    public energy myEnergy;
    public distance distanceToEnemy;
    public energy enemyEnergy;
    public Action action;

    public State(){
        myX = xPos.left;
        myY = yPos.top;
        myEnergy = energy.high;
        distanceToEnemy = distance.medium;
        enemyEnergy = energy.high;
        action = Action.down;
    }

    public State(xPos x, yPos y, energy e1, distance d, energy e2, Action a){
        myX = x;
        myY = y;
        myEnergy = e1;
        distanceToEnemy = d;
        enemyEnergy = e2;
        action = a;
    }

    //Define xPos
    public static xPos getXPosLevel(double x){
        if(x <= 300){
            return xPos.left;
        }
        else if(x <= 500){
            return xPos.middle;
        }
        else{
            return xPos.right;
        }
    }

    //Define yPos
    public static yPos getYPosLevel(double y){
        if(y <= 200){
            return yPos.bottom;
        }
        else if(y <= 500){
            return yPos.center;
        }
        else{
            return yPos.top;
        }
    }

    //Define mEnergy
    public static energy getEnergyLevel(double e){
        if(e <= 33){
            return energy.low;
        }
        else if(e <= 67){
            return energy.medium;
        }
        else{
            return energy.high;
        }
    }

    //Define distance to enemy
    public static distance getDistanceLevel(double d){
        if(d <= 350){
            return distance.close;
        }
        else if(d <= 650){
            return distance.medium;
        }
        else{
            return distance.far;
        }
    }
}
