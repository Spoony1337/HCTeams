package rip.orbit.hcteams.map.duel.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.duel.DuelHandler;
import rip.orbit.hcteams.map.duel.menu.SelectWagerMenu;

public class DuelCommand {

    @Command(names = { "duel" }, permission = "")
    public static void duel(Player sender, @Param(name = "player") Player target) {
        DuelHandler duelHandler = HCF.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canDuel(sender, target)) {
            return;
        }

        new SelectWagerMenu(wager -> {
            sender.closeInventory();
            duelHandler.sendDuelRequest(sender, target, wager);
        }).openMenu(sender);
    }

}
