package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 5:38 AM
 * HCTeams / rip.orbit.hcteams.persist.maps
 */
public class StarsMap extends PersistMap<Integer> {

	public StarsMap() {
		super("Stars", "Stars");
	}

	@Override
	public String getRedisValue(Integer lives) {
		return (String.valueOf(lives));
	}

	@Override
	public Integer getJavaObject(String str) {
		return (Integer.parseInt(str));
	}

	@Override
	public Object getMongoValue(Integer lives) {
		return (lives);
	}

	public int get(UUID check) {
		return (contains(check) ? getValue(check) : 0);
	}

	public void set(UUID update, int lives) {
		updateValueSync(update, lives);
	}

	public void add(UUID update, int amount) {
		set(update, get(update) + amount);
	}
	public void subtract(UUID update, int amount) {
		set(update, get(update) - amount);
	}
}
