package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;

public class MobCommand {

    @Command(names={"spawnmob", "mob"}, permission="foxtrot.spawnmob", description="Spawn mobs! Supports stacking")
    public static void spawnmob(Player sender, @Param(name="mob[,mob,mob...]") String mobs, @Param(name="amount", defaultValue="1") int amount) {
        String[] split = mobs.split(",");
        ArrayList<EntityType> types = new ArrayList<>();
        for (String part : split) {
            EntityType type = EntityUtils.parse((String)part);
            if (type == null) {
                sender.sendMessage((Object)ChatColor.RED + "Mob '" + part + "' not found.");
                return;
            }
            if (!type.isAlive()) {
                sender.sendMessage((Object)ChatColor.RED + "Entity type '" + part + "' is not a valid mob.");
                return;
            }
            types.add(type);
        }
        if (sender.getTargetBlock((HashSet)null, 30) == null) {
            sender.sendMessage((Object)ChatColor.RED + "Please look at a block.");
            return;
        }
        if (types.size() == 0) {
            sender.sendMessage((Object)ChatColor.RED + "Idk how you got here but um... Nope.");
            return;
        }
        Location location = sender.getTargetBlock((HashSet)null, 30).getLocation().add(0.0, 1.0, 0.0);
        int totalAmount = 0;
        for (int i = 0; i < amount; ++i) {
            Entity current = sender.getWorld().spawnEntity(location, (EntityType)types.get(0));
            ++totalAmount;
            for (int x = 1; x < types.size(); ++x) {
                Entity newEntity = sender.getWorld().spawnEntity(location, (EntityType)types.get(x));
                current.setPassenger(newEntity);
                ++totalAmount;
            }
        }
        sender.sendMessage((Object)ChatColor.GOLD + "Spawned " + (Object)ChatColor.WHITE + totalAmount + (Object)ChatColor.GOLD + " entities.");
    }
}

