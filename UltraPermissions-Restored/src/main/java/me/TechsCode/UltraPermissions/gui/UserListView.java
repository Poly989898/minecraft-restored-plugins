package me.TechsCode.UltraPermissions.gui;

import java.util.Arrays;
import java.util.Optional;
import me.TechsCode.UltraPermissions.CachedOnlineChecker;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.SkinTexture;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public abstract class UserListView extends PageableGUI<User> {
   private static final Phrase TITLE = Phrase.create("userListView.title", "Overview > Users");
   private static final Phrase USER_ACTION = Phrase.create("userListView.user.action", "**Click** to view", Colors.GRAY, Colors.AQUA);
   private static final Phrase USER_DELETE_ACTION = Phrase.create(
      "userListView.user.deleteAction", "**Press Q** to **delete**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase USER_SUPER_ADMIN_INDICATOR = Phrase.create("userListView.user.superAdminIndicator", "This user is a superadmin", Colors.GREEN);
   private static final Phrase USER_PREFIX_ATTRIBUTE = Phrase.create("userListView.user.prefixAttribute", "Prefix: **%prefix%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase USER_SUFFIX_ATTRIBUTE = Phrase.create("userListView.user.suffixAttribute", "Suffix: **%suffix%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase USER_RANK_UP_LIST_TITLE = Phrase.create("userListView.rankUpList.title", "Groups:", Colors.GRAY);
   private static final Phrase USER_DESC_NO_HEAD = Phrase.create("userListView.user.noHeadDesc", "Head will be shown after the next connect", Colors.DARK_GRAY);
   private static final Phrase SEARCH_TITLE = Phrase.create("userListView.search.title", "Search User");
   private static final Phrase SEARCH_ACTION = Phrase.create("userListView.search.action", "Click to type in a name", Colors.GRAY);
   private static final Phrase FILTER_TITLE = Phrase.create("userListView.onlineFilter.title", "Visibility");
   private static final Phrase FILTER_ENABLE_ACTION = Phrase.create(
      "userListView.onlineFilter.enableAction", "Click to **hide** Offline Users", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase FILTER_DISABLE_ACTION = Phrase.create(
      "userListView.onlineFilter.disableAction", "Click to **show** Offline Users", Colors.GRAY, Colors.YELLOW
   );
   private final UltraPermissions plugin;
   private final CachedOnlineChecker onlineChecker;
   public boolean onlineOnly;

   public UserListView(Player player, UltraPermissions plugin) {
      super(player, plugin);
      this.plugin = plugin;
      this.onlineChecker = CachedOnlineChecker.scan();
      this.onlineOnly = true;
   }

   @Override
   public String getTitle() {
      return TITLE.get();
   }

   public User[] getUsers() {
      return this.plugin.getUsers().toArray(new User[0]);
   }

   public User[] getObjects() {
      return Arrays.stream(this.getUsers()).filter(user -> !this.onlineOnly || this.onlineChecker.isOnline(user.getUuid())).toArray(User[]::new);
   }

   public void construct(Button button, User user) {
      Optional optional = user.getSkinTexture();
      if (optional.isPresent()) {
         button.material(XMaterial.PLAYER_HEAD).setSkullTexture((SkinTexture)optional.get());
      } else {
         button.material(XMaterial.GRAY_STAINED_GLASS);
      }

      button.item().name(Animation.wave(user.getName(), Colors.GOLD, Colors.YELLOW, Colors.WHITE)).appendLore(USER_ACTION.get());
      if (!this.onlineChecker.isOnline(user.getUuid())) {
         button.item().appendLore(USER_DELETE_ACTION.get());
         button.action(actionType -> {
            if (actionType == ActionType.Q) {
               user.remove();
            }
         });
      }

      if (user.isSuperadmin()) {
         button.item().appendLore("", USER_SUPER_ADMIN_INDICATOR.get());
      }

      String text = user.getPrefix().orElse(Colors.RED + Words.NONE.firstUpper());
      String suffix = user.getSuffix().orElse(Colors.RED + Words.NONE.firstUpper());
      button.item().appendLore("", USER_PREFIX_ATTRIBUTE.get().replace("%prefix%", text), USER_SUFFIX_ATTRIBUTE.get().replace("%suffix%", suffix), "");
      button.item().appendLore(USER_RANK_UP_LIST_TITLE.get());
      button.item().appendLore(LoreLists.printRankUpLoreList(user.getRankups()));
      if (!optional.isPresent()) {
         button.item().appendLore("", USER_DESC_NO_HEAD.get());
      }

      button.action(clickAction -> new UserView(this.p, this.plugin, user) {
         @Override
         public void onBack() {
            UserListView.this.reopen();
         }
      });
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(
         this.getRightOptionSlot(),
         button -> {
            button.material(this.onlineOnly ? XMaterial.ENDER_EYE : XMaterial.ENDER_PEARL)
               .name(Animation.wave(FILTER_TITLE.get(), Colors.AQUA, Colors.WHITE))
               .lore((this.onlineOnly ? FILTER_DISABLE_ACTION : FILTER_ENABLE_ACTION).get());
            button.action(actionType -> this.onlineOnly = !this.onlineOnly);
         }
      );
   }

   @Override
   public int getCurrentSlots() {
      return 54;
   }

   @Override
   public SearchFeature<User> getSearch() {
      return new SearchFeature<User>(XMaterial.NAME_TAG, SEARCH_TITLE.get(), SEARCH_ACTION.get()) {
         public String[] getSearchableText(User user) {
            return new String[]{user.getName()};
         }
      };
   }
}
