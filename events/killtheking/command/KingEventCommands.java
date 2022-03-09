package rip.orbit.hcteams.events.killtheking.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.killtheking.KingEvent;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.TimeUtil;


public class KingEventCommands {

    @Command(names = {"king", "king", "ke", "killtheking", "kingevent", "killthekingevent"}, permission = "op")
    public static void help(CommandSender sender) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cOnly in Kitmap, active mode for the event"));
            return;
        }
        if (KingEvent.isStarted(false)) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.translate("&c&lKing Event is already started! &e(/king info)"));
            sender.sendMessage(" ");
        }
        sender.sendMessage(ChatColor.BLUE + CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&c/king info"));
        sender.sendMessage(CC.translate("&c/king start"));
        sender.sendMessage(CC.translate("&c/king stop"));
        sender.sendMessage(CC.translate("&c/king setFocused <target>"));
        sender.sendMessage(CC.translate("&c/king togglescoreboard"));
        sender.sendMessage(CC.translate("&c/king setreward <reward>"));
        sender.sendMessage(ChatColor.BLUE + CC.CHAT_BAR);
    }

    @Command(names = {"king info", "king info", "ke info"}, permission = "")
    public static void info(CommandSender sender) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        if (!KingEvent.isStarted(false)) {
            sender.sendMessage(CC.translate("&c&lKing event is not active!"));
            return;
        }

        Player focusedPlayer = KingEvent.getFocusedPlayer();
        sender.sendMessage(CC.translate("&7&m--------------------------------------"));
        sender.sendMessage(CC.translate("&4&lKTK Event Information"));
        sender.sendMessage(CC.translate("&4&lCurrent King!&7: &c" + focusedPlayer.getName()));
        sender.sendMessage(CC.translate(" &4&l" + focusedPlayer.getName() + "'s Information"));
        Location focusedLocation = focusedPlayer.getLocation();
        sender.sendMessage(CC.translate(" &c&lLocation &7&c&l" + focusedLocation.getBlockX() + "&7, &c&l" + focusedLocation.getBlockY() + "&e, &c&l" + focusedLocation.getBlockZ()));
        sender.sendMessage(CC.translate(" &c&lReward &7" + KingEvent.getReward()));
        sender.sendMessage(CC.translate("&7&m--------------------------------------"));
    }

    @Command(names = {"king start", "king start", "ke start"}, permission = "op")
    public static void start(CommandSender sender) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        if (KingEvent.isStarted(true)) {
            sender.sendMessage(CC.translate("&c&lKing Event already started!"));
            return;
        }
        if (KingEvent.getFocusedPlayer() == null) {
            sender.sendMessage(CC.translate("&ePlayer not found, use /king setfocused <target>."));
            return;
        }
        if (KingEvent.getReward() == null) {
            sender.sendMessage(CC.translate("&eReward not found, use /king setreward <reward>"));
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam("SouthRoad");
        Player focusedPlayer = KingEvent.getFocusedPlayer();

        if (team != null && team.getHq() != null) {
            if (!focusedPlayer.teleport(team.getHq())) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (!player.hasPermission("foxtrot.staff")) return;

                    player.sendMessage(CC.translate("&6&l[KING EVENT] &c" + focusedPlayer.getName() + " could not be teleported South Road's HQ."));
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                });
            }
        }

        SpawnTagHandler.addOffensiveSeconds(focusedPlayer, 300 * 3);
        KingEvent.setStarted(true);
        Bukkit.broadcastMessage(CC.translate("&6&l[KING EVENT] &eEvent has started. Type '&a/king info&e' for more information about this event!"));
        focusedPlayer.sendMessage(CC.translate("&cNow you're &6&lTHE KING&c, you should survive 15 minutes to earn &6" + KingEvent.getReward() + "&c."));
        KingEvent.equipPlayer();
        KingEvent.setTime(System.currentTimeMillis() + TimeUtil.parseTime("15m"));
        Bukkit.getOnlinePlayers().forEach(online -> online.sendMessage(KingEvent.getStartedAlert()));
    }

    @Command(names = {"king stop", "king stop", "ke stop"}, permission = "op")
    public static void stop(CommandSender sender) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        if (!KingEvent.isStarted(true)) {
            sender.sendMessage(CC.translate("&c&lKing Event is not running!"));
            return;
        }
        KingEvent.clean();
        Bukkit.broadcastMessage(CC.translate("&6&l[KING EVENT] &eEvent has been stopped."));
    }

    @Command(names = {"king setfocused", "king setfocused", "ke setfocused"}, permission = "op")
    public static void focusTarget(CommandSender sender, @Param(name = "target") String target) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        if (KingEvent.isStarted(true)) {
            sender.sendMessage(CC.translate("&c&lKing Event already started!"));
            return;
        }
        Player player = Bukkit.getPlayer(target);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        KingEvent.setFocusedPlayer(player);
        sender.sendMessage(CC.translate("&6&l[KING EVENT] &a" + player.getName() + " has been focused for the Event."));
        player.sendMessage(CC.translate("&6&l[KING EVENT] &aYou have been focused by an &cAdministrator&a!"));
    }

    @Command(names = {"king setreward", "king setreward", "ke setreward"}, permission = "op")
    public static void cmd(CommandSender sender, @Param(name = "command") String command) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        command = command.replace(";", " ");
        KingEvent.setReward(command);
        sender.sendMessage(CC.translate("&6&l[KING EVENT] &eReward set to '&a" + command + "&e'."));
    }

    @Command(names = {"king togglescoreboard", "king togglescoreboard", "ke togglescoreboard"}, permission = "op")
    public static void toggleScoreboard(CommandSender sender) {
        if (!HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }
        KingEvent.setScoreboardInfo(!KingEvent.isScoreboardInfo());
        sender.sendMessage(CC.translate("&6&l[KING EVENT] &eEvent scoreboard is now " + (KingEvent.isScoreboardInfo() ? "&aen" : "&cdis") + "abled&e!"));
    }
}