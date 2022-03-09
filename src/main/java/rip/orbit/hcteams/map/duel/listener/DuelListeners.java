package rip.orbit.hcteams.map.duel.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.frozenorb.qlib.qLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.duel.Duel;
import rip.orbit.hcteams.map.duel.DuelHandler;
import rip.orbit.hcteams.map.duel.DuelState;

import java.lang.reflect.InvocationTargetException;

public class DuelListeners implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (HCF.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (HCF.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        if (HCF.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        if (HCF.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            DuelHandler duelHandler = HCF.getInstance().getMapHandler().getDuelHandler();

            Duel duel = duelHandler.getDuel(player);

            if (duel == null) {
                return;
            }

            if (duel.getState() != DuelState.FIGHTING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        DuelHandler duelHandler = HCF.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.isInDuel(player)) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (itemStack.getType() == Material.GLASS_BOTTLE) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DuelHandler duelHandler = HCF.getInstance().getMapHandler().getDuelHandler();

        Duel duel = duelHandler.getDuel(player);

        if (duel == null) {
            return;
        }

        if (duel.getState() != DuelState.FINISHED) {
            Player opponent = Bukkit.getPlayer(duel.getOpponent(player.getUniqueId()));

            opponent.sendMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has disconnected.");
        }

        duel.disconnect(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DuelHandler duelHandler = HCF.getInstance().getMapHandler().getDuelHandler();

        Duel duel = duelHandler.getDuel(player);

        if (duel == null) {
            return;
        }

        // send lightning packets to both players rather than striking globally
        PacketContainer lightning = createLightningPacket(player.getLocation());
        for (Player member : duel.getPlayers()) {
            member.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, 0.8F + qLib.RANDOM.nextFloat() * 0.2F);
            member.playSound(player.getLocation(), Sound.EXPLODE, 2F, 0.5F + qLib.RANDOM.nextFloat() * 0.2F);

            sendLightningPacket(member, lightning);
        }

        event.getDrops().clear();
        player.spigot().respawn();

        duel.eliminate(player);
        player.teleport(player.getLocation().add(0, 2, 0));
    }

    private PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128); // entity id of 128
        lightningPacket.getIntegers().write(4, 1); // type of lightning (1)
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D)); // x
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D)); // y
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D)); // z

        return lightningPacket;
    }

    private void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {
            // will never happen, ProtocolWrapper (the lib this code was from)
            // ignores this exception as well
        }
    }

}
