package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.persist.RedisSaveTask;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;

import java.io.*;
import java.util.Date;

public class TeamDataCommands {

    @Command(names={ "exportteamdata" }, permission="op")
    public static void exportTeamData(CommandSender sender, @Param(name="file") String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name already exists.");
            return;
        }

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            out.writeUTF(sender.getName());
            out.writeUTF(new Date().toString());
            out.writeInt(HCF.getInstance().getTeamHandler().getTeams().size());

            for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
                out.writeUTF(team.getName());
                out.writeUTF(team.saveString(false));
            }

            sender.sendMessage(ChatColor.GOLD + "Saved " + HCF.getInstance().getTeamHandler().getTeams().size() + " teams to " + ChatColor.GREEN + file.getAbsolutePath() + ChatColor.GOLD + ".");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import teams! Check console for errors.");
        }
    }

    @Command(names={ "importteamdata" }, permission="op")
    public static void importTeamData(CommandSender sender, @Param(name="file") String fileName) {
        long startTime = System.currentTimeMillis();
        File file = new File(fileName);

        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name does not exist.");
            return;
        }

        try {
            qLib.getInstance().runRedisCommand((jedis) -> {
                jedis.flushAll();
                return null;
            });

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            String author = in.readUTF();
            String created = in.readUTF();
            int teamsToRead = in.readInt();

            for (int i = 0; i < teamsToRead; i++) {
                String teamName = in.readUTF();
                String teamData = in.readUTF();

                Team team = new Team(teamName);
                team.load(teamData, true);

                HCF.getInstance().getTeamHandler().setupTeam(team, true);
            }

            LandBoard.getInstance().loadFromTeams(); // to update land board shit
            HCF.getInstance().getTeamHandler().recachePlayerTeams();
            RedisSaveTask.save(sender, true);
            sender.sendMessage(ChatColor.GOLD + "Loaded " + teamsToRead + " teams from an export created by " + ChatColor.GREEN + author + ChatColor.GOLD + " at " + ChatColor.GREEN + created + ChatColor.GOLD + " and recached claims in " + ChatColor.GREEN.toString() +  (System.currentTimeMillis() - startTime) + "ms.");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import teams! Check console for errors.");
        }
    }

}