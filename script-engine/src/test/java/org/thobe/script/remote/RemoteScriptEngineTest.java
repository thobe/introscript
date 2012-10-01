package org.thobe.script.remote;

import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Rule;
import org.junit.Test;
import org.thobe.testing.subprocess.Subprocess;
import org.thobe.testing.subprocess.TestProcesses;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.thobe.testing.subprocess.TestProcesses.Option.AWAIT_STDOUT_OUTPUT;

public class RemoteScriptEngineTest
{
    @Rule
    public final TestProcesses subprocess = new TestProcesses( AWAIT_STDOUT_OUTPUT );

    @Test
    public void shouldLoadScriptEngineByName() throws Exception
    {
        // given
        ScriptEngineManager manager = new ScriptEngineManager();

        // when
        ScriptEngine engine = manager.getEngineByName( RemoteScriptEngineFactory.NAME );

        // then
        assertThat( engine, instanceOf( RemoteScriptEngine.class ) );
    }

    @Test
    public void shouldLoadScriptEngineByAlternativeName() throws Exception
    {
        // given
        ScriptEngineManager manager = new ScriptEngineManager();

        // when
        ScriptEngine engine = manager.getEngineByName( RemoteScriptEngineFactory.ALT_NAME );

        // then
        assertThat( engine, instanceOf( RemoteScriptEngine.class ) );
    }

    @Test
    public void shouldConnectToSubProcess() throws Exception
    {
        // given
        Subprocess process = subprocess.starter( LoopingProcess.class ).stdOut( null ).start();
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( RemoteScriptEngineFactory.NAME );
        engine.put( RemoteScriptEngineFactory.PID, process.pid() );

        // when
        Object value = engine.eval( "1 + 2" );

        // then
        assertEquals( 3.0, value );
    }

    @Test
    public void shouldExecuteInSubprocess() throws Exception
    {
        // given
        Subprocess process = subprocess.starter( LoopingProcess.class ).stdOut( null ).start();
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( RemoteScriptEngineFactory.NAME );
        engine.put( RemoteScriptEngineFactory.PID, process.pid() );

        // when
        try
        {
            engine.eval( "java.lang.System.exit(42)" );
        }
        catch ( ScriptException ignore )
        {
            // do nothing, the call will fail, since System.exit() never returns
        }

        // then
        assertEquals( 42, process.awaitTermination( 5, TimeUnit.SECONDS ) );
    }
}
