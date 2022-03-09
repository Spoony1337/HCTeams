package rip.orbit.hcteams.events.koth.listeners;

import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.events.koth.events.EventControlTickEvent;
import rip.orbit.hcteams.util.CC;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (event.getKOTH().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = event.getKOTH();
        if (koth.getRemainingCapTime() % 180 == 0 && koth.getRemainingCapTime() <= (koth.getCapTime() - 30)) {
            HCF.getInstance().getServer().broadcastMessage(CC.translate("&b[KingOfTheHill] &eSomebody is attempting to control &9" + koth.getName() + "&e. &c(" + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + ")"));
        }
    }

}