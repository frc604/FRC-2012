package com._604robotics.robot2012;

import com._604robotics.robot2012.control.modes.SequentialModeLauncher;
import com._604robotics.robot2012.control.modes.hybrid.BridgeControlMode;
import com._604robotics.robot2012.control.modes.hybrid.KinectControlMode;
import com._604robotics.robot2012.control.modes.hybrid.ShootControlMode;
import com._604robotics.robot2012.control.modes.hybrid.WaitingControlMode;
import com._604robotics.robot2012.control.modes.teleop.DemoControlMode;
import com._604robotics.robot2012.control.workers.*;
import com._604robotics.robot2012.dashboard.Dashboard;
import com._604robotics.robot2012.dashboard.DemoDashboard;
import com._604robotics.robot2012.dashboard.FiringDashboard;
import com._604robotics.robot2012.dashboard.ShooterDashboard;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SimpleRobot;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.microedition.io.Connector;

/**
 * Main class for the 2012 robot code codenamed Orange.
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * @author Sebastian Merz <merzbasti95@gmail.com>
 * @author Aaron Wang <aaronw94@gmail.com>
 * @author Colin Aitken <cacolinerd@gmail.com>
 * @author Alan Li <alanpusongli@gmail.com>
 */
public class Robot2012Orange extends SimpleRobot {

    SequentialModeLauncher hybridMode = new SequentialModeLauncher("Hybrid");
    SequentialModeLauncher teleopMode = new SequentialModeLauncher("Teleop");

    public static void logException (Exception ex) {
        try {
            ex.printStackTrace();
            
            FileConnection log = (FileConnection) Connector.open("file://" + System.currentTimeMillis() + ".txt"); 
            log.create();
            
            OutputStream logstream = log.openOutputStream();
            PrintStream logger = new PrintStream(logstream);
            
            logger.println(ex.getMessage());
            logger.println(ex.toString());
            
            logger.close();
            logstream.close();
            
            
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    /**
     * Constructor.
     */
    public Robot2012Orange() {
        /*
         * Make sure the robot is warmed up. Probably isn't necessary.
         */
        Robot.init();

        /*
         * Register workers.
         */
        WorkerManager.registerWorker(new RingLightWorker());
        WorkerManager.registerWorker(new DriveWorker());
        WorkerManager.registerWorker(new ElevatorWorker());
        WorkerManager.registerWorker(new PickupWorker());
        WorkerManager.registerWorker(new ShooterWorker());
        WorkerManager.registerWorker(new StingerWorker());
        WorkerManager.registerWorker(new RespringWorker());

        /*
         * Register dashboard sections.
         */
        Dashboard.registerSection(DemoDashboard.getInstance());
        //Dashboard.registerSection(AutonomousDashboard.getInstance());
        //Dashboard.registerSection(ElevatorDashboard.getInstance());
        Dashboard.registerSection(FiringDashboard.getInstance());
        Dashboard.registerSection(ShooterDashboard.getInstance());
        //Dashboard.registerSection(StateDashboard.getInstance());
        //Dashboard.registerSection(UserDashboard.getInstance());

        /*
         * Initialize hybrid mode.
         */
        hybridMode.registerControlMode(new ShootControlMode(), true);
        hybridMode.registerControlMode(new BridgeControlMode(), false);
        hybridMode.registerControlMode(new WaitingControlMode(), true);
        hybridMode.registerControlMode(new KinectControlMode(), true);

        hybridMode.renderSmartDashboard();

        /*
         * Initialize teleop mode.
         */
        //teleopMode.registerControlMode(new CompetitionControlMode(), true);
        teleopMode.registerControlMode(new DemoControlMode(), true);
        //teleopMode.registerControlMode(new LearningControlMode(), true);

        teleopMode.renderSmartDashboard();

        // TODO: Make the following better.
        Robot.firingProvider.setAtFender(true);

        /*
         * Ditch the built-in Watchdog.
         */
        this.getWatchdog().setEnabled(false);
    }

    /**
     * Initializes the robot on startup.
     */
    public void robotInit() {
        System.out.println("It's ALIVE!");
    }

    /**
     * Automated drive for autonomous mode.
     */
    public void autonomous() {
        try {
            DriverStation.getInstance().setDigitalOut(2, false);
            DriverStation.getInstance().setDigitalOut(5, false);

            Robot.compressorPump.start();
            hybridMode.init();

            try {
                while (isAutonomous() && isEnabled() && hybridMode.step()) {
                    WorkerManager.work();
                    Dashboard.render();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            hybridMode.disable();
            Robot.compressorPump.stop();
        } catch (Exception ex) {
            logException(ex);
        }
    }

    /**
     * Operator-controlled drive for Teleop mode.
     */
    public void operatorControl() {
        boolean retry = true;
        
        while (retry) {
            try {
                retry = false;

                DriverStation.getInstance().setDigitalOut(2, false);
                DriverStation.getInstance().setDigitalOut(5, false);

                Robot.compressorPump.start();
                teleopMode.init();

                try {
                    while (isOperatorControl() && isEnabled() && teleopMode.step()) {
                        WorkerManager.work();
                        Dashboard.render();
                    }
                } catch (Exception ex) {
                    logException(ex);
                    retry = true;
                }

                teleopMode.disable();
                Robot.compressorPump.stop();
            } catch (Exception ex) {
                logException(ex);
                retry = true;
            }
        }
    }

    /**
     * Disabled mode processing.
     */
    public void disabled() {
        try {
            Robot.compressorPump.stop();
            Robot.driveTrain.setSafetyEnabled(false);

            while (!isEnabled()) {
                Robot.tryCalibrateElevator();
                Dashboard.render();
            }
        } catch (Exception ex) {
            logException(ex);
        }
    }
}
