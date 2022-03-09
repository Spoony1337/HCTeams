package rip.orbit.hcteams.persist;

import com.mongodb.DBCollection;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.util.UUID;

public class RedisSaveTask extends BukkitRunnable {

    @Override
	public void run() {
        save(null, false);
    }

    public static int save(CommandSender issuer, boolean forceAll) {
        long startMs = System.currentTimeMillis();
        int teamsSaved = qLib.getInstance().runRedisCommand(redis -> {

            DBCollection teamsCollection = HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("Teams");
            
            int changed = 0;

            for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
                if (team.isNeedsSave() || forceAll) {
                    changed++;

                    redis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                    teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                }
                
                if (forceAll) {
                    for (UUID member : team.getMembers()) {
                        HCF.getInstance().getTeamHandler().setTeam(member, team, true);
                    }
                }
            }

            redis.set("RostersLocked", String.valueOf(HCF.getInstance().getTeamHandler().isRostersLocked()));
            if (issuer != null && forceAll) redis.save();
            return (changed);
        });

        int time = (int) (System.currentTimeMillis() - startMs);

        if (teamsSaved != 0) {
            System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");

            if (issuer != null) {
                issuer.sendMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
            }
        }

        return (teamsSaved);
    }

}