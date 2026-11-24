package me.TechsCode.UltraPermissions.gui.dialogs;

import java.util.ArrayList;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class TogglePluginDialog extends UserInput {
   private UltraPermissions plugin;
   private ArrayList<String> toggledPlugins;
   private boolean isIn;
   private Player p;
   private static final Phrase TITLE = Phrase.create("togglePluginDialog.title", "Toggle Plugin");
   private static final Phrase SUB_TITLE = Phrase.create("togglePluginDialog.subtitle", "Type in the **Plugin Name**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase TOGGLED_EXCLUDE = Phrase.create(
      "togglePluginDialog.plugin.exclude", "**%PluginName%** is now excluded from the Permissions Log", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase TOGGLED_INCLUDE = Phrase.create(
      "togglePluginDialog.plugin.include", "**%PluginName%** is now included again in the Permissions Log", Colors.GRAY, Colors.YELLOW
   );

   public TogglePluginDialog(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions, TITLE.get(), SUB_TITLE.get());
      this.plugin = ultraPermissions;
      this.toggledPlugins = ultraPermissions.getToggledPluginsRegistry();
      this.p = player;
   }

   public abstract void reopen();

   @Override
   public boolean onResult(String text) {
      this.isIn = false;

      for (String registered : this.plugin.getToggledPluginsRegistry()) {
         if (registered.equalsIgnoreCase(text)) {
            text = registered;
            this.isIn = true;
         }
      }

      if (this.isIn) {
         this.p.sendMessage(this.plugin.getPrefix() + TOGGLED_INCLUDE.get().replace("%PluginName%", text));
         this.toggledPlugins.remove(text);
         this.plugin.setToggledPluginsRegistry(this.toggledPlugins);
         this.plugin.getToggledPluginsRegistry().remove(text);
         this.reopen();
         return true;
      } else {
         this.p.sendMessage(this.plugin.getPrefix() + TOGGLED_EXCLUDE.get().replace("%PluginName%", text));
         this.toggledPlugins.add(text);
         this.plugin.setToggledPluginsRegistry(this.toggledPlugins);
         this.plugin.getToggledPluginsRegistry().add(text);
         this.plugin.getPermissionLogger().clearManually(this.p, text);
         this.reopen();
         return true;
      }
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }
}
