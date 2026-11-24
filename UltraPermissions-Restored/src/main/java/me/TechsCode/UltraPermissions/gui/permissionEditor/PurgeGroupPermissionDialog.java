package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class PurgeGroupPermissionDialog extends UserInput {
   private final Player p;
   private final UltraPermissions plugin;
   private PermissionHolder holder;
   private static final Phrase TITLE = Phrase.create("purgeGroupPermissionDialog.title", "Purge Group Permission", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create("purgeGroupPermissionDialog.subtitle", "Type in a Permission via Chat", Colors.GRAY);
   private static final Phrase CONFIRM = Phrase.create(
      "purgeGroupPermissionDialog.confirm", "Successfully **purged** **%permission%** from all Groups", Colors.GRAY, Colors.RED, Colors.GREEN
   );

   public PurgeGroupPermissionDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, TITLE.get(), SUBTITLE);
      this.p = player;
      this.holder = permissionHolder;
      this.plugin = ultraPermissions;
   }

   @Override
   public boolean onResult(String text) {
      String permission = text.replace(" ", ".");

      for (Group group : this.plugin.getGroups()) {
         PermissionList name = group.getPermissions().name(permission);
         name.forEach(Permission::remove);
      }

      this.p.sendMessage(this.plugin.getPrefix() + CONFIRM.get().replace("%permission%", permission));
      this.reopen();
      return true;
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }

   public abstract void reopen();
}
