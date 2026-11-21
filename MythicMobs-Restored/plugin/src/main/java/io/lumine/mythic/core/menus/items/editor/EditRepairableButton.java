package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import org.bukkit.Material;

public class EditRepairableButton extends ItemEditorButton {
   public EditRepairableButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public boolean isApplicable(MythicItem mythicItem) {
      return !mythicItem.isUnbreakable() && mythicItem.getMaterial().getMaxDurability() > 0;
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.ENCHANTED_BOOK)
         .name("<green>Is Repairable")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + !context.getItem().getPreventAnvilWith()}))
         .click((context, player) -> {
            MythicItem item = context.getItem();
            boolean value = !item.getPreventAnvilWith();
            item.getConfig().setSave("Options.PreventAnvil", value);
            item.loadItem();
            item.buildItemCache();
         })
         .build();
   }
}
