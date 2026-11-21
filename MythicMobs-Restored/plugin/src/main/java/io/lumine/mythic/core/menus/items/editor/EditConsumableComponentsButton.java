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

public class EditConsumableComponentsButton extends ItemEditorButton {
   public EditConsumableComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.POTION).name("<green>Consumable").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("Consumable");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("Consumable.ConsumeSeconds")) {
            lore.add("<yellow>ConsumeSeconds: <white>" + config.getString("Consumable.ConsumeSeconds", "<none>"));
         }

         if (config.isSet("Consumable.HasParticles")) {
            lore.add("<yellow>HasParticles: <white>" + config.getBoolean("Consumable.HasParticles", true));
         }

         if (config.isSet("Consumable.Animation")) {
            lore.add("<yellow>Animation: <white>" + config.getString("Consumable.Animation", "<none>"));
         }

         if (config.isSet("Consumable.Sound")) {
            lore.add("<yellow>Sound: <white>" + config.getString("Consumable.Sound", "<none>"));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getConsumableComponentMenu().open(player, componentContext);
      }).build();
   }
}
