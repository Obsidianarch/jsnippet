package com.github.obsidianarch.jsnippet;

import static javax.swing.KeyStroke.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;

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
import com.wordpress.tips4java.MessageConsole;

/**
 * The design of the frame.
 * 
 * @author Austin
 */
public class JSnippetFrame extends SimpleFrame {
    
    //
    // Fields
    //
    
    /** The console that displays build messages to the user. */
    private final MessageConsole buildConsole;
    
    /** The console that displays the output from the program to the user. */
    private final MessageConsole outputConsole;

    //
    // Components
    //

    /** The main content panel, contains all components. */
    private JPanel            contentPane;
    
    /** Where all of the text editing happens. */
    private RSyntaxTextArea   textArea;
    
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
                
                textArea.setTabsEmulated( true );
                textArea.setTabSize( 4 );
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
            
            /* Unfortunately, we are making an anonymous class for this. */
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
                buildLog.setAutoscrolls( true );
                {
                    JTabbedPane tabbedPane = new JTabbedPane();
                    tabbedPane.addTab( "Build Log", null, new JScrollPane( buildLog ), null );
                    panel.setLeftComponent( tabbedPane );
                }
                
                outputLog = new JTextPane();
                outputLog.setFont( new Font( "Consolas", Font.PLAIN, 10 ) );
                outputLog.setEditable( false );
                outputLog.setAutoscrolls( true );
                {
                    JTabbedPane tabbedPane = new JTabbedPane();
                    tabbedPane.addTab( "Output Log", null, new JScrollPane( outputLog ), null );
                    panel.setRightComponent( tabbedPane );
                }

            }
            
            addMenuItem( getKeyStroke( "F6" ), "File", "Build" ).setActionCommand( "build" );
            addMenuItem( getKeyStroke( "control F5" ), "File", "Execute" ).setActionCommand( "execute" );
            addMenuItem( getKeyStroke( "F5" ), "File", "Build and Execute" ).setActionCommand( "buildAndExecute" );
            addSeparator( "File" );
            addMenuItem( getKeyStroke( "control B" ), "File", "Stop Tool" ).setActionCommand( "stopTool" );
            addSeparator( "File" );
            addMenuItem( getKeyStroke( "alt F4" ), "File", "Exit" ).setActionCommand( "exit" );
            
            addMenuItem( getKeyStroke( "control shift R" ), "Edit", "Run Arguments" ).setActionCommand( "editRunArgs" );
            addMenuItem( "Edit", "Console Line Limits" ).setActionCommand( "editLineLimit" );
            addSeparator( "Edit" );
            addMenuItem( "Edit", "Reset Default Text" ).setActionCommand( "resetDefaultText" );
            addMenuItem( "Edit", "Reset Template File" ).setActionCommand( "resetTemplates" );
            addMenuItem( "Edit", "Reset Keywords File" ).setActionCommand( "resetKeywords" );
        }
        
        buildConsole = new MessageConsole( buildLog );
        buildConsole.setMessageLines( JSnippet.consoleLineLimit );

        outputConsole = new MessageConsole( outputLog );
        outputConsole.setMessageLines( JSnippet.consoleLineLimit );
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
     * @return The MessageConsole used for the build output.
     */
    public MessageConsole getBuildConsole() {
        return buildConsole;
    }
    
    /**
     * @return The MessageConsole use for the program output.
     */
    public MessageConsole getOutputConsole() {
        return outputConsole;
    }
    
    //
    // Overrides
    //
    
    @Override
    public void windowClosed( WindowEvent e ) {
        MenuItemActions.exit();
    }

}
