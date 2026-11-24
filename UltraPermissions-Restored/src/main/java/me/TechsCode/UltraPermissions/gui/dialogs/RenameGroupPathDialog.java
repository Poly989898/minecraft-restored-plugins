package me.TechsCode.UltraPermissions.gui.dialogs;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import org.bukkit.entity.Player;

public abstract class RenameGroupPathDialog extends UserInput {
   private static final Phrase TITLE = Phrase.create("renameGroupPathDialog.title", "Rename Group Path");
   private static final Phrase SUB_TITLE = Phrase.create("renameGroupPathDialog.subTitle", "Type in a group path name", Colors.GRAY);
   private static final Phrase NO_SPACES = Phrase.create(
      "renameGroupPathDialog.noSpaces", "The Group Path Name **should not** contain spaces", Colors.GRAY, Colors.RED
   );
   private static final Phrase NO_COLORS = Phrase.create(
      "renameGroupPathDialog.noColors", "The Group Path Name **should not** contain Color Codes", Colors.GRAY, Colors.RED
   );
   private static final Phrase NAME_TAKEN = Phrase.create("renameGroupPathDialog.nameTaken", "This Group Path Name is already in use", Colors.GRAY);
   private final Player p;
   private final UltraPermissions plugin;
   private GroupPath groupPath;

   public RenameGroupPathDialog(Player player, UltraPermissions ultraPermissions, GroupPath groupPath) {
      super(player, ultraPermissions, TITLE.get(), SUB_TITLE.get());
      this.p = player;
      this.plugin = ultraPermissions;
      this.groupPath = groupPath;
   }

   @Override
   public boolean onResult(String text) {
      if (text.contains(" ")) {
         this.p.sendMessage(this.plugin.getPrefix() + NO_SPACES);
         return false;
      } else if (text.contains("&")) {
         this.p.sendMessage(this.plugin.getPrefix() + NO_COLORS);
         return false;
      } else {
         boolean isPresent = this.plugin.getGroupPaths().name(text).isPresent();
         if (isPresent) {
            this.p.sendMessage(this.plugin.getPrefix() + NAME_TAKEN);
            return false;
         } else {
            this.groupPath.setName(text);
            this.reopen();
            return true;
         }
      }
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }

   public abstract void reopen();
}
