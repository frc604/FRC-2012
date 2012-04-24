package edu.wpi.first.wpilibj.smartdashboard;

import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.dashboard.ShooterDashboard;
import com._604robotics.robot2012.speedcontrol.PIDDP;
import edu.wpi.first.wpilibj.networktables.NetworkListener;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class PIDDPEditor implements SmartDashboardData {
    private final PIDDP controller;
    private final NetworkTable table;
    
    public PIDDPEditor (PIDDP piddp) {
        this.controller = piddp;
        this.table = new NetworkTable();
    }
    
    public void update () {
        table.putBoolean("enabled", controller.isEnabled());
        table.putDouble("setpoint", controller.getSetpoint());
        
        table.putDouble("p", controller.getP());
        table.putDouble("i", controller.getI());
        table.putDouble("d", controller.getD());
        table.putDouble("dp", controller.getDP());
    }
    
    public NetworkTable getTable() {
        table.addListenerToAll(new NetworkListener() {
            public void valueChanged(String key, Object value) {
                if (key.equals("p")  || key.equals("i") || key.equals("d") || key.equals("dp")) {
                    controller.setPIDDP(table.getDouble("p", 0.0), table.getDouble("i", 0.0), table.getDouble("d", 0.0), table.getDouble("dp", 0.0));
                } else if (key.equals("setpoint")) {
                    controller.setSetpoint(((Double) value).doubleValue());
                }
            }

            public void valueConfirmed(String key, Object value) {
            }
        });
        
        return table;
    }
    
    public String getType() {
        return "PIDDPController";
    }
}
