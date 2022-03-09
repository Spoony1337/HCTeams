package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

public class ToggleAbilityCDsSBMap extends PersistMap<Boolean> {

    public ToggleAbilityCDsSBMap() {
        super("AbilityCDToggles", "AbilityCDEnabled");
    }


    @Override
	public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }


    @Override
	public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
	public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void setEnabled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isEnabled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}
