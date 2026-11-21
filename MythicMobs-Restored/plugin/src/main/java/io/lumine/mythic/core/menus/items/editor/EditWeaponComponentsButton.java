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
import org.bukkit.Material;

public class EditWeaponComponentsButton extends ItemEditorButton {
   public EditWeaponComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.IRON_SWORD).name("<green>Weapon").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("Weapon");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("Weapon.ItemDamagePerAttack")) {
            lore.add("<yellow>ItemDamagePerAttack: <white>" + config.getString("Weapon.ItemDamagePerAttack", "<none>"));
         }

         if (config.isSet("Weapon.DisableBlockingForSeconds")) {
            lore.add("<yellow>DisableBlockingForSeconds: <white>" + config.getString("Weapon.DisableBlockingForSeconds", "<none>"));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getWeaponComponentMenu().open(player, componentContext);
      }).build();
   }
}
