package com._604robotics.robot2012;

import com._604robotics.robot2012.control.modes.SequentialModeLauncher;
import com._604robotics.robot2012.control.modes.hybrid.AutonomousControlMode;
import com._604robotics.robot2012.control.modes.hybrid.KinectControlMode;
import com._604robotics.robot2012.control.modes.hybrid.WaitingControlMode;
import com._604robotics.robot2012.control.modes.teleop.CompetitionControlMode;
import com._604robotics.robot2012.control.modes.teleop.LearningControlMode;
import com._604robotics.robot2012.control.workers.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;

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
         * Initialize calibration signals.
         */
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        /*
         * Register workers.
         */
        WorkerManager.registerWorker(new ConfigWorker());
        WorkerManager.registerWorker(new RingLightWorker());
        WorkerManager.registerWorker(new DriveWorker());
        WorkerManager.registerWorker(new ElevatorWorker());
        WorkerManager.registerWorker(new PickupWorker());
        WorkerManager.registerWorker(new ShooterWorker());
        WorkerManager.registerWorker(new DashboardWorker());
        WorkerManager.registerWorker(new RespringWorker());

        /*
         * Initialize hybrid mode.
         */
        hybridMode.registerControlMode(new AutonomousControlMode(), true);
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
        System.out.println("Initialization fired.");
        Robot.init();
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
        }
    }
}
