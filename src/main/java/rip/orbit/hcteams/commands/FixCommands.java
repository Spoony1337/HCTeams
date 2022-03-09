package rip.orbit.hcteams.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.util.*;

public class FixCommands {

    @Command(names={ "purgeoldteams" }, permission="op", async = true)
    public static void purgeOldTeams(Player sender) {
        DBCollection coll = HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("TeamActions");
        DBCursor cursor = coll.find();
        Map<String, List<BasicDBObject>> data = new HashMap<>();

        sender.sendMessage("starting database pull");

        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            data.computeIfAbsent(obj.getString("teamId"), i -> new ArrayList<>());
            data.get(obj.getString("teamId")).add(obj);
        }

        sender.sendMessage("Collected data for " + data.size() + " unique teams... starting to sort");

        data.values().forEach(objs -> {
            objs.sort(Comparator.comparing(a -> a.getDate("time")));
        });

        sender.sendMessage("sorted teams, starting to remove old");

        data.values().removeIf(e -> {
            sender.sendMessage(e.get(0) + " -> " + e.get(e.size() - 1));
            return e.get(e.size() - 1).getString("type").equals("playerDisbandTeam");
        });

        sender.sendMessage("removed old teams, currently " + data.size() + " are left");

        data.forEach((key, e) -> {
            BasicDBObject latest = e.get(e.size() - 1);
            Team team = new Team(latest.getString("teamName"));
            team.load((BasicDBObject) latest.get("teamAfterAction"));

            //HCF.getInstance().getTeamHandler().setupTeam(team);
            sender.sendMessage(ChatColor.GREEN + "would have Reinstated team " + team.getName() + " with " + team.getClaims().size() + " claims and " + team.getMembers().size() + " members");
        });

        sender.sendMessage("reinstated all teams");
    }

    @Command(names = {"fixnulls"}, permission = "op")
    public static void fixNulls(CommandSender sender) {
        DBCollection coll = HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("Teams");
        DBCursor cursor = coll.find();

        cursor.forEach(document -> {
            if (!document.containsKey("Name")) return;

            Team team = new Team((String) document.get("Name"));
            team.load((BasicDBObject) document);

            HCF.getInstance().getTeamHandler().setupTeam(team);
        });
    }

}