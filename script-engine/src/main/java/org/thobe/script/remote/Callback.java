package org.thobe.script.remote;

import java.lang.instrument.Instrumentation;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.tools.attach.VirtualMachineDescriptor;
import org.thobe.agent.CallbackInvoker;
import org.thobe.java.tooling.ToolingInterface;
import sun.misc.Unsafe;

interface Callback extends Remote
{
    void addScriptEngine( RemoteEngine engine ) throws RemoteException;

    final class EngineRequest implements CallbackInvoker<Callback>
    {
        private final String language;

        EngineRequest( String language )
        {
            this.language = language;
        }

        @Override
        public void invokeCallback( Callback callback, Instrumentation instrumentation, Unsafe unsafe )
                throws RemoteException
        {
            callback.addScriptEngine( new LocalScriptEngine( language, instrumentation, unsafe ) );
        }

        @Override
        public void attachFailed( Callback callback, VirtualMachineDescriptor descriptor, Throwable failure )
                throws RemoteException
        {
            // ignore
        }
    }
}
