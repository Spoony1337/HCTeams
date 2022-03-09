package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.ServerHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.VisualClaim;
import rip.orbit.hcteams.team.claims.VisualClaimType;
import rip.orbit.hcteams.team.commands.team.subclaim.TeamSubclaimCommand;

import java.util.Arrays;

public class TeamClaimCommand implements Listener {

    public static ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_HOE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oClaiming Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect claim's corners",
                "",
                "§eRight Click §6Air",
                "§b- §fCancel current claim",
                "",
                "§9Crouch §eLeft Click §6Block/Air",
                "§b- §fPurchase current claim"

        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names={ "team claim", "t claim", "f claim", "faction claim", "fac claim" }, permission="")
    public static void teamClaim(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (HCF.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You are currently in the Warzone and can't claim land here. The Warzone ends at " + ServerHandler.WARZONE_RADIUS + ".");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

            int slot = -1;

            for (int i = 0; i < 9; i++) {
                if (sender.getInventory().getItem(i) == null) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
                return;
            }

            int finalSlot = slot;

            Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), new BukkitRunnable() {

                @Override
				public void run() {
                    sender.getInventory().setItem(finalSlot, SELECTION_WAND.clone());
                }

            });

            new VisualClaim(sender, VisualClaimType.CREATE, false).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }

            sender.sendMessage(ChatColor.GREEN + "Gave you a claim wand.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(TeamClaimCommand.SELECTION_WAND) || event.getItemDrop().getItemStack().equals(TeamResizeCommand.SELECTION_WAND)) {
            VisualClaim visualClaim = VisualClaim.getVisualClaim(event.getPlayer().getName());

            if (visualClaim != null) {
                event.setCancelled(true);
                visualClaim.cancel();
            }

            event.getItemDrop().remove();
        } else if (event.getItemDrop().getItemStack().equals(TeamSubclaimCommand.SELECTION_WAND)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
        event.getPlayer().getInventory().remove(TeamClaimCommand.SELECTION_WAND);
        event.getPlayer().getInventory().remove(TeamResizeCommand.SELECTION_WAND);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
        event.getPlayer().getInventory().remove(TeamClaimCommand.SELECTION_WAND);
        event.getPlayer().getInventory().remove(TeamResizeCommand.SELECTION_WAND);
    }

}