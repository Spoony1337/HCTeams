package rip.orbit.hcteams.util.cooldown;

import lombok.Data;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.util.cooldown.form.DurationFormatter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Cooldowns {

	private ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

	public void applyCooldown(Player player, long cooldown) {
		CompletableFuture.runAsync(() -> {
			if (player.hasMetadata("abilitymaster")) {
				cooldowns.put(player.getUniqueId(), (System.currentTimeMillis() + cooldown * 700));
			} else {
				cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown * 1000);
			}
		});
	}

	public boolean onCooldown(Player player) {
		return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
	}

	public void removeCooldown(Player player) {
		CompletableFuture.runAsync(() -> {
			cooldowns.remove(player.getUniqueId());
		});
	}

	public String getRemaining(Player player) {
		long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
		return DurationFormatter.getRemaining(l, true);

	}


}
