package rip.orbit.hcteams.team.commands.team;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;

import java.util.*;

public class TeamListCommand {

    @Command(names={ "team list", "t list", "f list", "faction list", "fac list" }, permission="")
    public static void teamList(Player sender, @Param(name="page", defaultValue="1") int page) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            @Override
			public void run() {
                if (page < 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                    return;
                }

                Map<Team, Integer> teamPlayerCount = new HashMap<>();

                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("invisible")) {
                        continue;
                    }

                    Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(player);

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }

                int maxPages = (teamPlayerCount.size() / 10) + 1;
                int currentPage = Math.min(page, maxPages);

                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);

                int start = (currentPage - 1) * 10;
                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);
                sender.sendMessage(ChatColor.BLUE + "Team List " +  ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages + ")");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    index++;

                    if (index < start) {
                        continue;
                    }

                    if (index > start + 10) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();
                    Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

                    teamMessage.text(index + ". ").color(ChatColor.GRAY).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(ChatColor.YELLOW).tooltip(
                            ChatColor.YELLOW + "DTR: " + teamEntry.getKey().getDTRColor() + Team.DTR_FORMAT.format(teamEntry.getKey().getDTR()) + ChatColor.YELLOW + " / " + teamEntry.getKey().getMaxDTR() + "\n" +
                            ChatColor.GREEN + "Click to view team info").command("/t who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(CC.translate(" &7[" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + "&7]"));
                    teamMessage.send(sender);
                }

                sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/" + maxPages + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/t list <page#>" + ChatColor.GRAY + ".");
                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(HCF.getInstance());
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<java.util.Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}