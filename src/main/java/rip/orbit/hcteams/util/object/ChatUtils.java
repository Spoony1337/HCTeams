package rip.orbit.hcteams.util.object;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@NoArgsConstructor
public class ChatUtils {

    private static List<ChatColor> chatColor = new ArrayList<>();

    public static ChatColor randomChatColor() {
        return chatColor.get(new Random().nextInt(chatColor.size()));

    }

    public static String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    static {
        chatColor.add(ChatColor.BLUE);
        chatColor.add(ChatColor.AQUA);
        chatColor.add(ChatColor.DARK_AQUA);
        chatColor.add(ChatColor.WHITE);
        chatColor.add(ChatColor.LIGHT_PURPLE);
        chatColor.add(ChatColor.DARK_PURPLE);
        chatColor.add(ChatColor.GRAY);
        chatColor.add(ChatColor.DARK_GREEN);
        chatColor.add(ChatColor.DARK_RED);
        chatColor.add(ChatColor.RED);
        chatColor.add(ChatColor.GREEN);
        chatColor.add(ChatColor.YELLOW);
    }
}

