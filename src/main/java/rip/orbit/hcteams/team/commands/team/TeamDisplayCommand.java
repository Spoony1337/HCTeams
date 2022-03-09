package rip.orbit.hcteams.team.commands.team;

public class TeamDisplayCommand {

//    @Command(names={ "team display", "t display", "f display", "faction display", "fac display" }, permission="")
//    public static void teamDisplay(Player sender, @Param(name="team") Team team) {
//        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
//            sender.sendMessage(ChatColor.RED + "You can not do this while you are deathbanned, come back later!");
//            return;
//        }
//
//        Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);
//
//        if (senderTeam == null) {
//            sender.sendMessage(ChatColor.RED + "You need to be in a team to do this.");
//            return;
//        }
//
//        if (!senderTeam.isOwner(sender.getUniqueId()) && !senderTeam.isCoLeader(sender.getUniqueId()) && !senderTeam.hasDisplayPermission(sender.getUniqueId())) {
//            sender.sendMessage(ChatColor.DARK_AQUA + "You lack permissions for that, ask your leader if this is an error.");
//            return;
//        }
//
////        if (senderTeam.equals(team)) {
////            sender.sendMessage(ChatColor.RED + "You cannot display your own team!");
////            return;
////        }
//
//        if (team.getHq() == null) {
//            sender.sendMessage(ChatColor.RED + "Sadly there is no HQ point set.");
//            return;
//        }
//
//        CBWaypoint waypoint = new CBWaypoint(team.getName(), team.getHq(), -16776961, true);
//        for (Player member : senderTeam.getOnlineMembers()) {
//            CheatBreakerAPI.getInstance().sendWaypoint(member, waypoint);
//        }
//
//        senderTeam.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has set the HQ point and has created a waypoint.");
//
//
//    }

}