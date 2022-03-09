package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.util.ArrayList;
import java.util.List;

public class ForceDisbandAllCommand {

    private static Runnable confirmRunnable;
    private static Runnable etowConfirm;

    @Command(names = {"forcedisbandall"}, permission = "op")
    public static void forceDisbandAll(CommandSender sender) {
        confirmRunnable = () -> {
            List<Team> teams = new ArrayList<>();

            for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
                teams.add(team);
            }

            for (Team team : teams) {
                team.disband();
            }

            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/forcedisbandall cancel" + ChatColor.RED + " to cancel.");
    }

    @Command(names = {"forcedisbandall confirm"}, permission = "op")
    public static void confirm(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        confirmRunnable.run();
    }

    @Command(names = {"forcedisbandall cancel"}, permission = "op")
    public static void cancel(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        confirmRunnable = null;
    }

    @Command(names = {"eotwdisband"}, permission = "op")
    public static void forcedisbandeotw(CommandSender sender) {
        etowConfirm = () -> {
            List<Team> teams = new ArrayList<>();

            for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
                teams.add(team);
            }

            for (Team team : teams) {
                team.disband();
            }

            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/eotwdisband confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/eotwdisband cancel" + ChatColor.RED + " to cancel.");
    }

    @Command(names = {"eotwdisband confirm"}, permission = "op")
    public static void s(CommandSender sender) {
        if (etowConfirm == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        etowConfirm.run();
    }

    @Command(names = {"eotwdisband cancel"}, permission = "op")
    public static void n(CommandSender sender) {
        if (etowConfirm == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        etowConfirm = null;

    }
}