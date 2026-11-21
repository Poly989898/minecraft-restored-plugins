package io.lumine.mythic.core.menus.items.editor.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import java.util.List;
import org.bukkit.Material;

public class ConfigDeleteComponentButton extends ComponentMenuButton<ItemComponentEditorMenuContext> {
   private final Material material;
   private final String name;
   private final List<String> keysToUnset;

   public ConfigDeleteComponentButton(int sortIndex, Material material, String name, List<String> keysToUnset) {
      super(sortIndex);
      this.material = material;
      this.name = name;
      this.keysToUnset = keysToUnset;
   }

   @Override
   public Icon<ItemComponentEditorMenuContext> getIcon() {
      return IconBuilder.<ItemComponentEditorMenuContext>create()
         .material(this.material)
         .name(this.name)
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Remove this component"}))
         .click((context, player) -> {
            MythicItem item = context.getItem();

            for (String key : this.keysToUnset) {
               item.getConfig().unsetSave(key);
            }

            item.loadItem();
            item.buildItemCache();
            context.openMenu(player);
         })
         .build();
   }
}
