package rip.orbit.hcteams.profile;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/07/2021 / 2:07 AM
 * HCTeams / rip.orbit.hcteams.profile
 */

@Data
public class Profile {

	public static Map<UUID, Profile> profileMap = new HashMap<>();

	private final UUID uuid;
	private String lastHitName = "";
	private String lastDamagerName = "";
	private BukkitTask ninjaTask;

	private long ninjaLastHitTime;
	private long antibuildHitTime;
	private long abilityInspectorHitTime;
	private long curseHitTime;
	private long peekABooHitTime;
	private long pearlThrownTime;

	public Profile(UUID uuid) {
		this.uuid = uuid;

		Profile.profileMap.put(this.uuid, this);
	}

	public boolean canHit(long time) {
		return time - System.currentTimeMillis() <= 0;
	}

	public static Profile byUUID(UUID toSearch) {
		for (Profile value : profileMap.values()) {
			if (value.getUuid() == toSearch) {
				return value;
			}
		}
		return new Profile(toSearch);
	}

}
