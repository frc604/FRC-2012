package edu.wpi.first.wpilibj.smartdashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class SendableTag implements SmartDashboardNamedData {
    private final String name;
    private final NetworkTable table;
    
    public SendableTag (String name, String[] tags) {
        this.name = name;
        
        this.table = new NetworkTable();
        for (int i = 0; i < tags.length; i++)
            this.table.putBoolean(tags[i], true);
    }
    
    public String getType () {
        return "Tag";
    }
    
    public NetworkTable getTable () {
        return this.table;
    }
    
    public String getName () {
        return this.name;
    }
}
