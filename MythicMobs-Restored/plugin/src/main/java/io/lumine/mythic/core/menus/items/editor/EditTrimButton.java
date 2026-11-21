package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public class EditTrimButton extends ItemEditorButton {
   public EditTrimButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.SMITHING_TABLE).name("<green>Trim").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         String current = config.getString("Trim", null);
         if (current == null) {
            current = config.getString("Options.Trim", null);
         }

         if (current == null) {
            String material = config.getString("Trim.Material", config.getString("Options.Trim.Material", null));
            String pattern = config.getString("Trim.Pattern", config.getString("Options.Trim.Pattern", null));
            if (material != null || pattern != null) {
               current = (material == null ? "iron" : material) + "." + (pattern == null ? "wild" : pattern);
            }
         }

         if (current == null) {
            current = "<none>";
         }

         return Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + current});
      }).click((context, player) -> {
         CommandHelper.sendEditorMessage(player, "Type the trim as material.pattern (or 'clear').", "Type cancel to abort editing.");
         player.closeInventory();
         ChatPrompt.listen(player, input -> {
            if (input.equalsIgnoreCase("cancel")) {
               return ChatPrompt.Response.ACCEPTED;
            } else {
               MythicItem item = context.getItem();
               MythicConfig config = item.getConfig();
               if (input.equalsIgnoreCase("clear")) {
                  for (String key : List.of("Trim", "Options.Trim", "Trim.Material", "Trim.Pattern", "Options.Trim.Material", "Options.Trim.Pattern")) {
                     config.unsetSave(key);
                  }
               } else {
                  String trimmed = input.trim();
                  String material = "iron";
                  String pattern = trimmed;
                  String[] parts = trimmed.split("\\.", 2);
                  if (parts.length > 1) {
                     material = parts[0];
                     pattern = parts[1];
                  }

                  NamespacedKey materialKey = toKey(material);
                  NamespacedKey patternKey = toKey(pattern);
                  if (materialKey == null || Registry.TRIM_MATERIAL.get(materialKey) == null) {
                     CommandHelper.sendEditorMessage(player, "<yellow>Warning: Trim material not found: <white>" + material, "");
                  }

                  if (patternKey == null || Registry.TRIM_PATTERN.get(patternKey) == null) {
                     CommandHelper.sendEditorMessage(player, "<yellow>Warning: Trim pattern not found: <white>" + pattern, "");
                  }

                  config.unsetSave("Options.Trim");
                  config.unsetSave("Trim.Material");
                  config.unsetSave("Trim.Pattern");
                  config.unsetSave("Options.Trim.Material");
                  config.unsetSave("Options.Trim.Pattern");
                  config.setSave("Trim", trimmed);
               }

               item.loadItem();
               item.buildItemCache();
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> context.openMenu(player));
      }).build();
   }

   private static NamespacedKey toKey(String input) {
      if (input != null && !input.isBlank()) {
         String value = input.toLowerCase(Locale.ROOT);
         if (!value.contains(":")) {
            value = "minecraft:" + value;
         }

         return NamespacedKey.fromString(value);
      } else {
         return null;
      }
   }
}
