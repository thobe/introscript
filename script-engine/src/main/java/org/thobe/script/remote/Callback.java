package org.thobe.script.remote;

import java.lang.instrument.Instrumentation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.tools.attach.VirtualMachineDescriptor;
import org.thobe.agent.CallbackAgent;
import org.thobe.agent.CallbackInvoker;
import org.thobe.java.tooling.ToolingInterface;
import sun.misc.Unsafe;

interface Callback extends Remote
{
    void addScriptEngine( RemoteEngine engine ) throws RemoteException;

    final class EngineRequest implements CallbackInvoker<Callback>
    {
        private final String language;
        private final String[] classpath;

        EngineRequest( String language )
        {
            this.language = language;
            Set<String> classpath = new HashSet<String>();
            for ( Class<?> type : new Class<?>[]{ToolingInterface.class} )
            {
                classpath.add( type.getProtectionDomain().getCodeSource().getLocation().getPath() );
            }
            this.classpath = classpath.toArray( new String[classpath.size()] );
        }

        @Override
        public void invokeCallback( Callback callback, Instrumentation instrumentation, Unsafe unsafe )
                throws RemoteException
        {
            for ( String path : classpath )
            {
                CallbackAgent.addToClasspath( instrumentation, path );
            }
            addScriptEngine( callback, instrumentation, unsafe );
        }

        private void addScriptEngine( Callback callback, Instrumentation instrumentation, Unsafe unsafe )
                throws RemoteException
        {
            ToolingInterface tools;
            try
            {
                tools = ToolingInterface.getToolingInterface();
            }
            catch ( UnsatisfiedLinkError e )
            {
                e.printStackTrace();
                tools = null;
            }
            catch ( Exception e )
            {
                tools = null;
            }
            callback.addScriptEngine( new LocalScriptEngine( language, instrumentation, unsafe, tools ) );
        }

        @Override
        public void attachFailed( Callback callback, VirtualMachineDescriptor descriptor, Throwable failure )
                throws RemoteException
        {
            // ignore
        }
    }
}
