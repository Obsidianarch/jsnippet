package com.github.obsidianarch.jsnippet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A nice utility that uses the Reflections API to invoke methods on separate threads.
 * 
 * @author Austin
 */
public class ThreadingUtility implements Runnable {
    
    //
    // Fields
    //
    
    /** The object calling the method */
    private final Object   source;

    /** The method to execute. */
    private final Method   method;
    
    /** Parameters to pass to the method. */
    private final Object[] parameters;

    //
    // Constructors
    //
    
    /**
     * Creates a new thread to be launched an execute the given method.
     * 
     * @param The
     *            object calling the method.
     * @param method
     *            The method to execute.
     * @param parameters
     *            Parameters to pass to the method.
     */
    private ThreadingUtility( Object source, Method method, Object[] parameters ) {
        this.source = source;
        this.method = method;
        this.parameters = parameters;
    }
    
    //
    // Overrides
    //
    
    @Override
    public void run() {
        try {
            method.invoke( source, parameters );
        }
        catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            System.err.println( Thread.currentThread().getName() );
            e.printStackTrace();
            System.err.println();
            System.err.flush();
        }
    }

    //
    // Static
    //

    /**
     * Dispatches a thread to work on the provided method.
     * 
     * @param source
     *            The object calling the source.
     * @param parentClass
     *            The class which contains the method.
     * @param methodName
     *            The method's name.
     */
    public static void dispatchThread( Object source, Class< ? > parentClass, String methodName ) {
        try {
            Method method = parentClass.getMethod( methodName );
            
            ThreadingUtility util = new ThreadingUtility( source, method, null );
            Thread thread = new Thread( util );
            thread.setName( parentClass.getName() + "." + methodName + "()" );
            thread.start();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Dispatches a thread to wrok on the provided method.
     * 
     * @param source
     *            The object calling the method.
     * @param method
     *            The method to invoke.
     * @param parameters
     *            The parameters to pass the method.
     */
    public static void dispatchThread( Object source, Method method, Object... parameters ) {
        ThreadingUtility util = new ThreadingUtility( source, method, parameters );
        Thread thread = new Thread( util );
        thread.setName( method.getName() );
        thread.start();
    }

}
