package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class AddPermissionDialog extends UserInput {
   private final Player p;
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private static final Phrase TITLE = Phrase.create("addPermissionDialog.title", "Add Permission", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create("addPermissionDialog.subtitle", "Type in a Permission via Chat", Colors.GRAY);

   public AddPermissionDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, TITLE.get(), SUBTITLE);
      this.p = player;
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
   }

   @Override
   public boolean onResult(String text) {
      String permission = text.replace(" ", ".");
      this.holder.newPermission(permission).create();
      new PermissionView(this.p, this.plugin, this.holder, new PermissionWithInfo(permission, this.plugin.getPermissionDatabase().getInfo(permission))) {
         @Override
         public void onBack() {
            new FullPermissionListView(this.p, AddPermissionDialog.this.plugin, AddPermissionDialog.this.holder) {
               @Override
               public void onBack() {
                  AddPermissionDialog.this.reopen();
               }
            };
         }
      };
      return true;
   }

   @Override
   public void onClose(Player closingPlayer) {
      this.reopen();
   }

   public abstract void reopen();
}
