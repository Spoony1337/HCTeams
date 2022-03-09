package rip.orbit.hcteams.map.game.arena;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.GameType;
import rip.orbit.hcteams.map.game.arena.select.Selection;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommands {

    @Command(names = { "game arena list" }, description = "Lists the existing arenas", permission = "op", async = true)
    public static void listArenas(Player player) {
        List<String> arenas = new ArrayList<>();

        for (GameArena arena : HCF.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
            if (arena.isSetup()) {
                arenas.add(ChatColor.GREEN.toString() + ChatColor.BOLD + arena.getName());
            } else {
                arenas.add(ChatColor.RED.toString() + ChatColor.BOLD + arena.getName());
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Arenas: " + StringUtils.join(arenas, ChatColor.GRAY.toString() + ", "));
    }

    @Command(names = { "game arena create" }, description = "Creates a new arena", permission = "op", async = true)
    public static void createArena(Player player, @Param(name = "name") String arenaName) {
        if (HCF.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena named `" + arenaName + "` already exists!");
            return;
        }

        GameArena arena = new GameArena(arenaName);
        HCF.getInstance().getMapHandler().getGameHandler().getConfig().trackArena(arena);

        player.sendMessage(ChatColor.GREEN + "Created a new arena named `" + arenaName + "`!");
    }

    @Command(names = { "game arena delete" }, description = "Deletes an existing arena", permission = "op", async = true)
    public static void deleteArena(Player player, @Param(name = "arena") GameArena arena) {
        HCF.getInstance().getMapHandler().getGameHandler().getConfig().forgetArena(arena);

        player.sendMessage(ChatColor.GREEN + "Deleted the arena named `" + arena.getName() + "`!");
    }

    @Command(names = { "game arena setpoint" }, description = "Set the A/B point location of an arena", permission = "op", async = true)
    public static void setPointSpawn(Player player, @Param(name = "a/b") String point, @Param(name = "arena") GameArena arena) {
        if (point.equalsIgnoreCase("a") || point.equalsIgnoreCase("b")) {
            if (point.equalsIgnoreCase("a")) {
                arena.setPointA(player.getLocation());
            } else {
                arena.setPointB(player.getLocation());
            }

            HCF.getInstance().getMapHandler().getGameHandler().saveConfig();

            player.sendMessage(ChatColor.GREEN + "Updated point " + point.toUpperCase() + " of " + arena.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Point must be either a/b!");
        }
    }

    @Command(names = { "game arena setspec" }, description = "Set the spectator spawn point of an arena", permission = "op", async = true)
    public static void setSpectatorSpawn(Player player, @Param(name = "arena") GameArena arena) {
        arena.setSpectatorSpawn(player.getLocation());
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated Sumo's spectator spawn location!");
    }

    @Command(names = { "game arena wand" }, description = "Set the bounding region of an arena", permission = "op", async = true)
    public static void wand(Player player) {
        player.getInventory().addItem(Selection.SELECTION_WAND.clone());
        player.sendMessage(ChatColor.GREEN + "Gave you a selection wand.");
        player.sendMessage(ChatColor.GREEN + "Left click to set 1st corner. Right click to set 2nd corner.");
    }

    @Command(names = { "game arena setbounds" }, description = "Set the bounding region of an arena", permission = "op", async = true)
    public static void setBounds(Player player, @Param(name = "arena") GameArena arena) {
        Selection selection = Selection.getOrCreateSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        arena.setBounds(selection.getCuboid());
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated the boundaries of " + arena.getName() + "!");
    }

    @Command(names = { "game arena types add" }, description = "Makes a game type compatible with an arena", permission = "op", async = true)
    public static void addType(Player player, @Param(name = "arena") GameArena arena, @Param(name = "gameType") GameType gameType) {
        arena.getCompatibleGameTypes().add(gameType.name().toLowerCase());
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is now compatible with the " + gameType.getDisplayName() + " event!");
    }

    @Command(names = { "game arena types remove" }, description = "Makes a game type incompatible with an arena", permission = "op", async = true)
    public static void removeType(Player player, @Param(name = "arena") GameArena arena, @Param(name = "gameType") GameType gameType) {
        arena.getCompatibleGameTypes().remove(gameType.name().toLowerCase());
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is no longer compatible with the " + gameType.getDisplayName() + " event!");
    }

}
