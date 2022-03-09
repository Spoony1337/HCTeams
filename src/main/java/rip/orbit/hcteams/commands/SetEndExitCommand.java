package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.Utils;

public class SetEndExitCommand {

    @Command(names = {"setendexit"}, permission = "op")
    public static void setendexit(Player sender) {
        HCF.getInstance().getConfig().set("Locations.end_exit", Utils.stringifyLocation(sender.getLocation()));
        HCF.getInstance().saveConfig();
        sender.sendMessage(CC.translate("&aSuccessfully created endexit."));
    }
}

