package org.thobe.script.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.thobe.agent.CallbackAgent;

import static org.thobe.script.remote.RemoteScriptEngine.DEFAULT_LANGUAGE;
import static org.thobe.script.remote.RemoteScriptEngineFactory.ENGINE;
import static org.thobe.script.remote.RemoteScriptEngineFactory.PID;
import static org.thobe.script.remote.RemoteScriptEngineFactory.LANGUAGE;

class Connector extends UnicastRemoteObject implements Callback
{
    static RemoteEngine getEngine( ScriptContext context ) throws ScriptException, RemoteException
    {
        RemoteEngine engine = (RemoteEngine) context.getAttribute( ENGINE );
        if ( engine == null )
        {
            String pid = (String) context.getAttribute( PID );
            if ( pid == null )
            {
                pid = System.getProperty( PID );
                if ( pid != null )
                {
                    context.setAttribute( PID, pid, ScriptContext.ENGINE_SCOPE );
                }
                else
                {
                    throw new ScriptException( "No PID specified." );
                }
            }
            String language = (String) context.getAttribute( LANGUAGE );
            if ( language == null )
            {
                language = System.getProperty( LANGUAGE );
                if ( language == null )
                {
                    language = DEFAULT_LANGUAGE;
                }
                context.setAttribute( LANGUAGE, language, ScriptContext.ENGINE_SCOPE );
            }
            try
            {
                engine = connect( pid, language );
            }
            catch ( Exception e )
            {
                throw new ScriptException( e );
            }
        }
        return engine;
    }

    private static RemoteEngine connect( String pid, String language )
            throws RemoteException, TimeoutException, InterruptedException
    {
        Connector connector = new Connector();
        new CallbackAgent( connector, new EngineRequest( language ) ).injectInto( pid );
        return connector.getScriptEngine( 5, TimeUnit.SECONDS );
    }

    private Connector() throws RemoteException
    {
    }

    private final Exchanger<RemoteEngine> scriptEngine = new Exchanger<RemoteEngine>();

    @Override
    public void addScriptEngine( RemoteEngine engine ) throws RemoteException
    {
        try
        {
            scriptEngine.exchange( engine );
        }
        catch ( InterruptedException e )
        {
            throw new RemoteException( "Request interrupted.", e );
        }
    }

    private RemoteEngine getScriptEngine( long timeout, TimeUnit unit ) throws TimeoutException, InterruptedException
    {
        return scriptEngine.exchange( null, timeout, unit );
    }
}
