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

public class EditAttackRangeComponentsButton extends ItemEditorButton {
   public EditAttackRangeComponentsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.COMPASS).name("<green>Attack Range").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         ArrayList<String> lore = Lists.newArrayList(new String[]{"<blue>Component", "&c"});
         boolean enabled = config.isSet("AttackRange");
         lore.add("<yellow>Enabled: <white>" + enabled);
         if (config.isSet("AttackRange.Min")) {
            lore.add("<yellow>Min: <white>" + config.getString("AttackRange.Min", "<none>"));
         }

         if (config.isSet("AttackRange.Max")) {
            lore.add("<yellow>Max: <white>" + config.getString("AttackRange.Max", "<none>"));
         }

         if (config.isSet("AttackRange.MinCreative")) {
            lore.add("<yellow>MinCreative: <white>" + config.getString("AttackRange.MinCreative", "<none>"));
         }

         if (config.isSet("AttackRange.MaxCreative")) {
            lore.add("<yellow>MaxCreative: <white>" + config.getString("AttackRange.MaxCreative", "<none>"));
         }

         if (config.isSet("AttackRange.HitboxMargin")) {
            lore.add("<yellow>HitboxMargin: <white>" + config.getString("AttackRange.HitboxMargin", "<none>"));
         }

         if (config.isSet("AttackRange.MobFactor")) {
            lore.add("<yellow>MobFactor: <white>" + config.getString("AttackRange.MobFactor", "<none>"));
         }

         if (!enabled) {
            lore.add("<yellow>Not Configured");
         }

         return lore;
      }).click((context, player) -> {
         ItemComponentEditorMenuContext componentContext = new ItemComponentEditorMenuContext(context.getItem(), context);
         MythicBukkit.inst().getMenuManager().getAttackRangeComponentMenu().open(player, componentContext);
      }).build();
   }
}
