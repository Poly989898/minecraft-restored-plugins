package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public class EditEquippableComponentsButton extends ItemEditorButton {
   public EditEquippableComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.ARMOR_STAND).name("<green>Equippable").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("Equippable");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("Equippable.Model")) {
            lore.add("<yellow>Model: <white>" + config.getString("Equippable.Model", "<none>"));
         }

         if (config.isSet("Equippable.Slot")) {
            lore.add("<yellow>Slot: <white>" + config.getString("Equippable.Slot", "<none>"));
         }

         if (config.isSet("Equippable.EquipSound")) {
            lore.add("<yellow>EquipSound: <white>" + config.getString("Equippable.EquipSound", "<none>"));
         }

         if (config.isSet("Equippable.CameraOverlay")) {
            lore.add("<yellow>CameraOverlay: <white>" + config.getString("Equippable.CameraOverlay", "<none>"));
         }

         if (config.isSet("Equippable.EntityTypes")) {
            List<String> types = config.getStringList("Equippable.EntityTypes");
            lore.add("<yellow>EntityTypes: <white>" + (types != null && !types.isEmpty() ? String.join(", ", types) : "<none>"));
         }

         if (config.isSet("Equippable.Dispensable")) {
            lore.add("<yellow>Dispensable: <white>" + config.getBoolean("Equippable.Dispensable", false));
         }

         if (config.isSet("Equippable.Swappable")) {
            lore.add("<yellow>Swappable: <white>" + config.getBoolean("Equippable.Swappable", false));
         }

         if (config.isSet("Equippable.DamageOnHurt")) {
            lore.add("<yellow>DamageOnHurt: <white>" + config.getBoolean("Equippable.DamageOnHurt", false));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getEquippableComponentMenu().open(player, componentContext);
      }).build();
   }
}
