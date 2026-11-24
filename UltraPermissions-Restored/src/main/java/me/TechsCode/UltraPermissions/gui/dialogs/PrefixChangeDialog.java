package me.TechsCode.UltraPermissions.gui.dialogs;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class PrefixChangeDialog extends UserInput {
   private static final Phrase MAIN_TITLE = Phrase.create("prefixDialog.title", "Edit Prefix", Colors.AQUA);
   private static final Phrase SUB_TITLE = Phrase.create("prefixDialog.subtitle", "Type in a new prefix for **%holder%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase ACTION_BAR = Phrase.create("prefixDialog.actionBar", "Type in **none** to clear the prefix", Colors.GRAY, Colors.RED);
   private final PermissionHolder holder;

   public PrefixChangeDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, MAIN_TITLE.get(), SUB_TITLE.get().replace("%holder%", permissionHolder.getName()), ACTION_BAR.get());
      this.holder = permissionHolder;
   }

   public abstract void reopen();

   @Override
   public void onClose(Player player) {
      this.reopen();
   }

   @Override
   public boolean onResult(String text) {
      this.holder.setPrefix(text.equalsIgnoreCase("none") ? null : text);
      this.reopen();
      return true;
   }
}
