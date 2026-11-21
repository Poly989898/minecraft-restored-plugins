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

public class PiercingWeaponComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public PiercingWeaponComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getPiercingWeaponComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigToggleButton(0, Material.SHIELD, "<white>Deals Knockback", "PiercingWeapon.DealsKnockback", false),
            new ConfigToggleButton(1, Material.SADDLE, "<white>Dismounts", "PiercingWeapon.Dismounts", false),
            new ConfigStringPromptButton(2, Material.NOTE_BLOCK, "<white>Sound", "PiercingWeapon.Sound", "Type the sound key to use (or 'clear')", true),
            new ConfigStringPromptButton(
               3, Material.NOTE_BLOCK, "<white>Hit Sound", "PiercingWeapon.HitSound", "Type the hit sound key to use (or 'clear')", true
            )
         }
      );
      this.open(player, context, Text.colorize("PiercingWeapon: " + context.getItem().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<ItemComponentEditorMenuContext> build(EditableMenuBuilder<ItemComponentEditorMenuContext> builder) {
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("DELETE_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .click(
                  (context, player) -> {
                     MythicItem item = context.getItem();

                     for (String key : List.of(
                        "PiercingWeapon", "PiercingWeapon.DealsKnockback", "PiercingWeapon.Dismounts", "PiercingWeapon.Sound", "PiercingWeapon.HitSound"
                     )) {
                        item.getConfig().unsetSave(key);
                     }

                     item.loadItem();
                     item.buildItemCache();
                     context.openPreviousMenu(player);
                  }
               )
         );
      return builder;
   }
}
