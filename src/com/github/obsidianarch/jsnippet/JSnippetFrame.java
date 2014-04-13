package com.github.obsidianarch.jsnippet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.github.obsidianarch.swingext.SimpleFrame;

/**
 * @author Austin
 */
public class JSnippetFrame extends SimpleFrame {
    
    //
    // Fields
    //
    
    /** The stream for the text pane displaying build progress. */
    private final PrintStream buildStream;
    
    /** The stream for the text pane displaying the program's output. */
    private final PrintStream outputStream;
    
    //
    // Components
    //

    /** The main content panel, contains all components. */
    private JPanel            contentPane;
    
    /** Where all of the text editing happens. */
    private RSyntaxTextArea   textArea;
    
    /** The tabbed pane at the bottom of the screen for logs. */
    private JTabbedPane       tabbedPane;
    
    /** Any problems compiling the code. */
    private JTextPane         buildLog;
    
    /** Everything printed to the console during the program's execution. */
    private JTextPane         outputLog;
    
    //
    // Constructors
    //

    /**
     * Creates the frame.
     */
    public JSnippetFrame( Theme theme ) {
        super( MenuItemActions.class );
        
        setTitle( "JSnippet" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setBounds( 100, 100, 810, 600 );
        
        {
            contentPane = new JPanel();
            contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
            contentPane.setLayout( new BorderLayout( 0, 0 ) );
            setContentPane( contentPane );
            
            {
                textArea = new RSyntaxTextArea();

                StringBuilder sb = new StringBuilder();
                try ( BufferedReader br = new BufferedReader( new FileReader( JSnippet.DEFAULT_TEXT ) ) ) {
                    
                    String line;
                    while ( ( line = br.readLine() ) != null ) {
                        sb.append( String.format( "%s%n", line ) );
                    }

                }
                catch ( Exception e ) {
                    JSnippet.showError( e, "Reading default text" );
                }
                textArea.setText( sb.toString() );

                textArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_JAVA );
                textArea.setCodeFoldingEnabled( true );
                textArea.setAntiAliasingEnabled( true );
                
                theme.apply( textArea );
                
                AutoCompletion ac = new AutoCompletion( CompletionProviderFactory.createProvider() );
                ac.setAutoActivationDelay( 50 );
                ac.setAutoActivationEnabled( true );
                ac.setAutoCompleteEnabled( true );
                ac.install( textArea );
                
                contentPane.add( new RTextScrollPane( textArea ) );
            }
            
            JSplitPane panel = new JSplitPane() {
                
                @Override
                public int getDividerLocation() {
                    return getWidth() / 2;
                }
                
            };
            panel.setContinuousLayout( true );
            panel.setResizeWeight( 0.5 );
            panel.setPreferredSize( new Dimension( 5, 250 ) );
            contentPane.add( panel, BorderLayout.SOUTH );
            {
                
                buildLog = new JTextPane();
                buildLog.setFont( new Font( "Consolas", Font.PLAIN, 10 ) );
                buildLog.setEditable( false );
                {
                    JTabbedPane tabbedPane = new JTabbedPane();
                    tabbedPane.addTab( "Build Log", null, new JScrollPane( buildLog ), null );
                    panel.setLeftComponent( tabbedPane );
                }
                
                outputLog = new JTextPane();
                outputLog.setFont( new Font( "Consolas", Font.PLAIN, 10 ) );
                outputLog.setEditable( false );
                {
                    JTabbedPane tabbedPane = new JTabbedPane();
                    tabbedPane.addTab( "Output Log", null, new JScrollPane( outputLog ), null );
                    panel.setRightComponent( tabbedPane );
                }

            }
            
            addMenuItem( "File", "Build" ).setActionCommand( "build" );;
            addMenuItem( "File", "Execute" ).setActionCommand( "execute" );
            addMenuItem( "File", "Build and Execute" ).setActionCommand( "buildAndExecute" );
            addSeparator( "File" );
            addMenuItem( "File", "Exit" ).setActionCommand( "exit" );
            
            addMenuItem( "Edit", "Run Arguments" ).setActionCommand( "editRunArgs" );
            addSeparator( "Edit" );
            addMenuItem( "Edit", "Reset Default Text" ).setActionCommand( "resetDefaultText" );
            addMenuItem( "Edit", "Reset Template File" ).setActionCommand( "resetTemplates" );
            addMenuItem( "Edit", "Reset Keywords File" ).setActionCommand( "resetKeywords" );
        }
        
        buildStream = new PrintStream( new TextAreaOutputStream( buildLog ) );
        outputStream = new PrintStream( new TextAreaOutputStream( outputLog ) );
    }
    
    //
    // Getters
    //
    
    /**
     * @return The syntax text area.
     */
    public RSyntaxTextArea getTextArea() {
        return textArea;
    }
    
    /**
     * @return The PrintStream to write to the build console.
     */
    public PrintStream getBuildStream() {
        return buildStream;
    }
    
    /**
     * @return The PrintStream to write to the output console.
     */
    public PrintStream getOutputStream() {
        return outputStream;
    }
}
