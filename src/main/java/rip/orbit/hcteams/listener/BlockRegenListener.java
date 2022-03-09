package rip.orbit.hcteams.listener;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.util.RegenUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockRegenListener implements Listener {

    private static Set<Material> REGEN = ImmutableSet.of(
            Material.COBBLESTONE,
            Material.STONE,
            Material.GRASS,
            Material.DIRT,
            Material.WOOD,
            Material.NETHERRACK,
            Material.LEAVES,
            Material.LEAVES_2
    );

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (HCF.getInstance().getAbilityHandler().byName("dome").getStack().isSimilar(event.getItemInHand())) {
            return;
        }

        if (event.isCancelled() || HCF.getInstance().getServerHandler().isAdminOverride(player)) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if ((team == null || !team.isMember(event.getPlayer().getUniqueId())) && (player.getItemInHand() != null && REGEN.contains(player.getItemInHand().getType()))) {
            RegenUtils.schedule(event.getBlock(), 35, TimeUnit.MINUTES, (block) -> {}, (block) -> {
                Team currentTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

                return !(currentTeam != null && currentTeam.isMember(player.getUniqueId()));
            });
        }
    }

}
