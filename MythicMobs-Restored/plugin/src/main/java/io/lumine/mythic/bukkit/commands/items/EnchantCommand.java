package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.events.MythicApplyEnchantEvent;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantCommand extends Command<MythicBukkit> {
   public EnchantCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args != null && args.length != 0) {
         String enchantment = args[0];
         int level = 1;
         if (args.length > 1) {
            try {
               level = Integer.valueOf(args[1]);
            } catch (Exception e) {
               CommandHelper.sendError(sender, "Level must be an integer.");
               return true;
            }
         }

         Player p = (Player)sender;
         ItemStack item = p.getInventory().getItemInMainHand();
         if (item != null && !item.getType().equals(Material.AIR)) {
            Enchantment en = Enchantment.getByName(enchantment);
            if (en == null) {
               CommandHelper.sendError(sender, "You must enter a valid enchantment name!");
               return true;
            } else {
               MythicApplyEnchantEvent event = new MythicApplyEnchantEvent(item, en, level);
               event = Events.callAndReturn(event);
               if (event.getWasHandled()) {
                  CommandHelper.sendSuccess(sender, "Applied enchantment to held item!");
                  return true;
               } else {
                  if (item.getType() != Material.ENCHANTED_BOOK) {
                     item.addUnsafeEnchantment(en, level);
                  } else {
                     EnchantmentStorageMeta esm = (EnchantmentStorageMeta)item.getItemMeta();
                     esm.addStoredEnchant(en, level, true);
                     item.setItemMeta(esm);
                  }

                  CommandHelper.sendSuccess(sender, "Applied enchantment to held item!");
                  return true;
               }
            }
         } else {
            CommandHelper.sendError(sender, "You must be holding a valid item!");
            return true;
         }
      } else {
         CommandHelper.sendError(sender, "Syntax: /mm item enchant <name> [level]");
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length != 1) {
         return null;
      } else {
         List<String> enchants = new ArrayList<>();

         for (Enchantment ench : Enchantment.values()) {
            enchants.add(ench.getName());
         }

         return CollectionUtils.filterMatches(args[0], enchants, 2);
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.enchant";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "enchant";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"ench", "e"};
   }
}
