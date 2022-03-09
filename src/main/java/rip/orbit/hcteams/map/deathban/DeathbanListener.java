package rip.orbit.hcteams.map.deathban;

import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.LastInvCommand;
import rip.orbit.hcteams.server.EnderpearlCooldownHandler;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        LastInvCommand.recordInventory(event.getEntity());

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(event.getEntity().getName()); // cancel enderpearls

        if (HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (HCF.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        if (HCF.getInstance().getServerHandler().isVeltKitMap()) {
            return;
        }

        long seconds = HCF.getInstance().getServerHandler().getDeathban(event.getEntity());
        HCF.getInstance().getDeathbanMap().deathban(event.getEntity().getUniqueId(), seconds);

        String time = TimeUtils.formatLongIntoDetailedString(seconds);

        new BukkitRunnable() {
            @Override
			public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (HCF.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().sendMessage(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                } else {
                    event.getEntity().sendMessage(ChatColor.YELLOW + "Come back in " + time + "!");
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                }
            }

        }.runTaskLater(HCF.getInstance(), 10 * 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!HCF.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
            return;
        }

        Player player = event.getPlayer();
        boolean shouldBypass = player.isOp() || player.hasPermission("foxtrot.staff");
        boolean isPowers = HCF.getInstance().getConfig().getBoolean("powers");

        if (shouldBypass) {
            HCF.getInstance().getLivesMap().setLives(player.getUniqueId(), 100);
            HCF.getInstance().getDeathbanMap().revive(player.getUniqueId());
            return;
        }

        if (isPowers) {
            if (!HCF.getInstance().getServerHandler().isPreEOTW() || !HCF.getInstance().getServerHandler().isEOTW()) return;
        }

        long unbannedOn = HCF.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
        long left = unbannedOn - System.currentTimeMillis();
        String time = TimeUtils.formatLongIntoDetailedString(left / 1000);

        if (HCF.getInstance().getServerHandler().isPreEOTW()) {
            player.sendMessage(ChatColor.RED + "Come back tomorrow for SOTW.");
            return;
        }

        int lives = HCF.getInstance().getLivesMap().getLives(player.getUniqueId());

        if (lives < 1) {
            player.sendMessage(ChatColor.RED + "You are currently deathbanned for " + ChatColor.BOLD + time + ChatColor.RED + "!");
            player.kickPlayer(ChatColor.RED + "You are currently deathbanned, for " + ChatColor.BOLD + time + ChatColor.RED + "!");
            return;
        }

        HCF.getInstance().getLivesMap().setLives(player.getUniqueId(), lives - 1);
        HCF.getInstance().getDeathbanMap().revive(player.getUniqueId());

        player.spigot().respawn();
        player.sendMessage(ChatColor.GREEN + "You have used a life to revive yourself. You now have " + (lives - 1) + " lives remaining.");
    }

}