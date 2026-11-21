package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import org.bukkit.Material;

public class EditGliderButton extends ItemEditorButton {
   public EditGliderButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.ELYTRA).name("<green>Glider").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         boolean enabled = config.isSet("Glider") || config.isSet("Options.Glider");
         return Lists.newArrayList(new String[]{"&c", "<yellow>Enabled: <white>" + enabled});
      }).click((context, player) -> {
         MythicItem item = context.getItem();
         MythicConfig config = item.getConfig();
         boolean enabled = config.isSet("Glider") || config.isSet("Options.Glider");
         if (enabled) {
            config.unsetSave("Glider");
            config.unsetSave("Options.Glider");
         } else {
            config.setSave("Glider", true);
         }

         item.loadItem();
         item.buildItemCache();
      }).build();
   }
}
