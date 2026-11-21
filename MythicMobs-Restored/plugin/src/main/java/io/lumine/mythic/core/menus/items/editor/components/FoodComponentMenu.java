package io.lumine.mythic.core.menus.items.editor.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FoodComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public FoodComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getFoodComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigNumberPromptButton(0, Material.APPLE, "<white>Nutrition", "Food.Nutrition", "Type the nutrition value (or 'clear')"),
            new ConfigNumberPromptButton(1, Material.GOLDEN_APPLE, "<white>Saturation", "Food.Saturation", "Type the saturation value (or 'clear')"),
            new ConfigNumberPromptButton(2, Material.CLOCK, "<white>Eat Seconds", "Food.EatSeconds", "Type the eat time in seconds (or 'clear')"),
            new ConfigToggleButton(3, Material.CAKE, "<white>Can Always Eat", "Food.CanAlwaysEat", false)
         }
      );
      this.open(player, context, Text.colorize("Food: " + context.getItem().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<ItemComponentEditorMenuContext> build(EditableMenuBuilder<ItemComponentEditorMenuContext> builder) {
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         MythicItem item = context.getItem();

         for (String key : List.of("Food", "Food.Nutrition", "Food.Saturation", "Food.EatSeconds", "Food.CanAlwaysEat")) {
            item.getConfig().unsetSave(key);
         }

         item.loadItem();
         item.buildItemCache();
         context.openMenu(player);
      }));
      return builder;
   }
}
