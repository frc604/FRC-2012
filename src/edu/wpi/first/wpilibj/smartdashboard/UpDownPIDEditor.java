
package edu.wpi.first.wpilibj.smartdashboard;

import com._604robotics.utils.UpDownPIDController;
import com._604robotics.utils.UpDownPIDController.Gains;
import edu.wpi.first.wpilibj.networktables.NetworkListener;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class UpDownPIDEditor implements SmartDashboardData {
    private final UpDownPIDController controller;
    private final NetworkTable table;
    
    public UpDownPIDEditor (UpDownPIDController updown) {
        this.controller = updown;
        this.table = new NetworkTable();
    }
    
    public void update () {
        table.putBoolean("enabled", controller.isEnable());
        table.putDouble("setpoint", controller.getSetpoint());
        
        table.putDouble("u-p", controller.getUpGains().P);
        table.putDouble("u-i", controller.getUpGains().I);
        table.putDouble("u-d", controller.getUpGains().D);
        
        table.putDouble("d-p", controller.getDownGains().P);
        table.putDouble("d-i", controller.getDownGains().I);
        table.putDouble("d-d", controller.getDownGains().D);
    }
    
    public NetworkTable getTable() {
        table.addListenerToAll(new NetworkListener() {
            public void valueChanged(String key, Object value) {
                if (key.equals("u-p") || key.equals("u-i") || key.equals("u-d")) {
                    controller.setUpGains(new Gains(table.getDouble("u-p", 0.0), table.getDouble("u-i", 0.0), table.getDouble("u-d", 0.0)));
                } else if (key.equals("d-p")  || key.equals("d-i") || key.equals("d-d")) {
                    controller.setDownGains(new Gains(table.getDouble("d-p", 0.0), table.getDouble("d-i", 0.0), table.getDouble("d-d", 0.0)));
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
        return "UpDownPIDController";
    }
}
