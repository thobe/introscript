package org.thobe.script.remote;

class LoopingProcess
{
    public static void main( String[] args ) throws InterruptedException
    {
        System.out.println("STARTING");
        for ( ;; )
        {
            Thread.sleep( 1 );
        }
    }
}
