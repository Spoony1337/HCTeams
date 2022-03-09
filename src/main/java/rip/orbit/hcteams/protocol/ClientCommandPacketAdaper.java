package rip.orbit.hcteams.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;

public class ClientCommandPacketAdaper extends PacketAdapter {

    public ClientCommandPacketAdaper() {
        super(HCF.getInstance(), PacketType.Play.Client.CLIENT_COMMAND);
    }

    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.PERFORM_RESPAWN) {
            if (!HCF.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
                return;
            }

            long unbannedOn = HCF.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();
            String time = TimeUtils.formatIntoDetailedString((int) left / 1000);
            event.setCancelled(true);

            new BukkitRunnable() {

                @Override
                public void run() {
                    event.getPlayer().setMetadata("loggedout", new FixedMetadataValue(HCF.getInstance(), true));

                    if (HCF.getInstance().getServerHandler().isPreEOTW()) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                    } else {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Come back in " + time + "!");
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                    }
                }

            }.runTask(HCF.getInstance());
        }
    }

}