package io.lumine.mythic.core.menus.items.editor.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import org.bukkit.Material;

public class ConfigToggleButton extends ComponentMenuButton<ItemComponentEditorMenuContext> {
   private final Material material;
   private final String name;
   private final String configPath;
   private final boolean defaultValue;

   public ConfigToggleButton(int sortIndex, Material material, String name, String configPath, boolean defaultValue) {
      super(sortIndex);
      this.material = material;
      this.name = name;
      this.configPath = configPath;
      this.defaultValue = defaultValue;
   }

   @Override
   public Icon<ItemComponentEditorMenuContext> getIcon() {
      return IconBuilder.<ItemComponentEditorMenuContext>create()
         .material(this.material)
         .name(this.name)
         .lore(
            context -> Lists.newArrayList(
               new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getConfig().getBoolean(this.configPath, this.defaultValue)}
            )
         )
         .click((context, player) -> {
            MythicItem item = context.getItem();
            boolean current = item.getConfig().getBoolean(this.configPath, this.defaultValue);
            item.getConfig().setSave(this.configPath, !current);
            item.loadItem();
            item.buildItemCache();
            context.openMenu(player);
         })
         .build();
   }
}
