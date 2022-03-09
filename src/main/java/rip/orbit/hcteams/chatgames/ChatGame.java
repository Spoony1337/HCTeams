package rip.orbit.hcteams.chatgames;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import rip.orbit.hcteams.HCF;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:36 PM
 * HCTeams / rip.orbit.hcteams.chatgames
 */
public abstract class ChatGame implements Listener {

	public boolean started;

	public ChatGame() {
		this.started = false;
		Bukkit.getPluginManager().registerEvents(this, HCF.getInstance());
	}

	public abstract String name();
	public abstract void start();
	public abstract void end();

}
