package me.TechsCode.UltraPermissions.gui.dialogs;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class CreateGroupDialog extends UserInput {
   private static final Phrase TITLE = Phrase.create("createGroupDialog.title", "Create Group");
   private static final Phrase SUB_TITLE = Phrase.create("createGroupDialog.subTitle", "Type in a group name", Colors.GRAY);
   private static final Phrase NO_SPACES = Phrase.create("createGroupDialog.noSpaces", "The Group Name **should not** contain spaces", Colors.GRAY, Colors.RED);
   private static final Phrase NO_COLORS = Phrase.create(
      "createGroupDialog.noColors", "The Group Name **should not** contain Color Codes", Colors.GRAY, Colors.RED
   );
   private static final Phrase NAME_TAKEN = Phrase.create("createGroupDialog.nameTaken", "This Group Name is already in use", Colors.GRAY);
   private final Player p;
   private final UltraPermissions plugin;

   public CreateGroupDialog(Player player, UltraPermissions plugin) {
      super(player, plugin, TITLE.get(), SUB_TITLE.get());
      this.p = player;
      this.plugin = plugin;
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
         boolean present = this.plugin.getGroups().name((String)text).isPresent();
         if (present) {
            this.p.sendMessage(this.plugin.getPrefix() + NAME_TAKEN);
            return false;
         } else {
            this.plugin.newGroup((String)text).setPrefix(Colors.GREEN + text).create();
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
