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

public class EditFoodComponentsButton extends ItemEditorButton {
   public EditFoodComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.APPLE).name("<green>Food").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("Food");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("Food.Nutrition")) {
            lore.add("<yellow>Nutrition: <white>" + config.getString("Food.Nutrition", "<none>"));
         }

         if (config.isSet("Food.Saturation")) {
            lore.add("<yellow>Saturation: <white>" + config.getString("Food.Saturation", "<none>"));
         }

         if (config.isSet("Food.EatSeconds")) {
            lore.add("<yellow>EatSeconds: <white>" + config.getString("Food.EatSeconds", "<none>"));
         }

         if (config.isSet("Food.CanAlwaysEat")) {
            lore.add("<yellow>CanAlwaysEat: <white>" + config.getBoolean("Food.CanAlwaysEat", false));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getFoodComponentMenu().open(player, componentContext);
      }).build();
   }
}
