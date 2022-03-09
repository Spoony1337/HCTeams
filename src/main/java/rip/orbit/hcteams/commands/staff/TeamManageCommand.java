package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.commands.SetTeamBalanceCommand;
import rip.orbit.hcteams.team.commands.team.TeamCreateCommand;
import rip.orbit.hcteams.team.menu.*;
import rip.orbit.hcteams.util.CC;

public class TeamManageCommand {

    @Command(names = {"manageteam leader"}, permission = "foxtrot.manage")
    public static void teamLeader(Player sender, @Param(name = "team") Team team) {
        new SelectNewLeaderMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam promote"}, permission = "foxtrot.manage")
    public static void promoteTeam(Player sender, @Param(name = "team") Team team) {
        new PromoteMembersMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam demote"}, permission = "foxtrot.manage")
    public static void demoteTeam(Player sender, @Param(name = "team") Team team) {
        new DemoteMembersMenu(team).openMenu(sender);
    }


    @Command(names = {"manageteam kick"}, permission = "foxtrot.manage")
    public static void kickTeam(Player sender, @Param(name = "team") Team team) {
        new KickPlayersMenu(team).openMenu(sender);
    }


    @Command(names = {"manageteam balance"}, permission = "foxtrot.manage")
    public static void balanceTeam(Player sender, @Param(name = "team") Team team) {
        if (sender.hasPermission("foxtrot.manage.setbalance")) {
            conversationDouble(sender, "§bEnter a new balance for " + team.getName() + ".", (d) -> {
                SetTeamBalanceCommand.setTeamBalance(sender, team, d.floatValue());
                sender.sendRawMessage(ChatColor.GRAY + team.getName() + " now has a balance of " + team.getBalance());
            });
        }
    }

    @Command(names = {"manageteam dtr"}, permission = "foxtrot.manage")
    public static void dtrTeam(Player sender, @Param(name = "team") Team team) {
        if (sender.hasPermission("foxtrot.manage.setdtr")) {
            conversationDouble(sender, "§eEnter a new DTR for " + team.getName() + ".", (d) -> {
                team.setDTR(d.floatValue());
                sender.sendRawMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + d.floatValue() + ChatColor.YELLOW + ".");
            });
        } else {
            new DTRMenu(team).openMenu(sender);
        }
    }

    @Command(names = {"manageteam points"}, permission = "foxtrot.manage")
    public static void pointsTeam (Player sender, @Param(name = "team") Team team) {
        if (sender.hasPermission("foxtrot.manage.points")) {
            conversationDouble(sender, "§eEnter a new Points amount for " + team.getName() + ".", (d) -> {
                team.setPoints(d.intValue());
                sender.sendRawMessage(CC.translate("&eYou have set &9" + team.getName() + "&e's points to &9" + d.intValue()));
            });
        }
    }

    @Command(names = {"manageteam rename"}, permission = "foxtrot.manage")
    public static void renameTeam(Player sender, @Param(name = "team") Team team) {
        conversationString(sender, "§eEnter a new name for " + team.getName() + ".", (name) -> {
            String oldName = team.getName();
            team.rename(name);
            sender.sendRawMessage(CC.translate("&7" + oldName + " &enow has a name of &9" + team.getName()));
        });
    }


    @Command(names = {"manageteam mute"}, permission = "foxtrot.manage")
    public static void muteTeam(Player sender, @Param(name = "team") Team team) {
        new MuteMenu(team).openMenu(sender);

    }


    @Command(names = {"manageteam manage"}, permission = "foxtrot.manage")
    public static void manageTeam(Player sender, @Param(name = "team") Team team) {
        new TeamManageMenu(team).openMenu(sender);
    }

    private static void conversationDouble(Player p, String prompt, Callback<Double> callback) {
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
			public String getPromptText(ConversationContext context) {
                return prompt;
            }

            
            @Override
			public Prompt acceptInput(ConversationContext cc, String s) {
                try {
                    callback.callback(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }

    private static void conversationString(Player p, String prompt, Callback<String> callback) {
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
			public String getPromptText(ConversationContext context) {
                return prompt;
            }

            
            @Override
			public Prompt acceptInput(ConversationContext cc, String newName) {

                if (newName.length() > 16) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (newName.length() < 3) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
                    if (HCF.getInstance().getTeamHandler().getTeam(newName) == null) {
                        callback.callback(newName);
                        return Prompt.END_OF_CONVERSATION;

                    } else {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "A team with that name already exists!");
                    }
                } else {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Team names must be alphanumeric!");
                }


                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }
}
