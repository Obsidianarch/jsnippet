package com.github.obsidianarch.jsnippet;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextPane;

/**
 * @author Austin
 */
public class TextAreaOutputStream extends OutputStream {
    
    //
    // Fields
    //
    
    /** The text pane we are editing. */
    private final JTextPane textPane;
    
    /** Builds the text for our output log. */
    private StringBuffer    sb;

    //
    // Constructors
    //
    
    /**
     * Redirects text to a JTextPane instead of to the System.out or System.err output.
     * 
     * @param textPane
     *            The text pane to output text to.
     */
    public TextAreaOutputStream( JTextPane textPane ) {
        this.textPane = textPane;
        sb = new StringBuffer();
        
        ThreadingUtility.dispatchThread( this, getClass(), "updateText" );
    }

    //
    // Actions
    //

    /**
     * Updates the text pane's text if it needs to be.
     */
    public void updateText() {
        try {
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e ) {}

        while ( true ) {

            synchronized ( textPane ) {
                textPane.setText( sb.toString() );
            }
            
            try {
                Thread.sleep( 20 );
            }
            catch ( InterruptedException e ) {}
        }
    }
    
    //
    // Overrides
    //

    @Override
    public void write( int b ) throws IOException {
        if ( ( ( char ) b ) == '\u2136' ) {
            sb = new StringBuffer();
        }
        else {
            sb.append( ( char ) b );
        }
    }

}
