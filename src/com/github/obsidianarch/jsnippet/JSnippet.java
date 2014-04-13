package com.github.obsidianarch.jsnippet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.fife.ui.rsyntaxtextarea.Theme;

/**
 * @author Austin
 */
public class JSnippet {
    
    //
    // Fields
    //
    
    /** Arguments passed to the main method when run */
    public static String              runArguments = "";
    
    /** Maximum number of characters in a text pane. */
    public static int                 maxPaneLines = 1000;

    /** Our compiler for our java source. */
    private static final JavaCompiler compiler     = ToolProvider.getSystemJavaCompiler();

    /** The temporary directory where all the program files are. */
    public static final File          TEMP_DIR     = new File( System.getProperty( "user.home" ), ".jsnippet" );
    
    /** The default text for the editor. */
    public static final File          DEFAULT_TEXT = new File( TEMP_DIR, "res/DefaultText.txt" );
    
    /** The code templates for autocompletion. */
    public static final File          TEMPLATES    = new File( TEMP_DIR, "res/templates.txt" );
    
    /** Keywords for the editor's syntax highlighter. */
    public static final File          KEYWORDS     = new File( TEMP_DIR, "res/keywords.txt" );

    /** The last created .java file. */
    private static File               sourceFile;
    
    /** The last created .class file. */
    private static File               binaryFile;

    static {
        // make the temporary directory
        if ( !TEMP_DIR.exists() ) {
            TEMP_DIR.mkdir();
        }

        // copy the new files into the program directory
        try {
            copyText( "/res/DefaultText.txt", DEFAULT_TEXT );
            copyText( "/res/keywords.txt", TEMPLATES );
            copyText( "/res/templates.txt", KEYWORDS );
        }
        catch ( Exception e ) {
            showError( e, "Failed to locate resources!" );
        }
    }

    //
    // Actions
    //
    
    /**
     * Copies the text from the resource file to an external file.
     * 
     * @param resource
     *            The path to the resource file.
     * @param output
     *            The output file to write to.
     * @return If the copy was successful or not.
     */
    public static boolean copyText( String resource, File output ) {
        if ( output.exists() ) return true;
        else {
            
            if ( !output.getParentFile().exists() ) {
                output.getParentFile().mkdirs();
            }

            try {
                output.createNewFile();
            }
            catch ( IOException e ) {
                e.printStackTrace();
                return false;
            }
        }

        try ( BufferedReader br = new BufferedReader( new InputStreamReader( JSnippet.class.getResourceAsStream( resource ) ) );
            BufferedWriter bw = new BufferedWriter( new FileWriter( output ) ) ) {

            String line;
            while ( ( line = br.readLine() ) != null ) {
                bw.write( String.format( "%s%n", line ) );
            }

            return true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            showError( e, "copying resource files to external application file." );
            return false;
        }
    }

    /**
     * Shows an error message to the user.
     * 
     * @param error
     *            The exception.
     * @param when
     *            What caused the exception.
     */
    public static void showError( Throwable error, String when ) {
        error.printStackTrace();
        String message = String.format( "Encountered a(n) \n\t %s \nWhen %s", error.getClass().getName(), when.toLowerCase() );
        JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.ERROR_MESSAGE );
    }
    
    /**
     * Extracts the name of the class from the source code.
     * 
     * @param source
     *            The source code.
     * @return The name of the class.
     */
    public static String getClassName( String source ) {
        String name = source.substring( source.indexOf( "class" ) + "class ".length() ); // remove everything up to and including class
        name = name.substring( 0, name.indexOf( ' ' ) ); // remove everything following the class name
        
        // the user can't format and left no spaces between the class name and the curly brace
        if ( name.contains( "{" ) ) {
            name = name.substring( 0, name.indexOf( '{' ) ); // remove the remaining curly brace
        }

        return name;
    }
    
    /**
     * Deletes the source and binary files that were created.
     */
    public static void cleanup() {

        if ( ( sourceFile != null ) && sourceFile.exists() ) {
            sourceFile.delete();
            System.out.println( "Deleted previous source file" );
        }
        
        if ( ( binaryFile != null ) && binaryFile.exists() ) {
            binaryFile.delete();
            System.out.println( "Deleted previous binary file" );
        }

    }

    /**
     * Compiles the source code for the file into the memory.
     * 
     * @param className
     *            The name of the source's class.
     * @param source
     *            The source code.
     * @return If the code successfully compiled.
     */
    public static boolean compileSource( String className, String source ) {
        long start = System.currentTimeMillis();
        System.out.println( "Starting compile of " + className + "" );
        
        cleanup();

        sourceFile = new File( TEMP_DIR, className + ".java" );
        binaryFile = new File( TEMP_DIR, className + ".class" );
        
        try ( BufferedWriter bw = new BufferedWriter( new FileWriter( sourceFile ) ) ) {
            bw.write( source );
        }
        catch ( IOException e ) {
            System.err.println( "Failed to write to temporary file, cancelling build" );
            e.printStackTrace();
            return false;
        }

        int result = compiler.run( null, null, System.err, sourceFile.getAbsolutePath() );

        System.out.print( "Build " );
        System.out.flush();
        if ( result == 0 ) {
            System.out.print( "SUCCESS" );
        }
        else {
            System.err.print( "FAILURE" );
            System.err.flush();
        }
        System.out.printf( " (%d milliseconds)%n", System.currentTimeMillis() - start );

        return result == 0;
    }
    
    /**
     * Executes the class that was compiled with the given class name.
     * 
     * @param className
     *            The class name to execute.
     */
    public static void executeClass( String className ) {
        if ( ( binaryFile == null ) || !binaryFile.exists() ) return;

        try {
            URL[] urls = new URL[ ] { TEMP_DIR.toURI().toURL() };
            URLClassLoader cl = new URLClassLoader( urls );
            {
                Class< ? > clazz = cl.loadClass( className );
                Method method = clazz.getDeclaredMethod( "main", String[].class );
                method.invoke( null, new Object[ ] { runArguments.split( " " ) } ); // the nesting into an object array prevents the expansion when varargs is applied
            }
            cl.close();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    //
    // Main
    //

    /**
     * Starts the JSnippet program.
     * 
     * @param args
     *            Command line arguments (ignored).
     */
    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); // change it to the system default
        }
        catch ( Exception e ) {
            showError( e, "setting the LookAndFeel!" );
        }
        
        Theme theme = null;
        try {
            theme = Theme.load( JSnippet.class.getResourceAsStream( "/res/eclipse_theme.xml" ) );
        }
        catch ( Exception e ) {
            showError( e, "When loading the Eclipse theme!" );
            System.exit( 0 );
        }

        JSnippetFrame frame = new JSnippetFrame( theme );
        frame.setVisible( true );
    }
    
}
