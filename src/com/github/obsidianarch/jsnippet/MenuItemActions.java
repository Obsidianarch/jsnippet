package com.github.obsidianarch.jsnippet;

import java.awt.Color;
import java.io.PrintStream;

import javax.swing.JOptionPane;

/**
 * Every method in this is named for a JMenuItem which will fire the event in this class
 * when the item is clicked.
 * 
 * @author Austin
 */
public class MenuItemActions {
    
    //
    // Constants
    //
    
    /** The actual System.out */
    private static final PrintStream realOut = System.out;
    
    /** The actual System.in */
    private static final PrintStream realErr = System.err;
    
    //
    //  Menu Items
    //

    /**
     * Builds the file.
     * 
     * @param frame
     *            The frame.
     */
    public static void build( JSnippetFrame frame ) {
        frame.getBuildConsole().getTextComponent().setText( "" );
        frame.getBuildConsole().redirectOut();
        frame.getBuildConsole().redirectErr( Color.RED, null );

        String source = frame.getTextArea().getText();
        String className = JSnippet.getClassName( source );
        
        if ( !JSnippet.compileSource( className, source ) ) {
            JOptionPane.showMessageDialog( frame, "Check console for errors!", "Compile Errors", JOptionPane.ERROR_MESSAGE );
        }
        
        System.setOut( realOut );
        System.setErr( realErr );
    }
    
    /**
     * Runs the build source file.
     * 
     * @param frame
     *            The frame.
     */
    public static void execute( JSnippetFrame frame ) {
        frame.getOutputConsole().getTextComponent().setText( "" );
        frame.getOutputConsole().redirectOut();
        frame.getOutputConsole().redirectErr( Color.RED, null );
        
        try {
            JSnippet.executeClass( JSnippet.getClassName( frame.getTextArea().getText() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        
        System.setOut( realOut );
        System.setErr( realErr );
    }

    /**
     * Builds the file, then executes it.
     * 
     * @param frame
     *            The frame.
     */
    public static void buildAndExecute( JSnippetFrame frame ) {
        build( frame );
        execute( frame );
    }
    
    /**
     * Cleans up, then exits.
     */
    public static void exit() {
        JSnippet.cleanup();
        System.exit( 0 );
    }
    
    /**
     * Edits the run arguments passed by the executor.
     * 
     * @param frame
     *            The frame.
     */
    public static void editRunArgs( JSnippetFrame frame ) {
        JSnippet.runArguments = JOptionPane.showInputDialog( frame, "Edit Run Argumetns", JSnippet.runArguments );
    }
    
    /**
     * Resets the default text to the program's original.
     * 
     * @param frame
     *            The frame.
     */
    public static void resetDefaultText( JSnippetFrame frame ) {
        JSnippet.DEFAULT_TEXT.delete();
        JSnippet.copyText( "/res/DefaultText.txt", JSnippet.DEFAULT_TEXT );
        JOptionPane.showMessageDialog( frame, "Default text reset" );
    }
    
    /**
     * Resets the default templates to the program's original.
     * 
     * @param frame
     *            The frame.
     */
    public static void resetTemplates( JSnippetFrame frame ) {
        JSnippet.TEMPLATES.delete();
        JSnippet.copyText( "/res/templates.txt", JSnippet.TEMPLATES );
        JOptionPane.showMessageDialog( frame, "Templates reset" );
    }
    
    /**
     * Resets the default templates to the program's original.
     * 
     * @param frame
     *            The frame.
     */
    public static void resetKeywords( JSnippetFrame frame ) {
        JSnippet.KEYWORDS.delete();
        JSnippet.copyText( "/res/keywords.txt", JSnippet.KEYWORDS );
        JOptionPane.showMessageDialog( frame, "Keywords reset" );
    }

}
