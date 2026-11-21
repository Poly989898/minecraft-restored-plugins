package io.lumine.mythic.core.menus;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MainMenu extends MythicMenu<Player> {
   public MainMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/main.yml");
   }

   @Override
   public EditableMenuBuilder<Player> build(EditableMenuBuilder<Player> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BUTTON_MOBBROWSER").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         this.getMenuManager().getMobBrowseMenu().open(player);
         player.playSound(player.getLocation(), Sound.ITEM_BOOK_PUT, 1.0F, 1.0F);
      }));
      builder.getIcon("BUTTON_ITEMBROWSER").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         this.getMenuManager().getItemBrowseMenu().open(player);
         player.playSound(player.getLocation(), Sound.ITEM_BOOK_PUT, 1.0F, 1.0F);
      }));
      builder.getIcon("BUTTON_DTBROWSER").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         CommandHelper.sendError(player, "The Drop Table Browser is not available yet.");
      }));
      builder.getIcon("BUTTON_RANDOMSPAWNINGBROWSER").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         CommandHelper.sendError(player, "The Random Spawning Browser is not available yet.");
      }));
      builder.getIcon("BUTTON_SPAWNERBROWSER").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         this.getMenuManager().getSpawnerBrowseMenu().open(player);
         player.playSound(player.getLocation(), Sound.ITEM_BOOK_PUT, 1.0F, 1.0F);
      }));
      return builder;
   }
}
