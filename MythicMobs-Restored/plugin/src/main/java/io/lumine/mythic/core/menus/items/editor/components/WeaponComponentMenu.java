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

public class WeaponComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public WeaponComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getWeaponComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigNumberPromptButton(
               0, Material.IRON_SWORD, "<white>Item Damage Per Attack", "Weapon.ItemDamagePerAttack", "Type the item damage per attack (or 'clear')"
            ),
            new ConfigNumberPromptButton(
               1,
               Material.SHIELD,
               "<white>Disable Blocking (Seconds)",
               "Weapon.DisableBlockingForSeconds",
               "Type the disable blocking duration in seconds (or 'clear')"
            )
         }
      );
      this.open(player, context, Text.colorize("Weapon: " + context.getItem().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<ItemComponentEditorMenuContext> build(EditableMenuBuilder<ItemComponentEditorMenuContext> builder) {
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         MythicItem item = context.getItem();

         for (String key : List.of("Weapon", "Weapon.ItemDamagePerAttack", "Weapon.DisableBlockingForSeconds")) {
            item.getConfig().unsetSave(key);
         }

         item.loadItem();
         item.buildItemCache();
         context.openMenu(player);
      }));
      return builder;
   }
}
