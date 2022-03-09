package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.util.item.ItemUtils;

public class SpectateCommand {

    @Command(names = {"spec", "spectate", "game spec", "game spectate"}, description = "Join an ongoing event", permission = "")
    public static void execute(Player player) {
        if (!HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        if (player.hasMetadata("modmode")) {
            player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
            return;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return;
        }

        ongoingGame.addSpectator(player);
    }

}
