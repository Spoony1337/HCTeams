package rip.orbit.hcteams.persist.maps;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.persist.PersistMap;
import rip.orbit.hcteams.team.dtr.DTRBitmask;

import java.util.UUID;

public class PvPTimerMap extends PersistMap<Integer> {

    public PvPTimerMap() {
        super("PvpTimers", "Pvptimer", false); // dont save this data to mongo
        // This should probably use a bit smarter of a system... but for now it's fine.
        new BukkitRunnable() {

            @Override
			public void run() {
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (hasTimer(player.getUniqueId())) {
                        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) || HCF.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
                            continue;
                        }

                        int newValue = getValue(player.getUniqueId()) - 1;

                        if (newValue % 60 == 0) {
                            int minutes = newValue / 60;

                            if (minutes <= 0) {
                                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your protection has expired!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + minutes + ChatColor.RED + " minute" + (minutes == 1 ? "" : "s") + " of protection remaining.");
                            }
                        }

                        updateValueAsync(player.getUniqueId(), newValue);
                    }
                 }
            }

        }.runTaskTimerAsynchronously(HCF.getInstance(), 20L, 20L);
    }


    @Override
	public String getRedisValue(Integer time) {
        return (String.valueOf(time));
    }


    @Override
	public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }


    @Override
	public Object getMongoValue(Integer time) {
        return (time);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, 0);
        HCF.getInstance().getStartingPvPTimerMap().set(update, false);
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update, seconds);
    }

    public void createStartingTimer(UUID update, int seconds) {
        createTimer(update, seconds);
        HCF.getInstance().getStartingPvPTimerMap().set(update, true);
    }

    public boolean hasTimer(UUID check) {
        return (getSecondsRemaining(check) > 0);
    }

    public int getSecondsRemaining(UUID check) {
        if (HCF.getInstance().getServerHandler().isPreEOTW() || HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
            return (0);
        }

        return (contains(check) ? getValue(check) : 0);
    }

}