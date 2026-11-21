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

public class ConsumableComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public ConsumableComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getConsumableComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigNumberPromptButton(
               0, Material.CLOCK, "<white>Consume Seconds", "Consumable.ConsumeSeconds", "Type the consume time in seconds (or 'clear')"
            ),
            new ConfigToggleButton(1, Material.GLOWSTONE_DUST, "<white>Has Particles", "Consumable.HasParticles", true),
            new ConfigStringPromptButton(2, Material.BOW, "<white>Animation", "Consumable.Animation", "Type the animation key (or 'clear')", false),
            new ConfigStringPromptButton(3, Material.NOTE_BLOCK, "<white>Sound", "Consumable.Sound", "Type the sound key (or 'clear')", true)
         }
      );
      this.open(player, context, Text.colorize("Consumable: " + context.getItem().getInternalName()), buttons);
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
                        "Consumable",
                        "Consumable.ConsumeSeconds",
                        "Consumable.HasParticles",
                        "Consumable.Animation",
                        "Consumable.Sound",
                        "Consumable.ConsumeEffects"
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
