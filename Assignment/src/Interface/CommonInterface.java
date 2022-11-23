package Interface;

import java.io.File;
import java.io.IOException;

/**
 * This interface is common to both the Neural Net and LUT interfaces.
 * The idea is that you should be able to easily switch the LUT
 * for the Neural Net since the interfaces are identical.
 */
public interface CommonInterface {
    /**
     *
     * @param X The input vector
     * @return The value returned by NN for this input vector
     */
    public double outputFor(double[] X);

    /**
     *
     * @param X The input vector
     * @param targetOutput The target value to learn
     * @return The error in the output for that input vector
     */
    public double train(double[] X, double targetOutput);

    /**
     * To write weights of a neural net to a file
     * @param argFile
     */
    public void save(File argFile) throws IOException;

    /**
     * Loads the neural net weights from file.
     * Will raise an error in case that an attmpt is being made to load data into a neural net whose structure does not
     * match the data in the file
     * @param argFileName
     * @throws IOException
     */
    public void load(String argFileName) throws IOException;
}
