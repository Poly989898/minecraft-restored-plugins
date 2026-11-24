package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class AddBungeePermissionDialog extends UserInput {
   private final Player p;
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private static final Phrase TITLE = Phrase.create("addBungeePermissionDialog.title", "Add Bungee Permission", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create("addBungeePermissionDialog.subtitle", "Type in a Permission via Chat", Colors.GRAY);

   public AddBungeePermissionDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, TITLE, SUBTITLE);
      this.p = player;
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
   }

   @Override
   public boolean onResult(String text) {
      String permissionNode = text.replace(" ", ".");
      this.holder.newPermission(permissionNode).setServer("BungeeCord").create();
      new FullPermissionListView(this.p, this.plugin, this.holder) {
         @Override
         public void onBack() {
            AddBungeePermissionDialog.this.reopen();
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
