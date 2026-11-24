package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public abstract class PlaceholderFillDialog {
   private final Player p;
   private final UltraPermissions plugin;
   private String permission;
   private String[] placeholders;
   private int internalCounter = 0;
   private static final Phrase SUB_TITLE = Phrase.create(
      "placeholderFillDialog.subtitle", "Type in a value for **[%placeholder%]**", Colors.GRAY, Colors.YELLOW
   );

   public PlaceholderFillDialog(Player player, UltraPermissions ultraPermissions, String text) {
      this.p = player;
      this.plugin = ultraPermissions;
      this.permission = text;
      this.placeholders = StringUtils.substringsBetween(text, "[", "]");
      this.run();
   }

   public abstract void onBack();

   public void run() {
      if (this.placeholders == null) {
         this.onComplete(this.permission);
      } else {
         for (final String placeholder : this.placeholders) {
            new UserInput(this.p, this.plugin, this.permission, SUB_TITLE.get().replace("%placeholder%", placeholder)) {
               @Override
               public void onClose(Player closingPlayer) {
                  PlaceholderFillDialog.this.onBack();
               }

               @Override
               public boolean onResult(String text) {
                  PlaceholderFillDialog.this.permission = PlaceholderFillDialog.this.permission.replace("[" + placeholder + "]", text);
                  PlaceholderFillDialog.this.internalCounter++;
                  if (PlaceholderFillDialog.this.internalCounter == PlaceholderFillDialog.this.placeholders.length) {
                     PlaceholderFillDialog.this.onComplete(PlaceholderFillDialog.this.permission);
                  }

                  return true;
               }
            };
         }
      }
   }

   public abstract void onComplete(String text);
}
