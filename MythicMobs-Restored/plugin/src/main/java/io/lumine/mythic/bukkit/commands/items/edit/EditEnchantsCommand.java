package io.lumine.mythic.bukkit.commands.items.edit;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.items.MythicItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EditEnchantsCommand extends Command<MythicBukkit> {
   public EditEnchantsCommand(Command<MythicBukkit> parent) {
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
               this.printEnchantsEditor(sender, item);
               return true;
            } else {
               EditEnchantsCommand.EditAction action = EditEnchantsCommand.EditAction.valueOf(args[1]);
               if (action == EditEnchantsCommand.EditAction.CLEAR) {
                  item.setLore(Lists.newArrayList());
                  saveEnchants(item);
                  item.buildItemCache();
                  this.printEnchantsEditor(sender, item);
                  return true;
               } else if (action == EditEnchantsCommand.EditAction.ADD) {
                  CommandHelper.sendEditorMessage(
                     sender, "Type in the Enchantment you'd like to add.", "Format: [enchantment_type] [amount]", "Type cancel to abort editing."
                  );
                  ChatPrompt.listen((Player)sender, input -> {
                     if (input.equalsIgnoreCase("cancel")) {
                        return ChatPrompt.Response.ACCEPTED;
                     } else if (!item.addEnchantment(input)) {
                        CommandHelper.sendError(sender, "Invalid enchantment supplied");
                        return ChatPrompt.Response.TRY_AGAIN;
                     } else {
                        saveEnchants(item);
                        return ChatPrompt.Response.ACCEPTED;
                     }
                  }).thenAcceptSync(in -> {
                     item.buildItemCache();
                     this.printEnchantsEditor(sender, item);
                  });
                  return true;
               } else {
                  Enchantment enchant = Enchantment.getByName(args[2]);
                  if (enchant == null) {
                     CommandHelper.sendError(sender, "Invalid Enchantment");
                     this.printEnchantsEditor(sender, item);
                     return true;
                  } else {
                     switch (action) {
                        case REMOVE:
                           item.getEnchantments().remove(enchant);
                        default:
                           saveEnchants(item);
                           item.buildItemCache();
                           this.printEnchantsEditor(sender, item);
                           return true;
                     }
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
      return "enchants";
   }

   public static void saveEnchants(MythicItem item) {
      ArrayList<Object> saveList = Lists.newArrayList();

      for (Entry<Enchantment, PlaceholderInt> entry : item.getEnchantments().entrySet()) {
         saveList.add(entry.getKey().getName() + " " + entry.getValue().toString());
      }

      item.getConfig().setSave("Enchantments", saveList);
   }

   public void printEnchantsEditor(CommandSender sender, MythicItem item) {
      String n = item.getInternalName();
      CommandHelper.sendEditorMessage(sender, "<yellow>Editing Item Enchantments for: <aqua>" + item.getInternalName());
      Text.sendMessage(sender, "<gray><bold>--------------------</bold>");

      for (Entry<Enchantment, PlaceholderInt> entry : item.getEnchantments().entrySet()) {
         String message = "";
         message = message + "<red><click:run_command:/mm i edit enchants " + n + " REMOVE " + entry.getKey().getName() + ">\u2715</click> ";
         message = message + "<gray><bold>\u2507</bold> ";
         message = message + "<white>" + entry.getKey().getName() + " " + entry.getValue().toString();
         Text.sendMessage(sender, message);
      }

      Text.sendMessage(
         sender,
         "<gray>[<green><bold><click:run_command:/mm i edit enchants "
            + n
            + " ADD>+</click></bold><gray>] <gray>[<gold><click:run_command:/mm i edit "
            + n
            + ">Finished Editing</click><gray>]"
      );
   }

   private static enum EditAction {
      CLEAR,
      ADD,
      REMOVE;
   }
}
