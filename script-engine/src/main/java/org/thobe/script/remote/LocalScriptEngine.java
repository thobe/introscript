package org.thobe.script.remote;

import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.thobe.java.tooling.ToolingInterface;
import sun.misc.Unsafe;

class LocalScriptEngine extends UnicastRemoteObject implements RemoteEngine
{
    private final ScriptEngine engine;

    LocalScriptEngine( String name, Instrumentation instrumentation, Unsafe unsafe, ToolingInterface tools )
            throws RemoteException
    {
        this.engine = new ScriptEngineManager().getEngineByName( name );
        if ( engine == null )
        {
            throw new IllegalArgumentException( String.format( "No such script engine '%s'.", name ) );
        }
        if ( instrumentation != null )
        {
            engine.put( "instrumentation", instrumentation );
        }
        if ( unsafe != null )
        {
            engine.put( "unsafe", unsafe );
        }
        if ( tools != null )
        {
            engine.put( "tools", tools );
        }
    }

    @Override
    public Object eval( String script ) throws ScriptException
    {
        Object value = engine.eval( script );
        if ( value != null && !(value instanceof Serializable) )
        {
            value = value.toString();
        }
        return value;
    }
}
