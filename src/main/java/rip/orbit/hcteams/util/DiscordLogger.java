package rip.orbit.hcteams.util;

import org.bukkit.Bukkit;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.object.Webhook;

import java.awt.*;
import java.util.UUID;

public class DiscordLogger {

    private rip.orbit.hcteams.HCF HCF;
    public DiscordLogger(HCF HCF) { this.HCF = HCF; }

    public void logRefund(String refunded, String refunder, String reason){
        UUID uuid = Bukkit.getPlayer(refunded).getUniqueId();

        Webhook webhook = new Webhook("https://discord.com/api/webhooks/859834498569076776/2k8_8b9XRRA8OcjlFgxJ0eb4z1aCAB4TVkd9dp8qC1hgcgoEd9z5soM6eUVfaqDsu6bN");
        webhook.addEmbed(new Webhook.EmbedObject()
                .setAuthor("Refund", null, null)
                .setColor(Color.RED)
                .addField("Player Refunded", refunded, false)
                .addField("Refunded by", refunder, false)
                .addField("Reason", reason, true)

        );
    }
}