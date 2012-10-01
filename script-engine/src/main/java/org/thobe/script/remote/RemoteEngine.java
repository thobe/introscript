package org.thobe.script.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.script.ScriptException;

public interface RemoteEngine extends Remote
{
    Object eval( String script ) throws RemoteException, ScriptException;
}
