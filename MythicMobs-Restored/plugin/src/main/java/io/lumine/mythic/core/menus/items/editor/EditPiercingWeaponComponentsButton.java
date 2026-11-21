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

public class EditPiercingWeaponComponentsButton extends ItemEditorButton {
   public EditPiercingWeaponComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.TRIDENT).name("<green>Piercing Weapon").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = this.hasPiercingWeapon(config);
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("PiercingWeapon.DealsKnockback")) {
            lore.add("<yellow>DealsKnockback: <white>" + config.getBoolean("PiercingWeapon.DealsKnockback", false));
         }

         if (config.isSet("PiercingWeapon.Dismounts")) {
            lore.add("<yellow>Dismounts: <white>" + config.getBoolean("PiercingWeapon.Dismounts", false));
         }

         if (config.isSet("PiercingWeapon.Sound")) {
            lore.add("<yellow>Sound: <white>" + config.getString("PiercingWeapon.Sound", "<none>"));
         }

         if (config.isSet("PiercingWeapon.HitSound")) {
            lore.add("<yellow>HitSound: <white>" + config.getString("PiercingWeapon.HitSound", "<none>"));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getPiercingWeaponComponentMenu().open(player, componentContext);
      }).build();
   }

   private boolean hasPiercingWeapon(MythicConfig config) {
      return config.isSet("PiercingWeapon");
   }
}
