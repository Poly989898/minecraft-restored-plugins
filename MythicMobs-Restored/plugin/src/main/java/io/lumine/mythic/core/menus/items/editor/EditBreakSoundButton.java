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
import org.bukkit.Material;

public class EditBreakSoundButton extends ItemEditorButton {
   public EditBreakSoundButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create().material(Material.ANVIL).name("<green>Break Sound").lore(context -> {
         MythicConfig config = context.getItem().getConfig();
         String current = config.getString("BreakSound", config.getString("Options.BreakSound", "<none>"));
         return Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + current});
      }).click((context, player) -> {
         CommandHelper.sendEditorMessage(player, "Type the break sound key (or 'clear').", "Type cancel to abort editing.");
         player.closeInventory();
         ChatPrompt.listen(player, input -> {
            if (input.equalsIgnoreCase("cancel")) {
               return ChatPrompt.Response.ACCEPTED;
            } else {
               MythicItem item = context.getItem();
               if (input.equalsIgnoreCase("clear")) {
                  item.getConfig().unsetSave("BreakSound");
                  item.getConfig().unsetSave("Options.BreakSound");
               } else {
                  item.getConfig().setSave("BreakSound", input);
               }

               item.loadItem();
               item.buildItemCache();
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> context.openMenu(player));
      }).build();
   }
}
