package rip.orbit.hcteams.map.game.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;

public class ToggleCommand {

    public static void execute(CommandSender sender) {
        HCF.getInstance().getMapHandler().getGameHandler().setDisabled(!HCF.getInstance().getMapHandler().getGameHandler().isDisabled());

        if (HCF.getInstance().getMapHandler().getGameHandler().isDisabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Events are now disabled!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Events are now enabled!");
        }
    }

}
