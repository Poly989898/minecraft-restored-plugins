package io.lumine.mythic.core.menus.mobs;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.mobs.MobType;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;

public class MobBrowseMenu extends MythicMenu<MobMenuContext> {
   private static final String DEFAULT_NEW_MOB_TYPE = "ZOMBIE";

   public MobBrowseMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/mob-browser.yml");
   }

   public void open(Player player) {
      this.open(player, new MobMenuContext());
   }

   public void open(Player player, MobMenuContext context) {
      if (context.getMode() == MobBrowseMode.PACKS && context.getPackFilter() == null) {
         List<MenuData<MobMenuContext>> data = Lists.newArrayList();

         for (Pack pack : this.getPlugin().getPackManager().getPacks()) {
            data.add(() -> pack.createMenuIcon((pContext, pPlayer) -> {
               pContext.setPackFilter(pack);
               this.open(pPlayer, pContext);
            }));
         }

         this.getPlugin().getMenuManager().getMobBrowseMenu().open(player, context, data);
      } else {
         List<MobType> mobs = Lists.newArrayList();
         this.getPlugin().getMobManager().getMobTypes().forEach(mob -> {
            if (!mob.isHidden()) {
               mobs.add((MobType)mob);
            }
         });
         if (context.hasPackFilter()) {
            mobs.removeIf(mm -> mm.getPack() == null || mm.getPack() != context.getPackFilter());
         }

         if (context.hasFilter()) {
            this.getPlugin().getMobManager().filterMobs(mobs, context.getFilter());
         }

         mobs.sort(Comparator.comparing(MobType::getInternalName, String.CASE_INSENSITIVE_ORDER));
         this.getPlugin().getMenuManager().getMobBrowseMenu().open(player, context, mobs);
      }
   }

   @Override
   public EditableMenuBuilder<MobMenuContext> build(EditableMenuBuilder<MobMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         if (context.getMode() == MobBrowseMode.PACKS && context.hasPackFilter()) {
            context.clearPackFilter();
            this.open(player, context);
         } else {
            this.getMenuManager().getMainMenu().open(player, player);
         }
      }));
      builder.getIcon("VIEW_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .lore(context -> Lists.newArrayList(new String[]{"<gold>Current View<white>: <yellow>" + context.getMode()}))
               .click((context, player) -> {
                  context.clearPackFilter();
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
                        "Type in the mob name in one of the following ways:",
                        "<yellow>- &6[mob_name]",
                        "<yellow>- &3<pack>&7.&6[mob_name] &3<path_to_file>",
                        "<gray>Type cancel to abort editing."
                     );
                     ChatPrompt.listen(player, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                           return ChatPrompt.Response.ACCEPTED;
                        } else {
                           String packName = null;
                           String filePath = null;
                           String[] split1 = input.split(" ");
                           if (split1.length > 1) {
                              input = split1[0];
                              filePath = split1[1];
                           }

                           String[] split2 = input.split("\\.");
                           if (split2.length > 1) {
                              packName = split2[0];
                              input = split2[1];
                           }

                           if (this.getPlugin().getMobManager().getMythicMob(input).isPresent()) {
                              CommandHelper.sendError(player, "A mob named " + input + " already exists");
                              return ChatPrompt.Response.TRY_AGAIN;
                           } else {
                              if (filePath == null) {
                                 filePath = input.toLowerCase() + ".yml";
                              } else if (!filePath.endsWith(".yml")) {
                                 filePath = filePath + ".yml";
                              }

                              if (filePath.contains("..")) {
                                 CommandHelper.sendError(player, "Invalid file path");
                                 return ChatPrompt.Response.TRY_AGAIN;
                              } else {
                                 Optional<Pack> maybePack = this.getPlugin().getPackManager().getPack(packName);
                                 if (maybePack.isEmpty()) {
                                    CommandHelper.sendError(player, "Invalid pack specified: not found");
                                    return ChatPrompt.Response.TRY_AGAIN;
                                 } else {
                                    Pack pack = maybePack.get();
                                    File file = new File(pack.getFolder(), "Mobs/" + filePath);
                                    if (!file.exists()) {
                                       if (!file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
                                          CommandHelper.sendError(player, "Failed to create file: see console for errors C");
                                          return ChatPrompt.Response.TRY_AGAIN;
                                       }
                                       try {
                                          if (!file.createNewFile()) {
                                             CommandHelper.sendError(player, "Failed to create file: see console for errors A");
                                             return ChatPrompt.Response.TRY_AGAIN;
                                          }
                                       } catch (IOException ioe) {
                                          ioe.printStackTrace();
                                          CommandHelper.sendError(player, "Failed to create file: see console for errors B");
                                          return ChatPrompt.Response.TRY_AGAIN;
                                       }
                                    }
                                    MythicConfigImpl config = new MythicConfigImpl(input, file);
                                    config.load();
                                    config.set("Type", "ZOMBIE");
                                    config.set("Display", input);
                                    config.save();
                                    MobType newMob = new MobType(this.getPlugin().getMobManager(), pack, file, input, config);
                                    this.getPlugin().getMobManager().registerMob(input, newMob);
                                    CommandHelper.sendSuccess(player, "Created new mob " + input + "...");
                                    MobEditorMenuContext ctx = new MobEditorMenuContext(newMob, context);
                                    Schedulers.sync().run(() -> this.getPlugin().getMenuManager().getMobEditorMenu().open(player, ctx));
                                    return ChatPrompt.Response.ACCEPTED;
                                    }
                                    }
                                    }
                                    }
                     }).thenAcceptSync(in -> {
                        if (in.getResponse().equalsIgnoreCase("cancel")) {
                           context.openMenu(player);
                        }
                     });
                  }
               )
         );
      return builder;
   }
}
