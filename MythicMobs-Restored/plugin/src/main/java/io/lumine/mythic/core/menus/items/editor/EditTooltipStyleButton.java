package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import org.bukkit.Material;

public class EditTooltipStyleButton extends ItemEditorButton {
   public EditTooltipStyleButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.NAME_TAG)
         .name("<green>Tooltip Style")
         .lore(
            context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getConfig().getString("TooltipStyle", "<none>")})
         )
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type the tooltip style (or 'clear').", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicItem item = context.getItem();
                  if (input.equalsIgnoreCase("clear")) {
                     item.getConfig().unsetSave("TooltipStyle");
                  } else {
                     item.getConfig().setSave("TooltipStyle", input);
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
