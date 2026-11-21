package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.Optional;
import org.bukkit.Material;

public class EditCustomModelDataButton extends ItemEditorButton {
   public EditCustomModelDataButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.PAINTING)
         .name("<green>Set CustomModelData")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getCustomModelData()}))
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the CustomModelData you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  Optional<Integer> maybeInt = Numbers.parseIntegerOpt(input);
                  if (maybeInt.isEmpty()) {
                     CommandHelper.sendError(player, "Failed to set CustomModelData: invalid value specified.");
                     return ChatPrompt.Response.TRY_AGAIN;
                  } else {
                     MythicItem item = context.getItem();
                     item.setCustomModelData(maybeInt.get());
                     item.getConfig().setSave("CustomModelData", maybeInt.get());
                     item.buildItemCache();
                     return ChatPrompt.Response.ACCEPTED;
                  }
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
