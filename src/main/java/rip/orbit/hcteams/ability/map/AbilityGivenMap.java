package rip.orbit.hcteams.ability.map;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/08/2021 / 12:43 AM
 * HCTeams / rip.orbit.hcteams.ability.map
 */
public class AbilityGivenMap extends PersistMap<Integer> {
	public AbilityGivenMap() {
		super("AbilityGiven", "AbilityGivenMap");
	}

	@Override
	public String getRedisValue(Integer kills) {
		return (String.valueOf(kills));
	}


	@Override
	public Integer getJavaObject(String str) {
		return (Integer.parseInt(str));
	}

	@Override
	public Object getMongoValue(Integer mined) {
		return (mined);
	}

	public int get(UUID check) {
		return (contains(check) ? getValue(check) : 0);
	}
	public void add(UUID update, int add) {
		updateValueAsync(update, get(update) + add);
	}
	public void set(UUID update, int mined) {
		updateValueAsync(update, mined);
	}
	public Collection<UUID> getPlayersGiven() {
		Collection<UUID> playersGiven = new HashSet<>();

		for (Map.Entry<UUID, Integer> entry : wrappedMap.entrySet()) {
			if (get(entry.getKey()) > 0) {
				playersGiven.add(entry.getKey());
			}
		}

		return (playersGiven);
	}
}
