package io.lumine.mythic.core.menus.mobs.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.mobs.MobEditorButton;
import io.lumine.mythic.core.menus.mobs.MobEditorButtons;
import io.lumine.mythic.core.menus.mobs.MobEditorMenuContext;
import org.bukkit.Material;

public class EditTypeButton extends MobEditorButton {
   public EditTypeButton(MobEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public String getLabel() {
      return "Type";
   }

   @Override
   public Icon<MobEditorMenuContext> getIcon() {
      return IconBuilder.<MobEditorMenuContext>create()
         .material(Material.PIG_SPAWN_EGG)
         .name("<green>Set Type")
         .lore(context -> Lists.newArrayList(new String[]{"&c", "<yellow>Current: <white>" + context.getMob().getEntityType()}))
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the entity type you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicEntityType entityType = MythicEntityType.get(input);
                  if (entityType == null) {
                     CommandHelper.sendError(player, "Failed to set entity type: invalid type specified.");
                     return ChatPrompt.Response.TRY_AGAIN;
                  } else {
                     context.getMob().setEntityType(entityType);
                     return ChatPrompt.Response.ACCEPTED;
                  }
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
