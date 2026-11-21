package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.commands.items.edit.ItemEditCommand;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythiccrucible.items.CrucibleItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import org.bukkit.Material;

public class EditStatsButton extends ItemEditorButton {
   public EditStatsButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.ENDER_EYE)
         .name("<green>Edit Stats")
         .lore(context -> {
            List<String> lore = Lists.newArrayList();
            if (MythicBukkit.inst().getCompatibility().getCrucible().isEmpty()) {
               lore.add("<red>[Requires MythicCrucible Add-on]");
               lore.add("<red> ");
            }

            lore.add("<white>Left-click add a stat");
            lore.add("<white>Right-click to edit stats");
            lore.add("<red>");
            lore.add("<yellow>Current:");
            lore.add("<red>");
            MythicItem item = context.getItem();
            CrucibleItem crucibleItem = MythicBukkit.inst().getCompatibility().getCrucible().get().getCrucibleItem(item.getInternalName()).get();
            lore.addAll(getItemStats(crucibleItem));
            return lore;
         })
         .click(
            (context, player) -> {
               if (MythicBukkit.inst().getCompatibility().getCrucible().isEmpty()) {
                  CommandHelper.sendError(player, "This feature requires the MythicCrucible add-on");
                  player.playSound(player, "entity.villager.no", 1.0F, 1.0F);
               } else {
                  CommandHelper.sendEditorMessage(
                     player,
                     "Type in the stat you'd like to add.",
                     "Format: [amount] [stat_name] <ADDITIVE/MULTIPLY/COMPOUND>",
                     "Type <yellow>cancel to abort editing or <yellow>list to list stats."
                  );
                  player.closeInventory();
                  ChatPrompt.listen(player, input -> {
                     if (input.equalsIgnoreCase("cancel")) {
                        return ChatPrompt.Response.ACCEPTED;
                     } else if (input.equalsIgnoreCase("list")) {
                        ArrayList<String> sortedStats = Lists.newArrayList(MythicBukkit.inst().getStatManager().getStats().keySet());
                        Collections.sort(sortedStats);

                        for (String stat : sortedStats) {
                           CommandHelper.send(player, "\u27f6 " + stat);
                        }

                        return ChatPrompt.Response.TRY_AGAIN;
                     } else {
                        MythicItem item = context.getItem();
                        CrucibleItem crucibleItem = MythicBukkit.inst().getCompatibility().getCrucible().get().getCrucibleItem(item.getInternalName()).get();
                        String[] split = input.split(" ");

                        try {
                           PlaceholderDouble amount = PlaceholderDouble.of(split[0]);
                           Optional<StatType> maybeStat = MythicBukkit.inst().getStatManager().getStat(split[1]);
                           if (maybeStat.isEmpty()) {
                              CommandHelper.sendError(player, "Stat '" + split[1] + "' not found. Make sure it's registered in stats.yml!");
                              return ChatPrompt.Response.TRY_AGAIN;
                           }

                           StatType stat = maybeStat.get();
                           StatModifierType modifierType = StatModifierType.ADDITIVE;
                           if (split.length > 2) {
                              modifierType = StatModifierType.get(split[2]);
                           }

                           Map<StatModifierType, PlaceholderDouble> statMap = crucibleItem.getStats().computeIfAbsent(stat.getKey(), s -> Maps.newHashMap());
                           statMap.put(modifierType, amount);
                           saveItemStats(crucibleItem);
                        } catch (Throwable e) {
                           CommandHelper.sendError(player, "Format: [amount] [stat_name] <ADDITIVE/MULTIPLICATIVE/COMPOUND>");
                           return ChatPrompt.Response.TRY_AGAIN;
                        }

                        return ChatPrompt.Response.ACCEPTED;
                     }
                  }).thenAcceptSync(in -> context.openMenu(player));
               }
            }
         )
         .rightClick((context, player) -> {
            ItemEditCommand.lastEdited.put(player.getUniqueId(), context);
            player.performCommand("mm i edit stats " + context.getItem().getInternalName());
            player.closeInventory();
         })
         .build();
   }

   public static List<String> getItemStats(CrucibleItem item) {
      List<String> saveList = Lists.newArrayList();

      for (Entry<String, Map<StatModifierType, PlaceholderDouble>> entry : item.getStats().entrySet()) {
         String stat = entry.getKey().toUpperCase();
         Map<StatModifierType, PlaceholderDouble> valueMap = entry.getValue();

         for (Entry<StatModifierType, PlaceholderDouble> opEntry : valueMap.entrySet()) {
            StatModifierType operation = opEntry.getKey();
            String value = opEntry.getValue().serialize();
            saveList.add(value + " " + stat + " " + operation);
         }
      }

      return saveList;
   }

   public static void saveItemStats(CrucibleItem item) {
      List<String> saveList = getItemStats(item);
      item.getConfig().setSave("Stats", saveList);
      item.getMythicItem().buildItemCache();
   }
}
