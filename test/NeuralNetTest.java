import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class NeuralNetTest {

    static final int NUM_INPUT = 2;
    static final int NUM_HIDDEN = 4;
    static final double LEARNING_RATE = 0.2;
    static final double MOMENTUM = 0;
    static final double LOWER_BOUND = 0;
    static final double UPPER_BOUND = 1;
    static final boolean BIPOLAR = false;

    static final int ZERO = 0;
    static final double DELTA = 0.00001;

    NeuralNet nn;

    @Before
    public void init(){
        nn = new NeuralNet(
                NUM_INPUT,
                NUM_HIDDEN,
                LEARNING_RATE,
                MOMENTUM,
                LOWER_BOUND,
                UPPER_BOUND,
                BIPOLAR);
    }

    @Test
    public void outputFor() {
    }

    @Test
    public void binarySigmoid() {
        assertEquals(0.5744425, nn.binarySigmoid(0.3), DELTA);
        assertEquals(0.4255575, nn.binarySigmoid(-0.3), DELTA);
    }

    @Test
    public void bipolarSigmoid() {
    }

    @Test
    public void customSigmoid() {
    }

    @Test
    public void initializeWeights() {
        nn.initializeWeights();
        double[][] weightsInputToHidden = nn.getWeightsInputToHidden();
        assertEquals(NUM_HIDDEN, weightsInputToHidden.length);
        assertEquals(NUM_INPUT + 1, weightsInputToHidden[0].length);
        for(double[] weights: weightsInputToHidden){
            for(double w: weights){
                assertTrue( w < NeuralNet.RANDOM_MAX);
                assertTrue(w >= NeuralNet.RANDOM_MIN);
            }
        }
        double[] weightsHiddenToOutput = nn.getWeightHiddenToOutput();
        assertEquals(NUM_HIDDEN + 1, weightsHiddenToOutput.length);
        for(double w: weightsHiddenToOutput){
            assertTrue(w < NeuralNet.RANDOM_MAX);
            assertTrue(w >= NeuralNet.RANDOM_MIN);
        }
    }

    @Test
    public void zeroWeights() {
        nn.zeroWeights();
        double[][] weightsInputToHidden = nn.getWeightsInputToHidden();
        assertEquals(NUM_HIDDEN, weightsInputToHidden.length);
        assertEquals(NUM_INPUT + 1, weightsInputToHidden[0].length);
        for(double[] weights: weightsInputToHidden){
            for(double w: weights){
                assertEquals(ZERO, w, DELTA);
            }
        }
        double[] weightsHiddenToOutput = nn.getWeightHiddenToOutput();
        assertEquals(NUM_HIDDEN + 1, weightsHiddenToOutput.length);
        for(double w: weightsHiddenToOutput){
           assertEquals(ZERO, w, DELTA);
        }
    }
}