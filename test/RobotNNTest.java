import ece.cpen502.Assignment1.NeuralNet;
import ece.cpen502.Assignment3.RobotNN;
import org.junit.Before;
import org.junit.Test;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.11.29
 */
public class RobotNNTest {

    RobotNN robot;

    @Before
    public void init(){
        robot = new RobotNN();
    }

    @Test
    public void oneHotEncodingFor(){
        double[] res = robot.oneHotEncodingFor(new double[]{0,2,0,0,1,0});
        for(double e: res){
            System.out.print(e + " ");
        }
        System.out.println();
    }

    @Test
    public void scaleVector(){
        double[] res = robot.scaleVector(robot.oneHotEncodingFor(new double[]{0,2,0,0,1,0}));
        for(double e: res){
            System.out.print(e + " ");
        }
        System.out.println();
    }
}
