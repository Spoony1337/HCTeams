package rip.orbit.hcteams.map.duel.arena;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.arena.select.Selection;

import java.util.List;
import java.util.stream.Collectors;

public class ArenaCommands {

    @Command(names = { "duelarena create" }, permission = "op", async = true)
    public static void createArena(Player player, @Param(name = "name") String arenaName) {
        if (HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenaByName(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena named `" + arenaName + "` already exists!");
            return;
        }

        DuelArena arena = new DuelArena(arenaName);
        HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().addArena(arena);

        player.sendMessage(ChatColor.GREEN + "Created a new arena named `" + arenaName + "`!");
    }

    @Command(names = { "duelarena delete" }, permission = "op", async = true)
    public static void deleteArena(Player player, @Param(name = "arena") DuelArena arena) {
        HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().removeArena(arena);

        player.sendMessage(ChatColor.GREEN + "Deleted the arena named `" + arena.getName() + "`!");
    }

    @Command(names = { "duelarena list" }, permission = "op", async = true)
    public static void listArenas(Player player) {
        List<String> arenas = HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenas().stream()
                .map(arena -> (arena.isSetup() ? ChatColor.GREEN : ChatColor.RED).toString() + arena.getName())
                .collect(Collectors.toList());

        player.sendMessage(ChatColor.YELLOW + "Arenas: " + StringUtils.join(arenas, ChatColor.GRAY.toString() + ", "));
    }

    @Command(names = { "duelarena setpoint" }, permission = "op", async = true)
    public static void setPointSpawn(Player player, @Param(name = "a/b") String point, @Param(name = "arena") DuelArena arena) {
        if (point.equalsIgnoreCase("a") || point.equalsIgnoreCase("b")) {
            if (point.equalsIgnoreCase("a")) {
                arena.setPointA(player.getLocation());
            } else {
                arena.setPointB(player.getLocation());
            }

            HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().saveArenas();

            player.sendMessage(ChatColor.GREEN + "Updated point " + point.toUpperCase() + " of " + arena.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Point must be either a/b!");
        }
    }

    @Command(names = { "duelarena wand" }, permission = "op", async = true)
    public static void wand(Player player) {
        player.getInventory().addItem(Selection.SELECTION_WAND.clone());
        player.sendMessage(ChatColor.GREEN + "Gave you a selection wand.");
        player.sendMessage(ChatColor.GREEN + "Left click to set 1st corner. Right click to set 2nd corner.");
    }

    @Command(names = { "duelarena setbounds" }, permission = "op", async = true)
    public static void setBounds(Player player, @Param(name = "arena") DuelArena arena) {
        Selection selection = Selection.getOrCreateSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        arena.setBounds(selection.getCuboid());
        HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().saveArenas();

        player.sendMessage(ChatColor.GREEN + "Updated the boundaries of " + arena.getName() + "!");
    }

}
