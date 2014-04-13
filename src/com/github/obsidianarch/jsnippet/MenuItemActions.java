package com.github.obsidianarch.jsnippet;

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
    
    private static final PrintStream realOut = System.out;
    private static final PrintStream realErr = System.err;
    
    //
    //  Menu Items
    //

    public static void build( JSnippetFrame frame ) {
        frame.getBuildStream().write( '\u2136' );

        String source = frame.getTextArea().getText();
        String className = JSnippet.getClassName( source );
        
        System.setOut( frame.getBuildStream() ); // redirect all build messages to the console
        System.setErr( frame.getBuildStream() );
        
        if ( !JSnippet.compileSource( className, source ) ) {
            JOptionPane.showMessageDialog( frame, "Check console for errors!", "Compile Errors", JOptionPane.ERROR_MESSAGE );
        }
        
        System.setOut( realOut );
        System.setErr( realErr );
    }
    
    public static void execute( JSnippetFrame frame ) {
        frame.getOutputStream().write( '\u2136' );

        System.setOut( frame.getOutputStream() );
        System.setErr( frame.getOutputStream() );
        
        try {
            JSnippet.executeClass( JSnippet.getClassName( frame.getTextArea().getText() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        
        System.setOut( realOut );
        System.setErr( realErr );
    }

    public static void buildAndExecute( JSnippetFrame frame ) {
        build( frame );
        execute( frame );
    }
    
    public static void exit() {
        JSnippet.cleanup();
        System.exit( 0 );
    }
    
    public static void editRunArgs( JSnippetFrame frame ) {
        JSnippet.runArguments = JOptionPane.showInputDialog( frame, "Edit Run Argumetns", JSnippet.runArguments );
    }
    
    public static void resetDefaultText( JSnippetFrame frame ) {
        JSnippet.DEFAULT_TEXT.delete();
        JSnippet.copyText( "/res/DefaultText.txt", JSnippet.DEFAULT_TEXT );
        JOptionPane.showMessageDialog( frame, "Default text reset" );
    }
    
    public static void resetTemplates( JSnippetFrame frame ) {
        JSnippet.TEMPLATES.delete();
        JSnippet.copyText( "/res/templates.txt", JSnippet.TEMPLATES );
        JOptionPane.showMessageDialog( frame, "Templates reset" );
    }
    
    public static void resetKeywords( JSnippetFrame frame ) {
        JSnippet.KEYWORDS.delete();
        JSnippet.copyText( "/res/keywords.txt", JSnippet.KEYWORDS );
        JOptionPane.showMessageDialog( frame, "Keywords reset" );
    }

}
