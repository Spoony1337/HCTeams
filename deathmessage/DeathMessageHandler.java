package rip.orbit.hcteams.deathmessage;

import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.deathmessage.listeners.DamageListener;
import rip.orbit.hcteams.deathmessage.objects.Damage;
import rip.orbit.hcteams.deathmessage.trackers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathMessageHandler {

    private static Map<String, List<Damage>> damage = new HashMap<>();

    public static void init() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), HCF.getInstance());

        HCF.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new BurnTracker(), HCF.getInstance());
    }

    public static List<Damage> getDamage(Player player) {
        return (damage.get(player.getName()));
    }

    public static void addDamage(Player player, Damage addedDamage) {
        if (!damage.containsKey(player.getName())) {
            damage.put(player.getName(), new ArrayList<>());
        }

        List<Damage> damageList = damage.get(player.getName());

        while (damageList.size() > 30) {
            damageList.remove(0);
        }

        damageList.add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getName());
    }

}