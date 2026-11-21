package io.lumine.mythic.bukkit.commands.items.edit;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.items.MythicItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditLoreCommand extends Command<MythicBukkit> {
   public EditLoreCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires Mythic Premium");
         return true;
      } else {
         String itemName = args[0];
         Optional<MythicItem> maybeItem = this.getPlugin().getItemManager().getItem(itemName);
         if (maybeItem.isEmpty()) {
            CommandHelper.sendError(sender, "Invalid Item Name");
            return true;
         } else {
            MythicItem item = maybeItem.get();
            if (args.length == 1) {
               this.printLoreEditor(sender, item);
               return true;
            } else {
               EditLoreCommand.EditAction action = EditLoreCommand.EditAction.valueOf(args[1]);
               if (action == EditLoreCommand.EditAction.CLEAR) {
                  item.setLore(Lists.newArrayList());
                  this.saveLore(item);
                  this.printLoreEditor(sender, item);
                  return true;
               } else if (action == EditLoreCommand.EditAction.ADD_LINE) {
                  CommandHelper.sendEditorMessage(sender, "Type in the Lore line you'd like to add.", "Type cancel to abort editing.");
                  ChatPrompt.listen((Player)sender, input -> {
                     if (input.equalsIgnoreCase("cancel")) {
                        return ChatPrompt.Response.ACCEPTED;
                     } else {
                        List<PlaceholderString> lore = item.getLoreRaw();
                        lore.add(PlaceholderString.of(input));
                        item.setLore(lore);
                        this.saveLore(item);
                        return ChatPrompt.Response.ACCEPTED;
                     }
                  }).thenAcceptSync(in -> this.printLoreEditor(sender, item));
                  return true;
               } else {
                  OptionalInt maybeLine = Numbers.parseInteger(args[2]);
                  if (maybeLine.isEmpty()) {
                     CommandHelper.sendError(sender, "Invalid Line Number");
                     this.printLoreEditor(sender, item);
                     return true;
                  } else {
                     int line = maybeLine.getAsInt();
                     List<PlaceholderString> itemLore = item.getLoreRaw();
                     switch (action) {
                        case REMOVE_LINE:
                           itemLore.remove(line);
                           item.setLore(itemLore);
                           break;
                        case MOVE_LINE_UP:
                           if (line != 0) {
                              Collections.swap(itemLore, line, line - 1);
                              item.setLore(itemLore);
                           }
                           break;
                        case MOVE_LINE_DOWN:
                           if (line != itemLore.size() - 1) {
                              Collections.swap(itemLore, line, line + 1);
                              item.setLore(itemLore);
                           }
                     }

                     this.saveLore(item);
                     this.printLoreEditor(sender, item);
                     return true;
                  }
               }
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.edit";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "lore";
   }

   private void saveLore(MythicItem item) {
      List<PlaceholderString> lore = item.getLoreRaw();
      ArrayList<Object> saveList = Lists.newArrayList();

      for (PlaceholderString pl : lore) {
         saveList.add(pl.toString());
      }

      item.getConfig().setSave("Lore", saveList);
      item.buildItemCache();
   }

   public void printLoreEditor(CommandSender sender, MythicItem item) {
      String n = item.getInternalName();
      List<String> itemLore = item.getLore();
      CommandHelper.sendEditorMessage(sender, "<yellow>Editing Item Lore for: <aqua>" + item.getInternalName());
      Text.sendMessage(sender, "<gray><bold>--------------------</bold>");

      for (int i = 0; i < itemLore.size(); i++) {
         String message = "";
         message = message + "<red><click:run_command:/mm i edit lore " + n + " REMOVE_LINE " + i + ">\u2715</click> ";
         message = message + "<gray><gold><bold><click:run_command:/mm i edit lore " + n + " MOVE_LINE_UP " + i + ">\u25b2</click>";
         message = message + "<click:run_command:/mm i edit lore " + n + " MOVE_LINE_DOWN " + i + ">\u25bc</click></bold> <gray><bold>\u2507</bold> ";
         message = message + "<white>" + itemLore.get(i);
         Text.sendMessage(sender, message);
      }

      Text.sendMessage(
         sender,
         "<gray>[<green><bold><click:run_command:/mm i edit lore "
            + n
            + " ADD_LINE>+</click></bold><gray>] <gray>[<gold><click:run_command:/mm i edit "
            + n
            + ">Finished Editing</click><gray>]"
      );
   }

   private static enum EditAction {
      CLEAR,
      ADD_LINE,
      REMOVE_LINE,
      MOVE_LINE_UP,
      MOVE_LINE_DOWN;
   }
}
