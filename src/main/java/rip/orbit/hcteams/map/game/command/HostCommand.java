package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.map.game.menu.HostMenu;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.util.CC;

public class HostCommand {

    @Command(names = { "host", "game host" }, description = "Host an Event", permission = "", async = true)
    public static void execute(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't host an event while spawn-tagged!");
            return;
        }
        if (SOTWCommand.isSOTWTimer()) {
            player.sendMessage(CC.translate("&cYou cannot do this whilst SOTW timer is active."));
            return;
        }

        new HostMenu().openMenu(player);
    }

}
