package io.lumine.mythic.core.menus.items;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.ItemGroup;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;

public class ItemBrowseMenu extends MythicMenu<ItemMenuContext> {
   public ItemBrowseMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-browser.yml");
   }

   public void open(Player player) {
      this.open(player, new ItemMenuContext());
   }

   public void open(Player player, ItemMenuContext context) {
      if (context.getMode() == ItemBrowseMode.PACKS && context.getPackFilter() == null) {
         List<MenuData<ItemMenuContext>> data = Lists.newArrayList();

         for (Pack pack : this.getPlugin().getPackManager().getPacks()) {
            if (pack.getItemCount() != 0) {
               data.add(() -> pack.createMenuIcon((pContext, pPlayer) -> {
                  pContext.setPackFilter(pack);
                  this.open(pPlayer, pContext);
               }));
            }
         }

         this.getPlugin().getMenuManager().getItemBrowseMenu().open(player, context, data);
      } else if (context.getMode() == ItemBrowseMode.GROUPS && context.getGroupFilter() == null) {
         List<MenuData<ItemMenuContext>> data = Lists.newArrayList();

         for (ItemGroup group : this.getPlugin().getItemManager().getItemGroups()) {
            data.add(() -> IconBuilder.<ItemMenuContext>create().item(group.getMenuItem()).name(group.getName()).click((pContext, pPlayer) -> {
               pContext.setGroupFilter(group);
               this.open(pPlayer, pContext);
            }).build());
         }

         this.getPlugin().getMenuManager().getItemBrowseMenu().open(player, context, data);
      } else {
         List<MythicItem> items = Lists.newArrayList(this.getPlugin().getItemManager().getItems());
         items.removeIf(MythicItem::isHidden);
         if (context.hasPackFilter()) {
            items.removeIf(mi -> mi.getPack() == null || mi.getPack() != context.getPackFilter());
         } else if (context.hasGroupFilter()) {
            items.removeIf(mi -> mi.getGroup() == null || !mi.getGroup().equalsIgnoreCase(context.getGroupFilter().getId()));
         }

         if (context.hasFilter()) {
            this.getPlugin().getItemManager().filterItems(items, context.getFilter());
         }

         items.sort(Comparator.comparing(MythicItem::getInternalName, String.CASE_INSENSITIVE_ORDER));
         this.getPlugin().getMenuManager().getItemBrowseMenu().open(player, context, items);
      }
   }

   @Override
   public EditableMenuBuilder<ItemMenuContext> build(EditableMenuBuilder<ItemMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         if (context.getMode() == ItemBrowseMode.PACKS && context.hasPackFilter()) {
            context.clearPackFilter();
            this.open(player, context);
         } else if (context.getMode() == ItemBrowseMode.GROUPS && context.hasGroupFilter()) {
            context.clearGroupFilter();
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
                  context.clearGroupFilter();
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
                        "Type in the item name in one of the following ways:",
                        "<yellow>- &6[item_name]",
                        "<yellow>- &3<pack>&7.&6[item_name] &3<path_to_file>",
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
                                 File file = new File(pack.getFolder(), "Items/" + filePath);
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
                                 MythicItem newItem = new MythicItem(pack, file, input);
                                 this.getPlugin().getItemManager().registerItem(input, newItem);
                                 CommandHelper.sendSuccess(player, "Created new item " + input + "...");
                                 ItemEditorMenuContext ctx = new ItemEditorMenuContext(newItem, context);
                                 Schedulers.sync().run(() -> this.getPlugin().getMenuManager().getItemEditorMenu().open(player, ctx));
                                 return ChatPrompt.Response.ACCEPTED;
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
