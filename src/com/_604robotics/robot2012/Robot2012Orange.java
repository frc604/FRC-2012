package com._604robotics.robot2012;

import com._604robotics.robot2012.control.modes.SequentialModeLauncher;
import com._604robotics.robot2012.control.modes.hybrid.BridgeControlMode;
import com._604robotics.robot2012.control.modes.hybrid.KinectControlMode;
import com._604robotics.robot2012.control.modes.hybrid.ShootControlMode;
import com._604robotics.robot2012.control.modes.hybrid.WaitingControlMode;
import com._604robotics.robot2012.control.modes.teleop.CompetitionControlMode;
import com._604robotics.robot2012.control.modes.teleop.LearningControlMode;
import com._604robotics.robot2012.control.workers.*;
import com._604robotics.robot2012.dashboard.Dashboard;
import com._604robotics.robot2012.dashboard.FiringDashboard;
import com._604robotics.robot2012.dashboard.ShooterDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SimpleRobot;

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

    /**
     * Constructor.
     */
    public Robot2012Orange() {
        /*
         * Make sure the robot is warmed up. Probably isn't necessary.
         */
        Robot.init();
        
        /*
         * Initialize calibration signals.
         */
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        /*
         * Register workers.
         */
        WorkerManager.registerWorker(new RingLightWorker());
        WorkerManager.registerWorker(new DriveWorker());
        WorkerManager.registerWorker(new ElevatorWorker());
        WorkerManager.registerWorker(new PickupWorker());
        WorkerManager.registerWorker(new ShooterWorker());
        WorkerManager.registerWorker(new RespringWorker());
        
        /*
         * Register dashboard sections.
         */
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
        teleopMode.registerControlMode(new CompetitionControlMode(), true);
        teleopMode.registerControlMode(new LearningControlMode(), false);

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
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        Robot.compressorPump.start();
        hybridMode.init();

        while (isAutonomous() && isEnabled() && hybridMode.step()) {
            WorkerManager.work();
            //Dashboard.render();
        }

        hybridMode.disable();
        Robot.compressorPump.stop();
    }

    /**
     * Operator-controlled drive for Teleop mode.
     */
    public void operatorControl() {
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        teleopMode.init();
        
        while (isOperatorControl() && isEnabled() && teleopMode.step()) {
            WorkerManager.work();
            Dashboard.render();
        }

        teleopMode.disable();
    }

    /**
     * Disabled mode processing.
     */
    public void disabled() {
        Robot.compressorPump.stop();
        Robot.driveTrain.setSafetyEnabled(false);

        while (!isEnabled()) {
            Robot.tryCalibrateElevator();
            Dashboard.render();
        }
    }
}
