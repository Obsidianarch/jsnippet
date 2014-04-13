package com.github.obsidianarch.jsnippet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

public final class CompletionProviderFactory {

    public static CompletionProvider createProvider() {

        List< String > keywords = readKeywords();
        List< String[] > templates = readTemplates();
        
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        
        for ( String s : keywords ) {
            provider.addCompletion( new BasicCompletion( provider, s ) );
        }
        
        for ( String[] s : templates ) {
            provider.addCompletion( new ShorthandCompletion( provider, s[ 0 ], s[ 1 ], s[ 2 ] ) );
        }
        
        return provider;
    }
    
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
    
    private static List< String[] > readTemplates() {
        List< String[] > templates = new ArrayList<>();
        
        try {
            try ( BufferedReader reader = new BufferedReader( new FileReader( JSnippet.TEMPLATES ) ) ) {
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
