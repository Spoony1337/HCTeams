package rip.orbit.hcteams.nametag;

import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.pvpclasses.pvpclasses.ArcherClass;
import rip.orbit.hcteams.team.Team;


public class FoxtrotNametagProvider extends NametagProvider {

    public FoxtrotNametagProvider() {
        super("Foxtrot Provider", 5);
    }


    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        Team viewerTeam =  HCF.getInstance().getTeamHandler().getTeam(refreshFor);
        NametagInfo nametagInfo = null;
        boolean isTeammate = false;
        boolean isArcherTagged = false;
        if (viewerTeam != null) {
            if (viewerTeam.isMember(toRefresh.getUniqueId())) {
                nametagInfo = FoxtrotNametagProvider.createNametag(HCF.getInstance().getTeamColorMap().getColor(refreshFor.getUniqueId()).toString(), "");
                isTeammate = true;
            }
            else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
                nametagInfo = FoxtrotNametagProvider.createNametag(HCF.getInstance().getAllyColorMap().getColor(refreshFor.getUniqueId()).toString(), "");
                isTeammate = true;
            }
            else if (viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(toRefresh.getUniqueId()) && !toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                nametagInfo = FoxtrotNametagProvider.createNametag(HCF.getInstance().getFocusColorMap().getColor(refreshFor.getUniqueId()).toString(), "");
            }
        }
        if (nametagInfo == null && toRefresh.hasMetadata("modmode")) {
            nametagInfo = FoxtrotNametagProvider.createNametag(ChatColor.GREEN + "[H] ", "");
        }
        if (nametagInfo == null && ArcherClass.isMarked(toRefresh)) {
            nametagInfo = FoxtrotNametagProvider.createNametag(HCF.getInstance().getArcherTagColorMap().getColor(refreshFor.getUniqueId()).toString(), "");
            isArcherTagged = true;
        }
        if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY) && refreshFor != toRefresh && !isArcherTagged && !isTeammate) {
            if (viewerTeam == null)
                nametagInfo = createNametag("invis", "");
            if (viewerTeam != null && !viewerTeam.isMember(toRefresh.getUniqueId()))
                nametagInfo = createNametag("invis", "");
        }
        return (nametagInfo == null) ? FoxtrotNametagProvider.createNametag(ChatColor.RED.toString(), "") : nametagInfo;
    }
}