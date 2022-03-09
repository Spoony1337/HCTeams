package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class KitCommand {

    @Command(names = "kit, mapkit", permission = "")
    public static void kit(Player sender) {
        String sharp = "Sharpness 1";
        String prot = "Protection 1";
        String bow = "Power 3";
        sender.sendMessage("§eEnchant Limits: §7" + prot + ", " + sharp + ", " + bow);
    }
}
