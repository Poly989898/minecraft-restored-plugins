package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.ArrayList;
import org.bukkit.Material;

public class EditUseCooldownComponentsButton extends ItemEditorButton {
   public EditUseCooldownComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.CLOCK).name("<green>Use Cooldown").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("UseCooldown");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("UseCooldown.CooldownGroup")) {
            lore.add("<yellow>CooldownGroup: <white>" + config.getString("UseCooldown.CooldownGroup", "<none>"));
         }

         if (config.isSet("UseCooldown.CooldownSeconds")) {
            lore.add("<yellow>CooldownSeconds: <white>" + config.getString("UseCooldown.CooldownSeconds", "<none>"));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getUseCooldownComponentMenu().open(player, componentContext);
      }).build();
   }
}
