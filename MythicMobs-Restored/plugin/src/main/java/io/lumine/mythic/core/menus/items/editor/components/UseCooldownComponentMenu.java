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

public class UseCooldownComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public UseCooldownComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getUseCooldownComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigStringPromptButton(
               0, Material.NAME_TAG, "<white>Cooldown Group", "UseCooldown.CooldownGroup", "Type the cooldown group key (or 'clear')", true
            ),
            new ConfigNumberPromptButton(
               1, Material.CLOCK, "<white>Cooldown Seconds", "UseCooldown.CooldownSeconds", "Type the cooldown in seconds (or 'clear')"
            )
         }
      );
      this.open(player, context, Text.colorize("UseCooldown: " + context.getItem().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<ItemComponentEditorMenuContext> build(EditableMenuBuilder<ItemComponentEditorMenuContext> builder) {
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         MythicItem item = context.getItem();

         for (String key : List.of("UseCooldown", "UseCooldown.CooldownGroup", "UseCooldown.CooldownSeconds")) {
            item.getConfig().unsetSave(key);
         }

         item.loadItem();
         item.buildItemCache();
         context.openMenu(player);
      }));
      return builder;
   }
}
