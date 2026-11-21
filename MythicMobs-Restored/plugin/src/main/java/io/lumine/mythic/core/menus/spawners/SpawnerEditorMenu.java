package io.lumine.mythic.core.menus.spawners;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.List;
import java.util.Locale;
import org.bukkit.entity.Player;

public class SpawnerEditorMenu extends MythicMenu<SpawnerEditorMenuContext> {
   private SpawnerEditorButtons buttons = new SpawnerEditorButtons();

   public SpawnerEditorMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/spawner-editor.yml");
   }

   public void open(Player player, MythicSpawner mythicSpawner) {
      this.open(player, new SpawnerEditorMenuContext(mythicSpawner, new SpawnerMenuContext()));
   }

   public void open(Player player, SpawnerEditorMenuContext context) {
      List<SpawnerEditorButton> buttons = Lists.newArrayList(this.buttons.getButtons());
      if (context.hasFilter()) {
         String filter = context.getFilter().toUpperCase(Locale.ROOT);
         buttons.removeIf(button -> !button.getLabel().toUpperCase(Locale.ROOT).contains(filter));
      }

      this.open(player, context, Text.colorize("Editing Spawner: " + context.getSpawner().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<SpawnerEditorMenuContext> build(EditableMenuBuilder<SpawnerEditorMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("SPAWNER_BUTTON")
         .ifPresent(icon -> icon.getBuilder().name(context -> "<gold>" + context.getSpawner().getInternalName()).lore(context -> {
            List<String> lore = SpawnerBrowseMenu.describeSpawner(context.getSpawner());
            lore.add("<red> ");
            lore.add("<gray>\u22b3 <yellow>Left-Click to Teleport");
            lore.add("<gray>\u22b3 <yellow>Right-Click to Reset Timers");
            return lore;
         }).hideFlags().click((context, player) -> {
            player.closeInventory();
            if (ServerVersion.isFolia()) {
               player.teleportAsync(BukkitAdapter.adapt(context.getSpawner().getLocation()));
            } else {
               player.teleport(BukkitAdapter.adapt(context.getSpawner().getLocation()));
            }

            player.playSound(player.getLocation(), "block.respawn_anchor.deplete", 1.0F, 2.0F);
         }).rightClick((context, player) -> {
            context.getSpawner().resetTimers();
            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
            context.openMenu(player);
         }));
      builder.getIcon("SEARCH_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .lore(
                  context -> Lists.newArrayList(new String[]{"<red> ", "<yellow>Filter<white>: <white>" + (context.hasFilter() ? context.getFilter() : "None")})
               )
               .click((context, player) -> {
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
               })
         );
      builder.getIcon("SEARCH_RESET_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         context.clearFilter();
         this.open(player, context);
      }));
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().hideFlags(false).click((context, player) -> {
         CommandHelper.sendEditorMessage(player, "Type <red>DELETE <white>to confirm deleting", "Type anything else to abort");
         player.closeInventory();
         ChatPrompt.listen(player, input -> {
            if (!input.equals("DELETE")) {
               context.openMenu(player);
               return ChatPrompt.Response.ACCEPTED;
            } else {
               this.getPlugin().getSpawnerManager().removeSpawner(context.getSpawner());
               context.openPreviousMenu(player);
               player.playSound(player.getLocation(), "entity.zombie.break_wooden_door", 1.0F, 1.0F);
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> {});
      }));
      return builder;
   }
}
