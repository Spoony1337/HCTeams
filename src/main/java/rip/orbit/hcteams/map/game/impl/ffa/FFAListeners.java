package rip.orbit.hcteams.map.game.impl.ffa;

import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.GameHandler;

public class FFAListeners implements Listener {

    private static final ItemStack GOLDEN_APPLE = ItemBuilder.of(Material.GOLDEN_APPLE).build();

    private final GameHandler gameHandler = HCF.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof FFAGame) {
            FFAGame ongoingGame = (FFAGame) gameHandler.getOngoingGame();

            if (!ongoingGame.isPlaying(player.getUniqueId())) {
                return;
            }

            Player killer = player.getKiller();

            ongoingGame.eliminatePlayer(player, killer);
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void onInventoryClickEvent(InventoryClickEvent event) {
//        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof FFAGame) {
//            if (!gameHandler.getOngoingGame().isPlaying(event.getWhoClicked().getUniqueId())) {
//                return;
//            }
//
//            event.setCancelled(event.getSlotType() == InventoryType.SlotType.ARMOR);
//        }
//    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof FFAGame) {
            FFAGame ongoingGame = (FFAGame) gameHandler.getOngoingGame();

            if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                ItemStack drop = event.getItemDrop().getItemStack();

                if (drop.getType() == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                }
            }
        }
    }

}
