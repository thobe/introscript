package org.thobe.script.remote;

import java.io.Reader;
import java.rmi.RemoteException;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

class RemoteScriptEngine extends AbstractScriptEngine
{
    private final RemoteScriptEngineFactory factory;
    static final String DEFAULT_LANGUAGE = "js";

    RemoteScriptEngine( RemoteScriptEngineFactory factory )
    {
        this.factory = factory;
    }

    @Override
    public Object eval( String script, ScriptContext context ) throws ScriptException
    {
        try
        {
            return Connector.getEngine( context ).eval( script );
        }
        catch ( RemoteException e )
        {
            throw new ScriptException( e );
        }
    }

    @Override
    public Object eval( Reader reader, ScriptContext context ) throws ScriptException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bindings createBindings()
    {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory()
    {
        return factory;
    }
}
