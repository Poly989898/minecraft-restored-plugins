package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class SearchPermissionDialog extends UserInput {
   private static final Phrase TITLE = Phrase.create("searchPermissionDialog.title", "Search for Permission", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create(
      "searchPermissionDialog.subtitle", "Type in a **Search Term** in Chat to search", Colors.GRAY, Colors.YELLOW
   );
   private final Player p;
   private final UltraPermissions plugin;
   private final PermissionHolder holder;

   public SearchPermissionDialog(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, TITLE, SUBTITLE);
      this.p = player;
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
   }

   @Override
   public boolean onResult(String text) {
      new SearchPermissionListView(this.p, this.plugin, this.holder, text) {
         @Override
         public void onBack() {
            SearchPermissionDialog.this.reopen();
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
