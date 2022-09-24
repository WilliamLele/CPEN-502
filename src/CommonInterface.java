import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @author: Hongjing
 * @date: 2022.09.23
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
     * @param targetOutput The new value to learn
     * @return The error in the output for that input vector
     */
    public double train(double[] X, double targetOutput);

    /**
     * To write weights of a neural net to a file
     * @param argFile
     */
    public void save(File argFile);

    /**
     * Loads the neural net weights from file.
     * Will raise an error in case that an attmpt is being made to load data into a neural net whose structure does not
     * match the data in the file
     * @param argFileName
     * @throws IOException
     */
    public void load(String argFileName) throws IOException;
}
