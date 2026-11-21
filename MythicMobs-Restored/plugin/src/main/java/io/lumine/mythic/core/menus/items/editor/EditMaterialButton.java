package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import org.bukkit.Material;

public class EditMaterialButton extends ItemEditorButton {
   public EditMaterialButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.GRASS_BLOCK)
         .name("<green>Set Material")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getMaterial()}))
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the item type (Material) you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else if (!context.getItem().setMaterial(input)) {
                  CommandHelper.sendError(player, "Failed to set material: invalid material specified.");
                  return ChatPrompt.Response.TRY_AGAIN;
               } else {
                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
