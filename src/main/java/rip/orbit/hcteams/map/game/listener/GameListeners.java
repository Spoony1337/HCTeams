package rip.orbit.hcteams.map.game.listener;

import net.frozenorb.qlib.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.map.game.GameHandler;
import rip.orbit.hcteams.map.game.GameItems;
import rip.orbit.hcteams.map.game.GameState;
import rip.orbit.hcteams.map.game.impl.ffa.FFAGame;
import rip.orbit.hcteams.map.game.impl.spleef.SpleefGame;
import rip.orbit.hcteams.util.item.InventoryUtils;

import java.util.Arrays;
import java.util.List;

public class GameListeners implements Listener {

    private static List<Material> CONTAINER_TYPES = Arrays.asList(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.DISPENSER,
            Material.HOPPER,
            Material.DROPPER,
            Material.BREWING_STAND
    );

    private GameHandler gameHandler = HCF.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.getLocation().getWorld().getName().equals("kits_events")) return;
        player.teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
                if (event.hasItem() && event.getItem().equals(GameItems.LEAVE_EVENT)) {
                    if (gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                        gameHandler.getOngoingGame().removePlayer(event.getPlayer());
                    } else if (gameHandler.getOngoingGame().isSpectating(event.getPlayer().getUniqueId())) {
                        gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
                    }

                    event.getPlayer().teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (CONTAINER_TYPES.contains(event.getClickedBlock().getType())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            // Spleef
            if (gameHandler.getOngoingGame() instanceof SpleefGame
                    && gameHandler.getOngoingGame().getState() == GameState.RUNNING
                    && gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                event.setCancelled(System.currentTimeMillis() < gameHandler.getOngoingGame().getStartedAt() + 6_000L);
                return;
            }

            event.setCancelled(true);
        } else if (event.getBlock().getWorld().equals(gameHandler.getWorld())) {
            event.setCancelled(!event.getPlayer().hasMetadata("build"));
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if (event.getBlock().getWorld().equals(gameHandler.getWorld())) {
            event.setCancelled(!event.getPlayer().hasMetadata("build"));
        }
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketFillEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
                // FFA
                if (gameHandler.getOngoingGame() instanceof FFAGame) {
                    if (!gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    if (InventoryUtils.isArmor(event.getItemDrop().getItemStack())) {
                        event.setCancelled(true);
                        return;
                    } else {
                        event.setCancelled(false);
                    }

                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            ((Player) event.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (event.getEntity() instanceof Player) {
                // Spleef
                if (gameHandler.getOngoingGame() instanceof SpleefGame && event.getDamager() instanceof Snowball) return;

                Player victim = (Player) event.getEntity();
                Player damager = PlayerUtils.getDamageSource(event.getDamager());

                if (damager == null) {
                    return;
                }

                Game ongoingGame = gameHandler.getOngoingGame();
                boolean victimInGame = ongoingGame.isPlayingOrSpectating(victim.getUniqueId());
                boolean damagerInGame = ongoingGame.isPlayingOrSpectating(damager.getUniqueId());

                if (!victimInGame && !damagerInGame) {
                    return;
                }

                if (!victimInGame || !damagerInGame) {
                    event.setCancelled(true);
                } else {
                    ongoingGame.handleDamage(victim, damager, event);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (gameHandler.isOngoingGame()) {
                if (gameHandler.getOngoingGame().isPlaying(player.getUniqueId())) {
                    if (!(event instanceof EntityDamageByEntityEvent) && !(gameHandler.getOngoingGame() instanceof FFAGame)) {
                        event.setCancelled(true);
                    }
                } else if (gameHandler.getOngoingGame().isSpectating(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (gameHandler.getOngoingGame().isPlayingOrSpectating(event.getWhoClicked().getUniqueId())) {
                // FFA
                if (gameHandler.getOngoingGame() instanceof FFAGame) {
                    if (!gameHandler.getOngoingGame().isPlaying(event.getWhoClicked().getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(event.getSlotType() == InventoryType.SlotType.ARMOR);
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (gameHandler.isOngoingGame()) {
            Game ongoingGame = gameHandler.getOngoingGame();
            if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                gameHandler.getOngoingGame().eliminatePlayer(event.getPlayer(), null);
            } else if (ongoingGame.isSpectating(event.getPlayer().getUniqueId())) {
                gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!gameHandler.isOngoingGame())
            return;

        if (!gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId()))
            return;

        if (event.getAction() == Action.PHYSICAL && (event.getClickedBlock().getType() == Material.CROPS || event.getClickedBlock().getType() == Material.SOIL)) {
            event.setCancelled(true);
        }
    }
}
