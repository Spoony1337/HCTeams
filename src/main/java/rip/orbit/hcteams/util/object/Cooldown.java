package rip.orbit.hcteams.util.object;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private Map<UUID, Long> cooldownMap = new HashMap<>();
    private long start = System.currentTimeMillis();
    private long expire;
    public Cooldown(long duration) {
        this.expire = this.start + duration;
    }


    public void applyCooldown(Player player, long cooldown) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
    }
    public boolean onCooldown(Player player) {
        return cooldownMap.containsKey(player.getUniqueId()) && (cooldownMap.get(player.getUniqueId()) >= System.currentTimeMillis());
    }
    public void cooldownRemove(Player player) {
        cooldownMap.remove(player.getUniqueId());
    }

    public String getRemaining(Player player) {
        long l = cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatUtils.formatDuration(l, "s");
    }

    public long getRemainingMilis(Player player){
        long l = cooldownMap.get(player.getUniqueId());
        return (int) (l - System.currentTimeMillis());
    }
    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - this.expire >= 0;
    }
    public int getRemainingInt(Player player){
        int l = Math.toIntExact(cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis());
        return l;
    }

}
