package rip.orbit.hcteams.listener;

import net.frozenorb.qlib.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.SpawnTagHandler;

public class SpawnTagListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        /* Only tag player damagers, and deny tagging self */
        if (damager != null && damager != event.getEntity()) {
            if (HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame() != null && HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame().getPlayers().contains(damager))
                return;
            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
            SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE
                || !SpawnTagHandler.isTagged(player)
                || HCF.getInstance().getServerHandler().isVeltKitMap()
                || HCF.getInstance().getServerHandler().isPlaceBlocksInCombat()) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't place blocks whilst in combat.");
        event.setCancelled(true);
    }

}