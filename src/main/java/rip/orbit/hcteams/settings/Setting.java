package rip.orbit.hcteams.settings;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.settings.menu.button.SettingButton;
import rip.orbit.hcteams.tab.TabListMode;

import java.util.Collection;

@AllArgsConstructor
public enum  Setting {

    SCOREBOARD_STAFF_BOARD(
            ChatColor.GOLD + "Staff Scoreboard",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "your staff scoreboard?"
            ),
            Material.BLAZE_ROD,
            ChatColor.WHITE + "Show scoreboard",
            ChatColor.WHITE + "Hide scoreboard",
            true
    ) {

        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getStaffBoardMap().isBoardToggled(player.getUniqueId());

            HCF.getInstance().getStaffBoardMap().setBoardToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see your staff scoreboard.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getStaffBoardMap().isBoardToggled(player.getUniqueId());
        }
    },

    GENERATOR(
            ChatColor.GOLD + "Generators",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "the messages when",
                    ChatColor.WHITE + "your generator produces",
                    ChatColor.WHITE + "an ability item?"
            ),
            Material.IRON_BLOCK,
            ChatColor.WHITE + "Show Generated Messages",
            ChatColor.WHITE + "Hide Generated Messages",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !HCF.getInstance().getReceiveGeneratorMessagesMap().isToggled(player.getUniqueId());

            HCF.getInstance().getReceiveGeneratorMessagesMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see generator produced messages.");
        }


        @Override
        public boolean isEnabled(Player player) {
            return HCF.getInstance().getReceiveGeneratorMessagesMap().isToggled(player.getUniqueId());
        }
    },

    TIPS(
            ChatColor.GOLD + "Tips",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "the tip announcements?"
            ),
            Material.SIGN,
            ChatColor.WHITE + "Show tips",
            ChatColor.WHITE + "Hide tips",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Profile.getByUuid(player.getUniqueId()).getOptions().isTipsEnabled();

            Profile.getByUuid(player.getUniqueId()).getOptions().setTipsEnabled(value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see tips.");
        }


        @Override
        public boolean isEnabled(Player player) {
            return Profile.getByUuid(player.getUniqueId()).getOptions().isTipsEnabled();
        }
    },

    ABILITY_CD(
            ChatColor.GOLD + "Ability Cooldowns",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "active ability cooldowns",
                    ChatColor.WHITE + "on your scoreboard?"
            ),
            Material.SLIME_BALL,
            ChatColor.WHITE + "Show ability cooldowns",
            ChatColor.WHITE + "Hide ability cooldowns",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !HCF.getInstance().getToggleAbilityCDsSBMap().isEnabled(player.getUniqueId());

            HCF.getInstance().getToggleAbilityCDsSBMap().setEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see ability cooldowns on the scoreboard.");
        }


        @Override
        public boolean isEnabled(Player player) {
            return HCF.getInstance().getToggleAbilityCDsSBMap().isEnabled(player.getUniqueId());
        }
    },

    PUBLIC_CHAT(
            ChatColor.GOLD + "Public Chat",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "public chat messages?"
            ),
            Material.BOOK,
            ChatColor.WHITE + "Show public chat",
            ChatColor.WHITE + "Hide public chat",
            true
    ) {


        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId());

            HCF.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see global chat messages.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId());
        }

    },

    TAB_LIST(
            ChatColor.GOLD + "Tab List Info",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "extra info on your",
                    ChatColor.WHITE + "tab list?"
            ),
            Material.ENCHANTED_BOOK,
            ChatColor.WHITE + "",
            ChatColor.WHITE + "",
            true
    ) {


        @Override
		public void toggle(Player player) {
            TabListMode mode = SettingButton.next(HCF.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId()));

            HCF.getInstance().getTabListModeMap().setTabListMode(player.getUniqueId(), mode);
            player.sendMessage(ChatColor.WHITE + "You've set your tab list mode to " + ChatColor.LIGHT_PURPLE + mode.getName() + ChatColor.WHITE + ".");
        }


        @Override
		public boolean isEnabled(Player player) {
            return true;
        }

    }, FOUND_DIAMONDS(
            ChatColor.GOLD + "Found Diamonds",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "found-diamonds messages?"
            ),
            Material.DIAMOND,
            ChatColor.WHITE + "Show messages",
            ChatColor.WHITE + "Hide messages",
            true
    ) {


        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());

            HCF.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see found diamond messages.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());
        }

    },

    DEATH_MESSAGES(
            ChatColor.GOLD + "Death Messages",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "death messages?"
            ),
            Material.SKULL_ITEM,
            ChatColor.WHITE + "Show messages",
            ChatColor.WHITE + "Hide messages",
            true
    ) {

        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());

            HCF.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.WHITE + " to see death messages.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());
        }
    },

    CLAIMONSB(
            ChatColor.GOLD + "Claim On Scoreboard",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want to see",
                    ChatColor.WHITE + "the claim you are",
                    ChatColor.WHITE + "in on your scoreboard?"
            ),
            Material.PAINTING,
            ChatColor.WHITE + "Show Claim",
            ChatColor.WHITE + "Hide Claim",
            true
    ) {

        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getClaimOnSbMap().isToggled(player.getUniqueId());

            HCF.getInstance().getClaimOnSbMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "Claims on Scoreboard will " + (value ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.WHITE + " be show.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getClaimOnSbMap().isToggled(player.getUniqueId());
        }
    },

    FACTION_INVITES(
            ChatColor.GOLD + "Faction Invites",
            ImmutableList.of(
                    ChatColor.WHITE + "Do you want others",
                    ChatColor.WHITE + "to be able to invite",
                    ChatColor.WHITE + "you to their faction?"
            ),
            Material.BEACON,
            ChatColor.WHITE + "Allow Invites",
            ChatColor.WHITE + "Don't Allow Invites",
            true
    ) {

        @Override
		public void toggle(Player player) {
            boolean value = !HCF.getInstance().getReceiveFactionInviteMap().isToggled(player.getUniqueId());

            HCF.getInstance().getReceiveFactionInviteMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.WHITE + "You will " + (value ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.WHITE + " be able to see faction invites.");
        }


        @Override
		public boolean isEnabled(Player player) {
            return HCF.getInstance().getReceiveFactionInviteMap().isToggled(player.getUniqueId());
        }
    };

    @Getter private String name;
    @Getter private Collection<String> description;
    @Getter private Material icon;
    @Getter private String enabledText;
    @Getter private String disabledText;
    private boolean defaultValue;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return (defaultValue);
    }

    public SettingButton toButton() {
        return new SettingButton(this);
    }

    public abstract void toggle(Player player);

    public abstract boolean isEnabled(Player player);

}
