package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

public class StaffBoardMap extends PersistMap<Boolean> {

    public StaffBoardMap() {
        super("StaffSB", "StaffSB");
    }

    
    @Override
	public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    
    @Override
	public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    
    @Override
	public Object getMongoValue(Boolean toggled) {
        return (toggled);
    }

    public void setBoardToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isBoardToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}