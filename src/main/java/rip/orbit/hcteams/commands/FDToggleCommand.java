package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class FDToggleCommand {

    @Command(names={ "FD Toggle", "ToggleFoundDiamonds", "ToggleFD" }, permission="")
    public static void fdToggle(Player sender) {
        boolean val = !HCF.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Found Diamonds messages!");
        HCF.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(sender.getUniqueId(), val);
    }

}