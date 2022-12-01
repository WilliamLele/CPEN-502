package ece.cpen502.utils;

import robocode.RobocodeFileOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class LogFile {
    public PrintStream stream;

    /**
     * Constructor.
     * @param argFile The file created by Robocode into which to write data
     *      */
    public LogFile ( File argFile ) {
        try {
            stream = new PrintStream( new RobocodeFileOutputStream( argFile ));
            System.out.println( "--+ Log file created." );
        } catch (IOException e) {
            System.out.println( "*** IO exception during file creation attempt.");
        }
    }

    public void print( String argString ) {
        stream.print( argString );
    }

    public void println( String argString ) {
        stream.println( argString );
    }

}
