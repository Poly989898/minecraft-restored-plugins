package io.lumine.mythic.core.menus.spawners;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SpawnerBrowseMenu extends MythicMenu<SpawnerMenuContext> {
   public SpawnerBrowseMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/spawner-browser.yml");
   }

   public void open(Player player) {
      this.open(player, new SpawnerMenuContext());
   }

   public void open(Player player, SpawnerMenuContext context) {
      List<MythicSpawner> spawners = Lists.newArrayList(this.getPlugin().getSpawnerManager().getSpawners());
      if (context.hasFilter()) {
         this.getPlugin().getSpawnerManager().filterSpawners(spawners, context.getFilter());
      }

      if (context.getMode() == SpawnerBrowseMode.ALPHABETICAL) {
         spawners.sort(Comparator.comparing(MythicSpawner::getInternalName, String.CASE_INSENSITIVE_ORDER));
      } else if (context.getMode() == SpawnerBrowseMode.DISTANCE) {
         AbstractLocation base = BukkitAdapter.adapt(player.getLocation());
         spawners.sort(
            (i1, i2) -> {
               boolean sameWorld1 = base.getWorld().getName().equals(i1.getWorldName());
               boolean sameWorld2 = base.getWorld().getName().equals(i2.getWorldName());
               if (sameWorld1 != sameWorld2) {
                  return sameWorld1 ? -1 : 1;
               } else {
                  return !sameWorld1
                     ? i1.getInternalName().compareToIgnoreCase(i2.getInternalName())
                     : Double.compare(base.distanceSquared(i1.getLocation()), base.distanceSquared(i2.getLocation()));
               }
            }
         );
      }

      List<MenuData<SpawnerMenuContext>> data = Lists.newArrayList();

      for (MythicSpawner spawner : spawners) {
         data.add(() -> this.createSpawnerIcon(spawner));
      }

      this.getPlugin().getMenuManager().getSpawnerBrowseMenu().open(player, context, data);
   }

   private Icon<SpawnerMenuContext> createSpawnerIcon(MythicSpawner spawner) {
      return IconBuilder.<SpawnerMenuContext>create().material(Material.SPAWNER).name("<gold>" + spawner.getInternalName()).hideFlags().lore(context -> {
         List<String> lore = describeSpawner(spawner);
         lore.add("<red> ");
         lore.add("<gray>\u22b3 <yellow>Left-Click to Teleport");
         lore.add("<gray>\u22b3 <yellow>Right-Click to Edit");
         return lore;
      }).click((context, player) -> {
         player.closeInventory();
         if (ServerVersion.isFolia()) {
            player.teleportAsync(BukkitAdapter.adapt(spawner.getLocation()));
         } else {
            player.teleport(BukkitAdapter.adapt(spawner.getLocation()));
         }

         player.playSound(player.getLocation(), "block.respawn_anchor.deplete", 1.0F, 2.0F);
      }).rightClick((context, player) -> this.getMenuManager().getSpawnerEditorMenu().open(player, new SpawnerEditorMenuContext(spawner, context))).build();
   }

   public static List<String> describeSpawner(MythicSpawner spawner) {
      AbstractLocation location = spawner.getLocation();
      List<String> lore = Lists.newArrayList();
      lore.add("<red> ");
      lore.add("<yellow>Mob(s)<white>: <white>" + spawner.getTypeName());
      lore.add("<yellow>World<white>: <white>" + spawner.getWorldName());
      lore.add("<yellow>Position<white>: <white>" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
      if (spawner.getGroup() != null && !spawner.getGroup().isEmpty()) {
         lore.add("<yellow>Group<white>: <white>" + spawner.getGroup());
      }

      lore.add("<yellow>Mobs<white>: <white>" + spawner.getNumberOfMobs() + " <gray>/ " + spawner.getMaxMobs().get(spawner));
      lore.add("<yellow>Cooldown<white>: <white>" + spawner.getRemainingCooldownSeconds() + "s <gray>/ " + spawner.getCooldownSeconds() + "s");
      lore.add("<yellow>Warmup<white>: <white>" + spawner.getRemainingWarmupSeconds() + "s <gray>/ " + spawner.getWarmupSeconds() + "s");
      return lore;
   }

   @Override
   public EditableMenuBuilder<SpawnerMenuContext> build(EditableMenuBuilder<SpawnerMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> this.getMenuManager().getMainMenu().open(player, player)));
      builder.getIcon("VIEW_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .lore(context -> Lists.newArrayList(new String[]{"<gold>Current View<white>: <yellow>" + context.getMode()}))
               .click((context, player) -> {
                  context.setMode(context.getMode().next());
                  this.open(player, context);
               })
         );
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
      builder.getIcon("CREATE_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .click(
                  (context, player) -> {
                     player.closeInventory();
                     CommandHelper.sendEditorMessage(
                        player,
                        "Type in the spawner name and mob type:",
                        "<yellow>- &6[spawner_name] &3[mob_type]",
                        "<gray>The spawner is created at your current location.",
                        "<gray>Type cancel to abort creating."
                     );
                     ChatPrompt.listen(player, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                           return ChatPrompt.Response.ACCEPTED;
                        } else {
                           String[] split = input.split(" ");
                           if (split.length < 2) {
                              CommandHelper.sendError(player, "You must supply both a spawner name and a mob type");
                              return ChatPrompt.Response.TRY_AGAIN;
                           } else {
                              String name = split[0];
                              String mobType = split[1];
                              if (this.getPlugin().getSpawnerManager().getSpawnerByName(name) != null) {
                                 CommandHelper.sendError(player, "A spawner named " + name + " already exists");
                                 return ChatPrompt.Response.TRY_AGAIN;
                              } else if (this.getPlugin().getMobManager().getMythicMob(mobType).isEmpty()) {
                                 CommandHelper.sendError(player, "Mob type '" + mobType + "' not found");
                                 return ChatPrompt.Response.TRY_AGAIN;
                              } else {
                                 MythicSpawner spawner = this.getPlugin().getSpawnerManager().createSpawner(name, player.getLocation(), mobType);
                                 if (spawner == null) {
                                    CommandHelper.sendError(player, "Failed to create spawner: see console for errors");
                                    return ChatPrompt.Response.TRY_AGAIN;
                                 } else {
                                    CommandHelper.sendSuccess(player, "Created new spawner " + name + "...");
                                    return ChatPrompt.Response.ACCEPTED;
                                 }
                              }
                           }
                        }
                     }).thenAcceptSync(in -> this.open(player, context));
                  }
               )
         );
      return builder;
   }
}
