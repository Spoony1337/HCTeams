package rip.orbit.hcteams.reclaim;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 10:04 PM
 * HCTeams / rip.orbit.hcteams.reclaim.config
 */
public class ReclaimMap extends PersistMap<Boolean> {

	public ReclaimMap() {
		super("Reclaims", "Reclaims");
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

	public void setToggled(UUID update, boolean toggled) {
		updateValueAsync(update, toggled);
	}

	public boolean isToggled(UUID check) {
		return (contains(check) ? getValue(check) : false);
	}

}
