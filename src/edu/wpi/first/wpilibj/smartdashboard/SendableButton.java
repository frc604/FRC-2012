package edu.wpi.first.wpilibj.smartdashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class SendableButton implements SmartDashboardNamedData {
    private final NetworkTable table;
    private final String name;
    
    private boolean wasPressed = false;
    private boolean isDisabled = false;
    private boolean deleted = false;
    
    public SendableButton (String name) {
        this.table = new NetworkTable();
        this.name = name;
    }
    
    public String getType () {
        return "Button";
    }
    
    public NetworkTable getTable () {
        return this.table;
    }
    
    public boolean getPressed () {
        try {
            return this.table.getBoolean("pressed");
        } catch (NetworkTableKeyNotDefined ex) {
            return false;
        }
    }
    
    public boolean getToggle () {
        return !this.wasPressed && (this.wasPressed = this.getPressed());
    }
    
    public boolean isDisabled () {
        return this.isDisabled;
    }
    
    public void setDisabled (boolean isDisabled) {
        this.isDisabled = isDisabled;
        this.table.putBoolean("disabled", isDisabled);
    }
    
    public boolean isDeleted () {
        return this.deleted;
    }
    
    public void delete () {
        this.deleted = true;
        this.table.putBoolean("delete", this.deleted);
    }

    public String getName () {
        return this.name;
    }
}
