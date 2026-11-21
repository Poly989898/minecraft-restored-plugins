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

public class EquippableComponentMenu extends MythicMenu<ItemComponentEditorMenuContext> {
   public EquippableComponentMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-component-detail.yml");
   }

   public void open(Player player, ItemComponentEditorMenuContext context) {
      context.setReopenMenu(p -> MythicBukkit.inst().getMenuManager().getEquippableComponentMenu().open(p, context));
      List<MenuData<ItemComponentEditorMenuContext>> buttons = Lists.newArrayList(
         new MenuData[]{
            new ConfigStringPromptButton(0, Material.ARMOR_STAND, "<white>Model", "Equippable.Model", "Type the model key (or 'clear')", true),
            new ConfigStringPromptButton(1, Material.IRON_CHESTPLATE, "<white>Slot", "Equippable.Slot", "Type the slot name (or 'clear')", false),
            new ConfigStringPromptButton(2, Material.NOTE_BLOCK, "<white>Equip Sound", "Equippable.EquipSound", "Type the equip sound key (or 'clear')", true),
            new ConfigStringPromptButton(
               3, Material.GLASS_PANE, "<white>Camera Overlay", "Equippable.CameraOverlay", "Type the camera overlay key (or 'clear')", true
            ),
            new ConfigStringListPromptButton(
               4, Material.NAME_TAG, "<white>Entity Types", "Equippable.EntityTypes", "Type entity types separated by commas (or 'clear')"
            ),
            new ConfigToggleButton(5, Material.DISPENSER, "<white>Dispensable", "Equippable.Dispensable", false),
            new ConfigToggleButton(6, Material.HOPPER, "<white>Swappable", "Equippable.Swappable", false),
            new ConfigToggleButton(7, Material.IRON_INGOT, "<white>Damage On Hurt", "Equippable.DamageOnHurt", false)
         }
      );
      this.open(player, context, Text.colorize("Equippable: " + context.getItem().getInternalName()), buttons);
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
                        "Equippable",
                        "Equippable.Model",
                        "Equippable.Slot",
                        "Equippable.EquipSound",
                        "Equippable.CameraOverlay",
                        "Equippable.EntityTypes",
                        "Equippable.Dispensable",
                        "Equippable.Swappable",
                        "Equippable.DamageOnHurt"
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
