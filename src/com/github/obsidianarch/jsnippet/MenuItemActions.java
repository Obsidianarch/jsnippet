package com.github.obsidianarch.jsnippet;

import java.awt.Color;

import javax.swing.JOptionPane;

/**
 * Every method in this is named for a JMenuItem which will fire the event in this class
 * when the item is clicked.
 * 
 * @author Austin
 */
public class MenuItemActions {
    
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

        JSnippet.printTime( "Build Start" );

        String source = frame.getTextArea().getText();
        String className = JSnippet.getClassName( source );
        
        if ( !JSnippet.compileSource( className, source ) ) {
            JOptionPane.showMessageDialog( frame, "Check console for errors!", "Compile Errors", JOptionPane.ERROR_MESSAGE );
        }
        
        JSnippet.printTime( "Build Ended" );
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

        JSnippet.printTime( "Execution Start" );

        try {
            JSnippet.executeClass( JSnippet.getClassName( frame.getTextArea().getText() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        
        JSnippet.printTime( "Execution Ended" );
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
        JSnippet.runArguments = JOptionPane.showInputDialog( frame, "Edit Run Arguments", JSnippet.runArguments );
        if ( JSnippet.runArguments == null ) JSnippet.runArguments = ""; // prevent it from being null!
    }
    
    /**
     * Edits the maximum number of lines the consoles will display.
     * 
     * @param frame
     *            The frame.
     */
    public static void editLineLimit( JSnippetFrame frame ) {
        String newNumber = JOptionPane.showInputDialog( frame, "Edit Console Line Limit", JSnippet.consoleLineLimit + "" );
        
        if ( newNumber == null ) return;

        try {
            int i = Integer.parseInt( newNumber );
            JSnippet.consoleLineLimit = i;
        }
        catch ( NumberFormatException e ) {
            // the user doesn't know what a number is
            // let's teach them, by parsing their input
            // as a number.
            
            int i = 0;
            for ( char c : newNumber.toCharArray() ) {
                i += c; // lol
            }
            
            JSnippet.consoleLineLimit = i;
            
            // sass the user
            String format = "Line limit set to %n%d%nConverted from%n%s%nBecause you don't understand integers.";
            JOptionPane.showMessageDialog( frame, String.format( format, JSnippet.consoleLineLimit, newNumber ) );
        }
        
        frame.getBuildConsole().setMessageLines( JSnippet.consoleLineLimit );
        frame.getOutputConsole().setMessageLines( JSnippet.consoleLineLimit );
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
