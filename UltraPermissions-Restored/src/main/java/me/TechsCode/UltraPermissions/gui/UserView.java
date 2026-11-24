package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.dialogs.PrefixChangeDialog;
import me.TechsCode.UltraPermissions.gui.dialogs.SuffixChangeDialog;
import me.TechsCode.UltraPermissions.gui.permissionEditor.PermissionCopyList;
import me.TechsCode.UltraPermissions.gui.permissionEditor.PermissionEditorMainView;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public abstract class UserView extends GUI {
   private static final Phrase TITLE = Phrase.create("userView.title", "User > %user%");
   private static final Phrase GROUPS_TITLE = Phrase.create("userView.groups.title", "Groups");
   private static final Phrase GROUPS_ACTION = Phrase.create("userView.groups.action", "**Click** to view Groups", Colors.GRAY, Colors.AQUA);
   private static final Phrase GROUPS_LIST_TITLE = Phrase.create("userView.groups.list.title", "Groups:", Colors.GRAY);
   private static final Phrase PERMISSIONS_TITLE = Phrase.create("userView.permissions.title", "View Permissions");
   private static final Phrase PERMISSIONS_ACTION = Phrase.create("userView.permissions.action", "**Click** to view Permissions", Colors.GRAY, Colors.AQUA);
   private static final Phrase PERMISSIONS_LIST_TITLE = Phrase.create("userView.permissions.list.title", "Permission Nodes:", Colors.GRAY);
   private final UltraPermissions plugin;
   private final User user;
   private final long opened;
   private static final Phrase PREFIX_SUFFIX_TITLE = Phrase.create("userView.prefixSuffix.title", "Prefix and Suffix");
   private static final Phrase PREFIX_SUFFIX_PREFIX_ACTION = Phrase.create(
      "userView.prefixSuffix.prefixAction", "**Left Click** to edit prefix", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase PREFIX_SUFFIX_SUFFIX_ACTION = Phrase.create(
      "userView.prefixSuffix.suffixAction", "**Right Click** to edit suffix", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase PREFIX_SUFFIX_PREFIX_ATTRIBUTE = Phrase.create(
      "userView.prefixSuffix.prefixAttribute", "Prefix: **%prefix%**", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase PREFIX_SUFFIX_SUFFIX_ATTRIBUTE = Phrase.create(
      "userView.prefixSuffix.suffixAttribute", "Suffix: **%suffix%**", Colors.GRAY, Colors.WHITE
   );

   public UserView(Player player, UltraPermissions ultraPermissions, User user) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.user = user;
      this.opened = System.currentTimeMillis();
   }

   public boolean hasBackButton() {
      return true;
   }

   public abstract void onBack();

   @Override
   public int getCurrentSlots() {
      return 54;
   }

   @Override
   public String getCurrentTitle() {
      return TITLE.get().replace("%user%", this.user.getName());
   }

   @Override
   protected void construct(Model model) {
      model.button(this::GroupsButton, 20);
      model.button(this::ViewPermissionsButton, 14);
      model.button(this::PrefixSuffixButton, 26);
      if (this.hasBackButton()) {
         model.button(50, button -> Common.BackButton(button, actionType -> this.onBack()));
      }
   }

   private void GroupsButton(Button button) {
      button.material(XMaterial.BOOKSHELF).name(Animation.wave(GROUPS_TITLE.get(), Colors.DeepSkyBlue, Colors.AliceBlue)).lore(GROUPS_ACTION.get(), "");
      button.item().appendLore(GROUPS_LIST_TITLE.get());
      button.item().appendLore(LoreLists.printRankUpLoreList(this.user.getRankups()));
      button.action(actionType -> new UserGroupListView(this.p, this.plugin, this.user) {
         @Override
         public void onBack() {
            UserView.this.reopen();
         }
      });
   }

   private void ViewPermissionsButton(Button button) {
      button.material(XMaterial.WRITABLE_BOOK)
         .name(Animation.wave(PERMISSIONS_TITLE.get(), Colors.DeepSkyBlue, Colors.AliceBlue))
         .lore(PERMISSIONS_ACTION.get(), "", PERMISSIONS_LIST_TITLE.get());
      PermissionCopyList list = new PermissionCopyList(this.user.getPermissions(), this.user.getAdditionalPermissions());
      button.item().appendLore(LoreLists.printPermissionCopiesList(list, this.opened));
      button.action(actionType -> new PermissionEditorMainView(this.p, this.plugin, this.user) {
         @Override
         public void onBack() {
            UserView.this.reopen();
         }
      });
   }

   private void PrefixSuffixButton(Button button) {
      String text = this.user.getPrefix().orElse(Colors.RED + Words.NONE.get());
      String suffixText = this.user.getSuffix().orElse(Colors.RED + Words.NONE.get());
      button.material(XMaterial.NAME_TAG)
         .name(Animation.wave(PREFIX_SUFFIX_TITLE.get(), Colors.DeepSkyBlue, Colors.AliceBlue))
         .lore(
            PREFIX_SUFFIX_PREFIX_ACTION.get(),
            PREFIX_SUFFIX_SUFFIX_ACTION.get(),
            "",
            PREFIX_SUFFIX_PREFIX_ATTRIBUTE.get().replace("%prefix%", text),
            PREFIX_SUFFIX_SUFFIX_ATTRIBUTE.get().replace("%suffix%", suffixText)
         );
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new PrefixChangeDialog(this.p, this.plugin, this.user) {
               @Override
               public void reopen() {
                  UserView.this.reopen();
               }
            };
         }
      });
      button.action(actionType -> {
         if (actionType == ActionType.RIGHT) {
            new SuffixChangeDialog(this.p, this.plugin, this.user) {
               @Override
               public void reopen() {
                  UserView.this.reopen();
               }
            };
         }
      });
   }
}
