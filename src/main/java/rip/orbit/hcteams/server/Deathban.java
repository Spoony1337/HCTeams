package rip.orbit.hcteams.server;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Deathban {

    private static Map<String, Integer> deathban = new LinkedHashMap<>();
    private static int defaultMinutes = 120;

    static {
        deathban.put("Default", 30);
        deathban.put("Star", 25);
        deathban.put("Comet", 20);
        deathban.put("Meteor", 15);
        deathban.put("Solar", 10);
        deathban.put("Asteroid", 5);
        deathban.put("Orbit", 1);
        deathban.put("Youtube", 1);
        deathban.put("Famous", 1);
        deathban.put("Partner", 1);
    }

    public static void load(BasicDBObject object) {
        deathban.clear();

        for (String key : object.keySet()) {
            if (key.equals("DEFAULT"))  {
                defaultMinutes = object.getInt(key);
            } else {
                deathban.put(key, object.getInt(key));
            }
        }
    }

    public static int getDeathbanSeconds(Player player) {
        int minutes = defaultMinutes;

        for (Map.Entry<String, Integer> entry : deathban.entrySet()) {
            if (player.hasPermission("ranks." + entry.getKey().toLowerCase()) && entry.getValue() < minutes) {
                minutes = entry.getValue();
            }
        }

        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }

}
