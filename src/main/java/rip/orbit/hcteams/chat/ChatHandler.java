package rip.orbit.hcteams.chat;

import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.chat.listeners.ChatListener;

import java.util.concurrent.atomic.AtomicInteger;

public class ChatHandler {

    private static AtomicInteger publicMessagesSent = new AtomicInteger();

    public ChatHandler() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), HCF.getInstance());
    }

    public static AtomicInteger getPublicMessagesSent() {
        return publicMessagesSent;
    }
}