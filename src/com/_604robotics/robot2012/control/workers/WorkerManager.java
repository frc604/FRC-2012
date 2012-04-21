package com._604robotics.robot2012.control.workers;

import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class WorkerManager {
    private static Vector workers = new Vector();
    
    private static Worker getWorker (int index) {
        return ((Worker) workers.elementAt(index));
    }
    
    public static void registerWorker (Worker worker) {
        workers.addElement(worker);
    }
    
    public static void work () {
        for (int i = 0; i < workers.size(); i++)
            WorkerManager.getWorker(i).work();
    }
}
