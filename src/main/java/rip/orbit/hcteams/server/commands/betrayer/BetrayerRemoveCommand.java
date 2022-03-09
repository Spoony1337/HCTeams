package rip.orbit.hcteams.server.commands.betrayer;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.Betrayer;

import java.util.UUID;

public class BetrayerRemoveCommand {

    @Command(names = {"betrayer remove"}, permission = "op")
    public static void betrayerRemove(Player sender, @Param(name = "player") UUID player) {
        Betrayer betrayer = HCF.getInstance().getServerHandler().getBetrayer(player);
        if (betrayer != null) {
            HCF.getInstance().getServerHandler().getBetrayers().remove(betrayer);
            HCF.getInstance().getServerHandler().save();

            sender.sendMessage(ChatColor.GREEN + "Removed " + UUIDUtils.name(player) + "'s betrayer tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't a betrayer.");
        }
    }

}