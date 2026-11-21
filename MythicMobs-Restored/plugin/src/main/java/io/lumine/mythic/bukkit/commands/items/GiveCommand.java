package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.items.MythicItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends Command<MythicBukkit> {
   private static final BooleanProp GIVEITEMFEEDBACK = Property.Boolean(Scope.CONFIG, "Configuration.General.SendGiveItemFeedback", true);

   public GiveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optSilent = !GIVEITEMFEEDBACK.get();
      boolean optDropIfFull = false;
      boolean optAsTable = false;
      if (args != null && args.length > 2 && args[0].startsWith("-")) {
         if (args[0].contains("s")) {
            optSilent = true;
         }

         if (args[0].contains("d")) {
            optDropIfFull = true;
         }

         if (args[0].contains("t")) {
            optAsTable = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      if (args.length < 2) {
         if (!optSilent) {
            CommandHelper.sendError(sender, "Command Syntax: /mm item give <name> <item>");
         }

         return true;
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         String item = args[1];
         int amount = 1;
         if (args.length > 2) {
            amount = Integer.valueOf(args[2]);
         }

         if (target == null) {
            if (!optSilent) {
               CommandHelper.sendError(sender, "That player is not online");
            }

            return true;
         } else {
            AbstractPlayer atarget = BukkitAdapter.adapt(target);
            DropMetadata meta = new DropMetadataImpl(new GenericCaster(atarget), atarget);
            Optional<DropTable> maybeDrops = MythicBukkit.inst().getDropManager().getDropTable(item);
            if (maybeDrops.isPresent()) {
               DropTable dt = maybeDrops.get();
               if (dt.hasDrops()) {
                  for (int i = 0; i < amount; i++) {
                     LootBag loot = dt.generate(meta);
                     loot.give(atarget, optSilent);
                  }
               }

               Events.call(new MythicModifiedInventoryEvent(null, target));
               BukkitAdapter.adapt(atarget).updateInventory();
               if (!optSilent) {
                  CommandHelper.sendSuccess(sender, "DropTable &b" + item + " &awas put in your inventory!");
               }

               return true;
            } else {
               Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(item);
               if (maybeItem.isPresent()) {
                  MythicItem mi = maybeItem.get();

                  for (int i = 0; i < amount; i++) {
                     ItemStack theItem;
                     if (optAsTable) {
                        theItem = BukkitAdapter.adapt(this.getPlugin().getBootstrap().createItem(item));
                     } else {
                        theItem = BukkitAdapter.adapt(mi.generateItemStack(meta, 1));
                     }

                     HashMap<Integer, ItemStack> full = target.getInventory().addItem(new ItemStack[]{theItem});
                     if (optDropIfFull && !full.isEmpty()) {
                        for (ItemStack dropItem : full.values()) {
                           target.getWorld().dropItemNaturally(target.getLocation(), dropItem, droppedItem -> {
                              droppedItem.setCanMobPickup(false);
                              droppedItem.setOwner(target.getUniqueId());
                              droppedItem.setThrower(target.getUniqueId());
                           });
                        }
                     }
                  }

                  Events.call(new MythicModifiedInventoryEvent(null, target));
                  target.updateInventory();
                  if (!optSilent) {
                     target.sendMessage(
                        ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "Item " + ChatColor.AQUA + item + ChatColor.GREEN + " was put in your inventory!"
                     );
                  }
               } else if (!optSilent) {
                  sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "No Mythic Item loaded with the name " + item + ".");
               }

               return true;
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         List<String> players = new ArrayList<>();

         for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getName());
         }

         return CollectionUtils.filterMatches(args[0], players, 2);
      } else if (args.length != 2) {
         return null;
      } else {
         List<String> itemNames = new ArrayList<>();

         for (MythicItem mythicItem : this.getPlugin().getItemManager().getItems()) {
            if (!mythicItem.isHidden()) {
               itemNames.add(mythicItem.getInternalName());
            }
         }

         for (DropTable s : this.getPlugin().getDropManager().getDropTables()) {
            itemNames.add(s.getInternalName());
         }

         return CollectionUtils.filterMatches(args[1], itemNames, 2);
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.give";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "give";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"g", "gi"};
   }
}
