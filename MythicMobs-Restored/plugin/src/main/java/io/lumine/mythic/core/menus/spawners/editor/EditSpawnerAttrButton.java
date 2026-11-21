package io.lumine.mythic.core.menus.spawners.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorButton;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorButtons;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorMenuContext;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.function.Function;
import org.bukkit.Material;

public class EditSpawnerAttrButton extends SpawnerEditorButton {
   private final String label;
   private final String attribute;
   private final Material icon;
   private final Function<MythicSpawner, String> currentValue;
   private final String hint;

   public EditSpawnerAttrButton(
      SpawnerEditorButtons parent, int sort, String label, String attribute, Material icon, Function<MythicSpawner, String> currentValue, String hint
   ) {
      super(parent, sort);
      this.label = label;
      this.attribute = attribute;
      this.icon = icon;
      this.currentValue = currentValue;
      this.hint = hint;
   }

   @Override
   public String getLabel() {
      return this.label;
   }

   @Override
   public Icon<SpawnerEditorMenuContext> getIcon() {
      return IconBuilder.<SpawnerEditorMenuContext>create()
         .material(this.icon)
         .name("<green>Set " + this.label)
         .hideFlags()
         .lore(
            context -> {
               String value;
               try {
                  value = this.currentValue.apply(context.getSpawner());
               } catch (Error | Exception e) {
                  value = null;
               }

               return Lists.newArrayList(
                  new String[]{"&c", "<yellow>Current: <white>" + (value == null ? "None" : value), "<red> ", "<gray>\u22b3 <yellow>Click to Edit"}
               );
            }
         )
         .click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the " + this.label + " value you'd like to use.", this.hint, "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  MythicSpawner spawner = context.getSpawner();
                  if (!MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(spawner, this.attribute, input)) {
                     CommandHelper.sendError(player, "Invalid value for " + this.label);
                     return ChatPrompt.Response.TRY_AGAIN;
                  } else {
                     spawner.save();
                     return ChatPrompt.Response.ACCEPTED;
                  }
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
