package com.github.obsidianarch.jsnippet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Manages the execution of binary files for JSnippet.
 * 
 * @author Austin
 */
public class ExecutionService {
    
    //
    // Fields
    //
    
    /** The current process. */
    private static Process currentProcess;

    //
    // Actions 
    //
    
    /**
     * Executes the class that was compiled with the given class name.
     * 
     * @param className
     *            The class name to execute.
     */
    public static void startProcess( String fileName, File binaryFile ) throws IOException {
        
        // process is still running
        if ( ( currentProcess != null ) && currentProcess.isAlive() ) {
            currentProcess.destroyForcibly(); // force the process to die
        }

        // parameters passed to the process creator
        String[] parameters = new String[ ] {
            "java",
            "-cp",
            JSnippet.TEMP_DIR.getAbsolutePath(),
            binaryFile.getName().substring( 0, binaryFile.getName().indexOf( '.' ) )
        };
        
        currentProcess = Runtime.getRuntime().exec( parameters ); // execute the file in a different process
        
        BufferedReader standardOutput = new BufferedReader( new InputStreamReader( currentProcess.getInputStream() ) ); // the standard output from the program
        BufferedReader errorOutput = new BufferedReader( new InputStreamReader( currentProcess.getErrorStream() ) ); // the error output from the program
        
        OutputManager outManager = new OutputManager( System.out, standardOutput ); // manages output to System.out
        OutputManager errManager = new OutputManager( System.err, errorOutput ); // manages output to System.err

        // starts output thread
        Thread outThread = new Thread( outManager );
        outThread.setName( "Standard Output Manager" );
        outThread.start();
        
        // starts error thraed
        Thread errThread = new Thread( errManager );
        errThread.setName( "Standard Error Manager" );
        errThread.start();
        
        Thread exitThread = new Thread( new ExitListener( currentProcess ) );
        exitThread.setName( "Exit Listener Thread" );
        exitThread.start();
    }
    
    //
    // Nested Classes
    //

    public static class ExitListener implements Runnable {
        
        //
        // Fields
        //
        
        /** The process that we're listening to. */
        private final Process process;

        //
        // Constructors
        //

        public ExitListener( Process process ) {
            this.process = process;
        }
        
        //
        // Overrides
        //
        
        @Override
        public void run() {
            try {
                int exitCode = process.waitFor();
                
                if ( exitCode != 0 ) {
                    System.err.printf( "Exit code: %d%n", exitCode );
                }

                JSnippet.printTime( "Process Ended" );
            }
            catch ( InterruptedException e ) {
                System.err.println( "Failed to wait for process exit!" );
            }
        }

    }

    /**
     * Manages output from the process.
     * 
     * @author Austin
     */
    private static class OutputManager implements Runnable {
        
        //
        // Fields
        //
        
        /** Output to the console. */
        private final PrintStream    output;
        
        /** Output from the program. */
        private final BufferedReader input;
        
        //
        // Constructors
        //
        
        /**
         * Constructs a new OutputManager that will print input from {@code input} to
         * {@code output}.
         * 
         * @param output
         *            The print stream to write to (System.out or System.err).
         * @param input
         *            The input to read from.
         */
        public OutputManager( PrintStream output, BufferedReader input ) {
            this.output = output;
            this.input = input;
        }
        
        //
        // Actions
        //
        
        /**
         * Reads all the available characters from the input and returns them as
         * a
         * single string.
         * 
         * @return All characters that the bufferedReader currently has.
         * @throws IOException
         *             If there was a problem when reading characters.
         */
        public String readOutput() throws IOException {
            char[] chars = new char[ 1024 ]; // we'll read 1024 characters at a time
            
            StringBuilder sb = new StringBuilder(); // holds all the characters, faster than string concatenation
            
            while ( input.ready() ) {
                
                int read = input.read( chars ); // reads the characters, keeps track of how many were read
                sb.append( chars, 0, read ); // adds the read characters to the string
                
            }
            
            return sb.toString();
        }

        //
        // Overrides
        //
        
        @Override
        public void run() {
            try {

                while ( currentProcess.isAlive() || input.ready() ) {
                    
                    if ( input.ready() ) {
                        output.print( readOutput() );
                    }
                    else {
                        Thread.sleep( 20 ); // wait before trying again
                    }
                }

            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            finally {

                try {
                    input.close();
                }
                catch ( IOException e ) {
                    // sucks
                }

            }
        }

    }

}
