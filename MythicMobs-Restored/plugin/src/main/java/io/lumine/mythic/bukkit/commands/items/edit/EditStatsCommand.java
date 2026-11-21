package io.lumine.mythic.bukkit.commands.items.edit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.editor.EditStatsButton;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythiccrucible.items.CrucibleItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditStatsCommand extends Command<MythicBukkit> {
   public EditStatsCommand(Command<MythicBukkit> parent) {
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
               this.printEditor(sender, item);
               return true;
            } else {
               EditStatsCommand.EditAction action = EditStatsCommand.EditAction.valueOf(args[1]);
               if (action == EditStatsCommand.EditAction.ADD) {
                  CommandHelper.sendEditorMessage(sender, "Type in the stat you'd like to add.", "Type cancel to abort editing.");
                  ChatPrompt.listen(
                        (Player)sender,
                        input -> {
                           if (input.equalsIgnoreCase("cancel")) {
                              return ChatPrompt.Response.ACCEPTED;
                           } else if (input.equalsIgnoreCase("list")) {
                              ArrayList<String> sortedStats = Lists.newArrayList(MythicBukkit.inst().getStatManager().getStats().keySet());
                              Collections.sort(sortedStats);

                              for (String statx : sortedStats) {
                                 CommandHelper.send(sender, "\u27f6 " + statx);
                              }

                              return ChatPrompt.Response.TRY_AGAIN;
                           } else {
                              CrucibleItem crucibleItemx = MythicBukkit.inst()
                                 .getCompatibility()
                                 .getCrucible()
                                 .get()
                                 .getCrucibleItem(item.getInternalName())
                                 .get();
                              String[] splitx = input.split(" ");

                              try {
                                 PlaceholderDouble amount = PlaceholderDouble.of(splitx[0]);
                                 Optional<StatType> maybeStatx = MythicBukkit.inst().getStatManager().getStat(splitx[1]);
                                 if (maybeStatx.isEmpty()) {
                                    CommandHelper.sendError(sender, "Stat '" + splitx[1] + "' not found. Make sure it's registered in stats.yml!");
                                    return ChatPrompt.Response.TRY_AGAIN;
                                 }

                                 StatType statxx = maybeStatx.get();
                                 StatModifierType modifierTypex = StatModifierType.ADDITIVE;
                                 if (splitx.length > 2) {
                                    modifierTypex = StatModifierType.get(splitx[2]);
                                 }

                                 Map<StatModifierType, PlaceholderDouble> statMapx = crucibleItemx.getStats()
                                    .computeIfAbsent(statxx.getKey(), s -> Maps.newHashMap());
                                 statMapx.put(modifierTypex, amount);
                                 EditStatsButton.saveItemStats(crucibleItemx);
                              } catch (Throwable var10x) {
                                 CommandHelper.sendError(sender, "Format: [amount] [stat_name] <ADDITIVE/MULTIPLICATIVE/COMPOUND>");
                                 return ChatPrompt.Response.TRY_AGAIN;
                              }

                              return ChatPrompt.Response.ACCEPTED;
                           }
                        }
                     )
                     .thenAcceptSync(in -> this.printEditor(sender, item));
                  return true;
               } else {
                  OptionalInt maybeLine = Numbers.parseInteger(args[2]);
                  if (maybeLine.isEmpty()) {
                     CommandHelper.sendError(sender, "Invalid index");
                     this.printEditor(sender, item);
                     return true;
                  } else {
                     String n = item.getInternalName();
                     CrucibleItem crucibleItem = MythicBukkit.inst().getCompatibility().getCrucible().get().getCrucibleItem(n).get();
                     switch (action) {
                        case CLEAR:
                           crucibleItem.getStats().clear();
                           EditStatsButton.saveItemStats(crucibleItem);
                           break;
                        case REMOVE:
                           List<String> itemStats = EditStatsButton.getItemStats(crucibleItem);
                           String line = itemStats.get(maybeLine.getAsInt());
                           String[] split = line.split(" ");
                           Optional<StatType> maybeStat = MythicBukkit.inst().getStatManager().getStat(split[1]);
                           StatType stat = maybeStat.get();
                           StatModifierType modifierType = StatModifierType.ADDITIVE;
                           if (split.length > 2) {
                              modifierType = StatModifierType.get(split[2]);
                           }

                           Map<StatModifierType, PlaceholderDouble> statMap = crucibleItem.getStats()
                              .get(stat.getKey());
                           statMap.remove(modifierType);
                           EditStatsButton.saveItemStats(crucibleItem);
                     }

                     this.printEditor(sender, item);
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
      return "stats";
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

   public void printEditor(CommandSender sender, MythicItem item) {
      String n = item.getInternalName();
      CrucibleItem crucibleItem = MythicBukkit.inst().getCompatibility().getCrucible().get().getCrucibleItem(n).get();
      List<String> itemStats = EditStatsButton.getItemStats(crucibleItem);
      CommandHelper.sendEditorMessage(sender, "<yellow>Editing Item Stats for: <aqua>" + item.getInternalName());
      Text.sendMessage(sender, "<gray><bold>--------------------</bold>");

      for (int i = 0; i < itemStats.size(); i++) {
         String message = "";
         message = message + "<red><click:run_command:/mm i edit stats " + n + " REMOVE " + i + ">\u2715</click> ";
         message = message + "<white>" + itemStats.get(i);
         Text.sendMessage(sender, message);
      }

      Text.sendMessage(
         sender,
         "<gray>[<green><bold><click:run_command:/mm i edit stats "
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
