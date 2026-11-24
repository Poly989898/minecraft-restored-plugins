package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public abstract class PurgePlayerPermissionDialog extends UserInput {
   private final Player p;
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private static final Phrase TITLE = Phrase.create("purgePlayerPermissionDialog.title", "Purge Player Permission", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create("purgePlayerPermissionDialog.subtitle", "Type in a Permission via Chat", Colors.GRAY);
   private static final Phrase CONFIRM = Phrase.create(
      "purgePlayerPermissionDialog.confirm", "Successfully **purged** **%permission%** from all Players", Colors.GRAY, Colors.RED, Colors.GREEN
   );

   public PurgePlayerPermissionDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, TITLE.get(), SUBTITLE);
      this.p = player;
      this.holder = permissionHolder;
      this.plugin = ultraPermissions;
   }

   @Override
   public boolean onResult(String text) {
      String permissionName = text.replace(" ", ".");

      for (User user : this.plugin.getUsers()) {
         PermissionList name = user.getPermissions().name(permissionName);
         name.forEach(Permission::remove);
      }

      this.p.sendMessage(this.plugin.getPrefix() + CONFIRM.get().replace("%permission%", permissionName));
      this.reopen();
      return true;
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }

   public abstract void reopen();
}
