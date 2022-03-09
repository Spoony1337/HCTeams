package rip.orbit.hcteams.commands.staff;


import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class SpawnCommand {


    @Command(names={ "spawn" }, permission="foxtrot.spawn")
    public static void spawn(Player sender) {
        Player player = (Player) sender;
        if (sender.hasPermission("foxtrot.spawn")) {
            sender.teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
        } else {
            HCF.getInstance().getServerHandler().startSpawnteleport(sender);
        }
    }
}
