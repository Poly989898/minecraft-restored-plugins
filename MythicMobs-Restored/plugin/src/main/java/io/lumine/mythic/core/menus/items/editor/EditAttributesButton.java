package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.commands.items.edit.ItemEditCommand;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public class EditAttributesButton extends ItemEditorButton {
   public EditAttributesButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.WRITABLE_BOOK)
         .name("<green>Edit Attributes")
         .lore(
            context -> {
               ArrayList<String> lore = Lists.newArrayList(
                  new String[]{"<white>Left-click add a line", "<white>Right-click to edit lines", "&c", "<yellow>Current:", "&c"}
               );
               lore.addAll(context.getItem().getLore());
               return lore;
            }
         )
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the Lore line you'd like to add.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicItem item = context.getItem();
                  List<PlaceholderString> lore = item.getLoreRaw();
                  lore.add(PlaceholderString.of(input));
                  item.setLore(lore);
                  ArrayList<Object> saveList = Lists.newArrayList();

                  for (PlaceholderString pl : lore) {
                     saveList.add(pl.toString());
                  }

                  item.getConfig().setSave("Lore", saveList);
                  item.buildItemCache();
                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .rightClick((context, player) -> {
            ItemEditCommand.lastEdited.put(player.getUniqueId(), context);
            player.performCommand("mm i edit lore " + context.getItem().getInternalName());
            player.closeInventory();
         })
         .build();
   }
}
