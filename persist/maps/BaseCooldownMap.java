package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/05/2021 / 5:24 AM
 * SparkHCTeams / rip.orbit.hcteams.extras.pets.buff.cooldowns
 */
public class BaseCooldownMap extends PersistMap<Long> {

	public BaseCooldownMap() {
		super("BaseCooldowns", "BaseCooldowns");
	}

	@Override
	public String getRedisValue(Long time) {
		return (String.valueOf(time));
	}

	@Override
	public Long getJavaObject(String str) {
		return (Long.parseLong(str));
	}

	@Override
	public Object getMongoValue(Long time) {
		return Long.toString(time);
	}

	public boolean hasTimer(UUID check) {
		if (getValue(check) != null) {
			return (getValue(check) > System.currentTimeMillis());
		}

		return (false);
	}
	public long getRemaining(UUID check) {
		return (contains(check) ? getValue(check) : 0L);
	}

	public void apply(UUID update, long seconds) {
		updateValueAsync(update, seconds);
	}

	public void remove(UUID update) {
		updateValueAsync(update, 0L);
	}
}
