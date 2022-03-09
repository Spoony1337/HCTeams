package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;
import rip.orbit.hcteams.tab.TabListMode;

import java.util.UUID;

public class TabListModeMap extends PersistMap<TabListMode> {

    public TabListModeMap() {
        super("TabListInfo", "TabListInfo", false);
    }

    
    @Override
	public String getRedisValue(TabListMode toggled){
        return (toggled.name());
    }

    
    @Override
	public TabListMode getJavaObject(String str){
        if (str.equals("VANILLA")) return TabListMode.DETAILED;
        return (TabListMode.valueOf(str));
    }

    
    @Override
	public Object getMongoValue(TabListMode toggled) {
        return (toggled);
    }

    public void setTabListMode(UUID update, TabListMode mode) {
        updateValueAsync(update, mode);
    }

    public TabListMode getTabListMode(UUID check) {
        return (contains(check) ? getValue(check) : TabListMode.DETAILED);
    }

}