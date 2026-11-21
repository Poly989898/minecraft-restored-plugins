package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import org.bukkit.Material;

public class EditDurabilityMaxButton extends ItemEditorButton {
   public EditDurabilityMaxButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public boolean isApplicable(MythicItem mythicItem) {
      return !mythicItem.isUnbreakable() && mythicItem.getMaterial().getMaxDurability() > 0;
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.ANVIL)
         .name("<green>Set Maximum Durability")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getItem().getMaxDurability()}))
         .click(
            (context, player) -> {
               CommandHelper.sendEditorMessage(
                  player,
                  "Type in the maximum durability you'd like to use.",
                  "(Can be a placeholder that evaluates to an integer)",
                  "Type cancel to abort editing."
               );
               player.closeInventory();
               ChatPrompt.listen(player, input -> {
                  if (input.equalsIgnoreCase("cancel")) {
                     return ChatPrompt.Response.ACCEPTED;
                  } else {
                     MythicItem item = context.getItem();
                     MythicConfig config = item.getConfig();
                     item.setMaxDurability(PlaceholderInt.of(input));
                     String key = config.determineWhichKeyToUse("Options.MaxDurability", "MaxDurability", "CustomDurability.Durability");
                     item.getConfig().setSave(key, input);
                     item.buildItemCache();
                     return ChatPrompt.Response.ACCEPTED;
                  }
               }).thenAcceptSync(in -> context.openMenu(player));
            }
         )
         .build();
   }
}
