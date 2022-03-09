package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class WipeDeathbansCommand {

    @Command(names={ "WipeDeathbans" }, permission="op")
    public static void wipeDeathbans(Player sender) {
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
			public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to wipe all deathbans? This action CANNOT be reversed. Type §byes§a to confirm or §cno§a to quit.";
            }

            
            @Override
			public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    HCF.getInstance().getDeathbanMap().wipeDeathbans();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Deathbans have been wiped.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Deathban wipe aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}