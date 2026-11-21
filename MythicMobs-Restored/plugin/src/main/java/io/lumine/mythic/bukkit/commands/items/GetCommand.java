package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.config.MythicLang;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetCommand extends Command<MythicBukkit> {
   public GetCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      if (args.length < 1) {
         CommandHelper.sendError(sender, "Command Syntax: /mm item get <name>");
         return true;
      } else {
         String item = args[0];
         int amount = 1;
         if (args.length > 1) {
            amount = Integer.valueOf(args[1]);
         }

         AbstractPlayer target = BukkitAdapter.adapt(p);
         SkillCaster caster = this.getPlugin().getSkillManager().getCaster(target);
         DropMetadataImpl meta = new DropMetadataImpl(caster, target);
         Optional<DropTable> maybeDrops = this.getPlugin().getDropManager().getDropTable(item);
         if (maybeDrops.isPresent()) {
            DropTable dt = maybeDrops.get();
            if (dt.hasDrops()) {
               for (int i = 0; i < amount; i++) {
                  LootBag loot = dt.generate(meta);
                  loot.give(target);
               }
            }

            Events.call(new MythicModifiedInventoryEvent(null, p));
            BukkitAdapter.adapt(target).updateInventory();
            CommandHelper.sendSuccess(sender, "DropTable &b" + item + " &awas put in your inventory!");
            return true;
         } else {
            Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(item);
            if (maybeItem.isPresent()) {
               MythicItem mi = maybeItem.get();

               for (int i = 0; i < amount; i++) {
                  try {
                     p.getInventory().addItem(new ItemStack[]{BukkitAdapter.adapt(mi.generateItemStack(meta, 1))});
                  } catch (Exception e) {
                     CommandHelper.sendError(sender, "Could not get item: item is improperly configured and returned null.");
                     MythicLogger.handleMinorError(e);
                     return true;
                  }
               }

               Events.call(new MythicModifiedInventoryEvent(null, p));
               p.updateInventory();
               MythicLang.sendSuccess(sender, "command.item.get.success", "Item <aqua>{0} <green>was put in your inventory!", item);
            } else {
               MythicLang.sendError(sender, "command.item.get.error.not-found", "No Mythic Item loaded with the name {0}.", item);
            }

            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length != 1) {
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

         return CollectionUtils.filterMatches(args[0], itemNames, 2);
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.get";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "get";
   }
}
