package io.lumine.mythic.core.menus.config;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import org.bukkit.entity.Player;

public class ConfigMenu extends MythicMenu<ConfigMenuContext> {
   public ConfigMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/config.yml");
   }

   public void open(Player player) {
      this.open(player, new ConfigMenuContext());
   }

   public void open(Player player, ConfigMenuContext context) {
   }

   @Override
   public EditableMenuBuilder<ConfigMenuContext> build(EditableMenuBuilder<ConfigMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> this.getMenuManager().getMainMenu().open(player, player)));
      builder.getIcon("FILTER_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         player.closeInventory();
         CommandHelper.sendEditorMessage(player, "Type in the text you'd like to search for:", "<gray>Type cancel to abort filtering.");
         ChatPrompt.listen(player, input -> {
            if (input.equalsIgnoreCase("cancel")) {
               return ChatPrompt.Response.ACCEPTED;
            } else {
               context.setFilter(input);
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> this.open(player, context));
      }));
      builder.getIcon("FILTER_RESET_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         context.clearFilter();
         this.open(player, context);
      }));
      return builder;
   }
}
