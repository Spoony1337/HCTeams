package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class SetLobbyCommand {

    @Command(names = { "game setlobby" }, description = "Sets the lobby spawn location for events", permission = "op", async = true)
    public static void execute(Player player) {
        HCF.getInstance().getMapHandler().getGameHandler().getConfig().setLobbySpawnLocation(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Updated event lobby location!");
    }

}
