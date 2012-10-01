package org.thobe.script.remote;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class RemoteScriptEngineFactory implements ScriptEngineFactory
{
    public static final String NAME = "remote", ALT_NAME = "rmi";
    public static final String ENGINE = key( "ENGINE" );
    public static final String PID = key( "PID" );
    public static final String LANGUAGE = key( "LANGUAGE" );

    private static String key( String key )
    {
        return Connector.class.getPackage().getName() + '.' + key;
    }

    @Override
    public String getEngineName()
    {
        return NAME;
    }

    @Override
    public String getEngineVersion()
    {
        return "1.0-SNAPSHOT";
    }

    @Override
    public List<String> getExtensions()
    {
        return emptyList();
    }

    @Override
    public List<String> getMimeTypes()
    {
        return emptyList();
    }

    @Override
    public List<String> getNames()
    {
        return asList( NAME, ALT_NAME );
    }

    @Override
    public String getLanguageName()
    {
        return "rmi";
    }

    @Override
    public String getLanguageVersion()
    {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public Object getParameter( String key )
    {
        if ( ScriptEngine.ENGINE.equals( key ) )
        {
            return getEngineName();
        }
        if ( ScriptEngine.ENGINE_VERSION.equals( key ) )
        {
            return getEngineVersion();
        }
        if ( ScriptEngine.NAME.equals( key ) )
        {
            return getNames().get( 0 );
        }
        if ( ScriptEngine.LANGUAGE.equals( key ) )
        {
            return getLanguageName();
        }
        if ( ScriptEngine.LANGUAGE_VERSION.equals( key ) )
        {
            return getLanguageVersion();
        }
        return null;
    }

    @Override
    public String getMethodCallSyntax( String obj, String m, String... args )
    {
        throw new UnsupportedOperationException( "Scripting language unknown" );
    }

    @Override
    public String getOutputStatement( String toDisplay )
    {
        throw new UnsupportedOperationException( "Scripting language unknown" );
    }

    @Override
    public String getProgram( String... statements )
    {
        throw new UnsupportedOperationException( "Scripting language unknown" );
    }

    @Override
    public ScriptEngine getScriptEngine()
    {
        return new RemoteScriptEngine( this );
    }
}
