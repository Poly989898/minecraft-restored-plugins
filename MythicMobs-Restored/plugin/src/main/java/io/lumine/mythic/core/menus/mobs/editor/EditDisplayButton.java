package io.lumine.mythic.core.menus.mobs.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.mobs.MobEditorButton;
import io.lumine.mythic.core.menus.mobs.MobEditorButtons;
import io.lumine.mythic.core.menus.mobs.MobEditorMenuContext;
import org.bukkit.Material;

public class EditDisplayButton extends MobEditorButton {
   public EditDisplayButton(MobEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public String getLabel() {
      return "Display Name";
   }

   @Override
   public Icon<MobEditorMenuContext> getIcon() {
      return IconBuilder.<MobEditorMenuContext>create()
         .material(Material.NAME_TAG)
         .name("<green>Set Display Name")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getMob().getDisplayName()}))
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the Display Name you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicMob editing = context.getMob();
                  editing.setDisplayName(PlaceholderString.of(input));
                  editing.getConfig().setSave("Display", input);
                  editing.buildCache();
                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
