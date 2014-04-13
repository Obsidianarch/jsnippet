package com.github.obsidianarch.jsnippet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

/**
 * Creates a CompletionProvider for the RSyntaxTextPane.
 * 
 * @author Austin
 */
public final class CompletionProviderFactory {

    /**
     * Creates the CompletionProvider with the keywords and code templates read from their
     * respective files.
     * 
     * @return The created CompletionProvider.
     */
    public static CompletionProvider createProvider() {

        // load the data from the text files
        List< String > keywords = readKeywords();
        List< String[] > templates = readTemplates();
        
        // create the provider
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        
        // load data into the provider
        for ( String s : keywords ) {
            provider.addCompletion( new BasicCompletion( provider, s ) );
        }
        
        for ( String[] s : templates ) {
            provider.addCompletion( new ShorthandCompletion( provider, s[ 0 ], s[ 1 ], s[ 2 ] ) );
        }
        
        return provider;
    }
    
    /**
     * Reads the keywords that the editor will autocomplete.
     * 
     * @return The keywords list.
     */
    private static List< String > readKeywords() {
        List< String > keywords = new ArrayList<>();
        try {
            try ( BufferedReader reader = new BufferedReader( new FileReader( JSnippet.KEYWORDS ) ) ) {
                String line;
                while ( ( line = reader.readLine() ) != null ) {
                    keywords.add( line );
                }
            }
        }
        catch ( Exception e ) {
            JSnippet.showError( e, "Loading keywords for syntax highlighting." );
        }
        
        return keywords;
    }
    
    /**
     * Reads the code templates that the editor will finish.
     * 
     * @return The templates list.
     */
    private static List< String[] > readTemplates() {
        List< String[] > templates = new ArrayList<>();
        
        try {
            try ( BufferedReader reader = new BufferedReader( new FileReader( JSnippet.TEMPLATES ) ) ) {
                
                // three parts of a template, each on their separate lines
                // alias - what the user types
                // code - what is added in
                // description - a short description of the code

                String alias;
                String code;
                String desc;
                while ( ( ( alias = reader.readLine() ) != null ) && ( ( code = reader.readLine() ) != null ) && ( ( desc = reader.readLine() ) != null ) ) {
                    templates.add( new String[ ] { alias, code, desc } );
                }
            }
        }
        catch ( Exception e ) {
            JSnippet.showError( e, "Loading templates for autocompletion." );
        }
        
        return templates;
    }
}
