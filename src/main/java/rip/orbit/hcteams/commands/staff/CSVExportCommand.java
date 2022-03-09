package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class CSVExportCommand {

    @Command(names={ "csvexport" }, permission="op")
    public static void csvExport(Player sender) {
        try (FileWriter fileWriter = new FileWriter("export.csv")) {
            fileWriter.append("Name,HasTeam,TeamBalance,TeamSize,CoalMined,DiamondMined,EmeraldMined,FishingKitUses,FriendLives,GoldMined,IronMined,Kills,LapisMined,Playtime,RedstoneMined,SoulboundLives,Balance,Whitelisted,OP").append('\n');

            for (UUID player : HCF.getInstance().getFirstJoinMap().getAllPlayers()) {
                Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(player);
                OfflinePlayer offlinePlayer = HCF.getInstance().getServer().getOfflinePlayer(player);

                fileWriter.append(FrozenUUIDCache.name(player)).append(",");
                fileWriter.append(playerTeam != null ? "1" : "0").append(",");
                fileWriter.append(String.valueOf(playerTeam == null ? 0 : playerTeam.getBalance())).append(",");
                fileWriter.append(String.valueOf(playerTeam == null ? 0 : playerTeam.getSize())).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getCoalMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getDiamondMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getEmeraldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getFishingKitMap().getUses(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getLivesMap().getLives(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getGoldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getIronMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getLapisMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getPlaytimeMap().getPlaytime(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getRedstoneMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCF.getInstance().getWrappedBalanceMap().getBalance(player))).append(",");
                fileWriter.append(offlinePlayer.isWhitelisted() ? "1" : "0").append(",");
                fileWriter.append(offlinePlayer.isOp() ? "1" : "0").append('\n');

                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage("Done!");
    }

}