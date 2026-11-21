package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.commands.items.edit.EditEnchantsCommand;
import io.lumine.mythic.bukkit.commands.items.edit.ItemEditCommand;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.ArrayList;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class EditEnchantsButton extends ItemEditorButton {
   public EditEnchantsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.ENCHANTING_TABLE)
         .name("<green>Edit Enchantments")
         .lore(
            context -> {
               ArrayList<String> lore = Lists.newArrayList(
                  new String[]{"<white>Left-click add an enchantment", "<white>Right-click to edit enchantments", "&c", "<yellow>Current:", "&c"}
               );

               for (Entry<Enchantment, PlaceholderInt> entry : context.getItem().getEnchantments().entrySet()) {
                  lore.add("<gray>" + entry.getKey().getName() + " " + entry.getValue().toString());
               }

               return lore;
            }
         )
         .click(
            (context, player) -> {
               player.closeInventory();
               CommandHelper.sendEditorMessage(
                  player, "Type in the Enchantment you'd like to add.", "Format: [enchantment_type] [amount]", "Type cancel to abort editing."
               );
               ChatPrompt.listen(
                     player,
                     input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                           return ChatPrompt.Response.ACCEPTED;
                        } else if (!context.getItem().addEnchantment(input)) {
                           CommandHelper.sendError(player, "Invalid enchantment supplied");
                           CommandHelper.sendEditorMessage(
                              player, "Type in the Enchantment you'd like to add.", "Format: [enchantment_type] [amount]", "Type cancel to abort editing."
                           );
                           return ChatPrompt.Response.TRY_AGAIN;
                        } else {
                           EditEnchantsCommand.saveEnchants(context.getItem());
                           return ChatPrompt.Response.ACCEPTED;
                        }
                     }
                  )
                  .thenAcceptSync(in -> {
                     context.getItem().buildItemCache();
                     context.openMenu(player);
                  });
            }
         )
         .rightClick((context, player) -> {
            ItemEditCommand.lastEdited.put(player.getUniqueId(), context);
            player.performCommand("mm i edit enchants " + context.getItem().getInternalName());
            player.closeInventory();
         })
         .build();
   }
}
