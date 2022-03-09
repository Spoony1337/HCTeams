package rip.orbit.hcteams.events.citadel.listeners;

import net.frozenorb.qlib.event.HourEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.profile.global.GlobalInfo;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.events.citadel.events.CitadelActivatedEvent;
import rip.orbit.hcteams.events.citadel.events.CitadelCapturedEvent;
import rip.orbit.hcteams.events.events.EventActivatedEvent;
import rip.orbit.hcteams.events.events.EventCapturedEvent;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            HCF.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (DTRBitmask.CITADEL.appliesAt(event.getPlayer().getLocation())) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
                if (!HCF.getInstance().getCitadelHandler().canLootCitadel((Player) event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (playerTeam != null) {
                HCF.getInstance().getCitadelHandler().addCapper(playerTeam.getUniqueId());
                playerTeam.setCitadelsCapped(playerTeam.getCitadelsCapped() + 1);

                playerTeam.getMembers().forEach(uuid -> {
                    Profile profile = Profile.getByUuid(uuid);
                    GlobalInfo info = profile.getGlobalInfo();
                    if (HCF.getInstance().getMapHandler().isKitMap()) {
                        info.setKitsCitadelCaps(info.getKitsCitadelCaps() + 1);
                    } else {
                        info.setHcfCitadelCaps(info.getHcfCitadelCaps() + 1);
                    }
                    profile.save();
                });
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        HCF.getInstance().getCitadelHandler().resetCappers();
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        HCF.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(HCF.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + "");
    }

    @EventHandler(priority=EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (playerTeam != null && HCF.getInstance().getCitadelHandler().getCappers().contains(playerTeam.getUniqueId())) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENDER_PEARL) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getPlayer().getLocation();
                if (DTRBitmask.CITADEL.appliesAt(location)) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(CC.RED + "You cannot do this whilst at citadel.");
                    }
                }
            }
        }
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        boolean slashKit = message.startsWith("/kit");
        if (slashKit || message.startsWith("/gkit") || message.startsWith("/gkits") || message.startsWith("/gkitz")) {
            Location location = event.getPlayer().getLocation();
            if (DTRBitmask.CITADEL.appliesAt(location)) {
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(CC.RED + "You cannot do this whilst at citadel.");
                }
            }
            if (!event.getPlayer().isOp() && message.split(" ").length > 2) {
                event.setCancelled(true);
            }

            if (slashKit && message.startsWith("/kits")) {
                event.setMessage(message.replace("/kit", "/gkit"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Location location = player.getLocation();
            if (DTRBitmask.CITADEL.appliesAt(location)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "You cannot do this whilst at citadel .");
                }
            }
        }
    }

    @EventHandler
    public void onHour(HourEvent event) {
        // Every other hour
        if (event.getHour() % 2 == 0) {
            int respawned = HCF.getInstance().getCitadelHandler().respawnCitadelChests();

            if (respawned != 0) {
                HCF.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");
            }
        }
    }

}