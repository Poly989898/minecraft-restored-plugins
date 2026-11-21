package io.lumine.mythic.core.menus.items.editor.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import org.bukkit.Material;

public class ConfigStringPromptButton extends ComponentMenuButton<ItemComponentEditorMenuContext> {
   private final Material material;
   private final String name;
   private final String configPath;
   private final String prompt;
   private final boolean normalizeNamespacedKey;

   public ConfigStringPromptButton(int sortIndex, Material material, String name, String configPath, String prompt, boolean normalizeNamespacedKey) {
      super(sortIndex);
      this.material = material;
      this.name = name;
      this.configPath = configPath;
      this.prompt = prompt;
      this.normalizeNamespacedKey = normalizeNamespacedKey;
   }

   @Override
   public Icon<ItemComponentEditorMenuContext> getIcon() {
      return IconBuilder.<ItemComponentEditorMenuContext>create()
         .material(this.material)
         .name(this.name)
         .lore(
            context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getConfig().getString(this.configPath, "<none>")})
         )
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, this.prompt, "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicItem item = context.getItem();
                  if (input.equalsIgnoreCase("clear")) {
                     item.getConfig().unsetSave(this.configPath);
                  } else {
                     String value = this.normalizeNamespacedKey ? MythicConfig.normalizeNamespacedKey(input) : input;
                     item.getConfig().setSave(this.configPath, value);
                  }

                  item.loadItem();
                  item.buildItemCache();
                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
