package me.TechsCode.UltraPermissions.gui.dialogs;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GroupRenameDialog extends UserInput {
   private static final Phrase TITLE = Phrase.create("groupRenameDialog.title", "Rename Group");
   private static final Phrase SUB_TITLE = Phrase.create("groupRenameDialog.subtitle", "Type in a new name for **%group%**", Colors.GRAY, Colors.YELLOW);
   private final Group group;

   public GroupRenameDialog(Player player, UltraPermissions ultraPermissions, Group group) {
      super(player, ultraPermissions, TITLE.get(), SUB_TITLE.get().replace("%group%", group.getName()));
      this.group = group;
   }

   public abstract void reopen();

   @Override
   public boolean onResult(String text) {
      this.group.setName(text.replace(" ", ""));
      this.reopen();
      return true;
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }
}
