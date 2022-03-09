package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names={ "team", "t", "f", "faction", "fac" }, permission="")
    public static void team(Player sender) {
        sender.sendMessage(HELP);

    }

    private static String[] HELP = {

        "§7§m-----------------------------------------------------",
                "§6§lTeam Help §7- §fTeam Help",
                "§7§m-----------------------------------------------------",


                "§6General Commands:",
                "§f/t create <teamName> §7- Create a new team",
                "§f/t accept <teamName> §7- Accept a pending invitation",
                "§f/t lives add <amount> §7- Irreversibly add lives to your faction",
                "§f/t leave §7- Leave your current team",
                "§f/t home §7- Teleport to your team home",
                "§f/t stuck §7- Teleport out of enemy territory",
                "§f/t deposit <amount§7|§eall> §7- Deposit money into your team balance",


                "",
                "§6Information Commands:",
                "§f/t who [player§7|§eteamName] §7- Display team information",
                "§f/t map §7- Show nearby claims (identified by pillars)",
                "§f/t list §7- Show list of teams online (sorted by most online)",

                "",
                "§6Captain Commands:",
                "§f/t invite <player> §7- Invite a player to your team",
                "§f/t uninvite <player> §7- Revoke an invitation",
                "§f/t invites §7- List all open invitations",
                "§f/t kick <player> §7- Kick a player from your team",
                "§f/t claim §7- Start a claim for your team",
                "§f/t subclaim §7- Show the subclaim help page",
                "§f/t sethome §7- Set your team's home at your current location",
                "§f/t withdraw <amount> §7- Withdraw money from your team's balance",
                "§f/t announcement [message here] §7- Set your team's announcement",

                "",
                "§6Leader Commands:",

                "§f/t coleader <add|remove> <player> §7- Add or remove a co-leader",
                "§f/t captain <add|remove> <player> §7- Add or remove a captain",
                "§f/t revive <player> §7- Revive a teammate using team lives",
                "§f/t unclaim [all] §7- Unclaim land",
                "§f/t rename <newName> §7- Rename your team",
                "§f/t disband §7- Disband your team",


                "§7§m-----------------------------------------------------",



    };

}