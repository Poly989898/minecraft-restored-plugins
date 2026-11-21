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

public class AttackRangeComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public AttackRangeComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getAttackRangeComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigNumberPromptButton(0, Material.COMPASS, "<white>Min Reach", "AttackRange.Min", "Type the minimum reach (or 'clear')"),
            new ConfigNumberPromptButton(1, Material.COMPASS, "<white>Max Reach", "AttackRange.Max", "Type the maximum reach (or 'clear')"),
            new ConfigNumberPromptButton(
               2, Material.MAP, "<white>Min Creative Reach", "AttackRange.MinCreative", "Type the minimum creative reach (or 'clear')"
            ),
            new ConfigNumberPromptButton(
               3, Material.MAP, "<white>Max Creative Reach", "AttackRange.MaxCreative", "Type the maximum creative reach (or 'clear')"
            ),
            new ConfigNumberPromptButton(4, Material.PAPER, "<white>Hitbox Margin", "AttackRange.HitboxMargin", "Type the hitbox margin (or 'clear')"),
            new ConfigNumberPromptButton(5, Material.SLIME_BALL, "<white>Mob Factor", "AttackRange.MobFactor", "Type the mob factor (or 'clear')")
         }
      );
      this.open(player, context, Text.colorize("AttackRange: " + context.getItem().getInternalName()), buttons);
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
                        "AttackRange",
                        "AttackRange.Min",
                        "AttackRange.Max",
                        "AttackRange.MinCreative",
                        "AttackRange.MaxCreative",
                        "AttackRange.HitboxMargin",
                        "AttackRange.MobFactor"
                     )) {
                        item.getConfig().unsetSave(key);
                     }

                     item.loadItem();
                     item.buildItemCache();
                     context.openMenu(player);
                  }
               )
         );
      return builder;
   }
}
