package me.TechsCode.UltraPermissions.gui.dialogs;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.views.ConfirmationView;
import me.TechsCode.UltraPermissions.gui.GroupListView;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GroupDeleteView extends ConfirmationView {
   private static final Phrase TITLE = Phrase.create("groupDeleteView.title", "Confirm deletion of %group%");
   private final UltraPermissions plugin;
   private final Group group;

   public GroupDeleteView(Player player, UltraPermissions ultraPermissions, Group group) {
      super(player, ultraPermissions, TITLE.get().replace("%group%", group.getName()));
      this.plugin = ultraPermissions;
      this.group = group;
   }

   @Override
   public void choose(boolean flag) {
      if (flag) {
         this.group.remove();
         new GroupListView(this.p, this.plugin) {
            @Override
            public void onBack() {
               GroupDeleteView.this.reopen();
            }
         };
      } else {
         this.reopen();
      }
   }

   @Override
   public abstract void reopen();
}
