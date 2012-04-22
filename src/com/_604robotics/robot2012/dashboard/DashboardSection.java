package com._604robotics.robot2012.dashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public interface DashboardSection {
    public abstract void enable ();
    public abstract void render ();
    public abstract String getName ();
}
