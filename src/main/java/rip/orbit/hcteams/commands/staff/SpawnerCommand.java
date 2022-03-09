package rip.orbit.hcteams.commands.staff;

import me.lbuddyboy.crates.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.gravity.util.ItemBuilder;

public class SpawnerCommand {
    @Command(names = "spawner", permission = "foxtrot.spawner")
    public static void spawnerCommand(Player player, @Param(name = "spawner") String spawnerName) {
        player.getInventory().addItem(new ItemBuilder(Material.MOB_SPAWNER).name(CC.translate("&r&a" + spawnerName + " Spawner")).build());
    }
}

