package io.lumine.mythic.core.menus.items.editor.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemComponentEditorMenuContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;

public class ConfigStringListPromptButton extends ComponentMenuButton<ItemComponentEditorMenuContext> {
   private final Material material;
   private final String name;
   private final String configPath;
   private final String prompt;

   public ConfigStringListPromptButton(int sortIndex, Material material, String name, String configPath, String prompt) {
      super(sortIndex);
      this.material = material;
      this.name = name;
      this.configPath = configPath;
      this.prompt = prompt;
   }

   @Override
   public Icon<ItemComponentEditorMenuContext> getIcon() {
      return IconBuilder.<ItemComponentEditorMenuContext>create().material(this.material).name(this.name).lore(context -> {
         List<String> values = context.getItem().getConfig().getStringList(this.configPath);
         String current = values != null && !values.isEmpty() ? String.join(", ", values) : "<none>";
         return Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + current});
      }).click((context, player) -> {
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
                  List<String> values = Arrays.stream(input.split(",")).map(String::trim).filter(value -> !value.isEmpty()).collect(Collectors.toList());
                  if (values.isEmpty()) {
                     CommandHelper.sendEditorMessage(player, "<red>Invalid list: <white>" + input, "");
                     return ChatPrompt.Response.ACCEPTED;
                  }

                  item.getConfig().setSave(this.configPath, values);
               }

               item.loadItem();
               item.buildItemCache();
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> context.openMenu(player));
      }).build();
   }
}
