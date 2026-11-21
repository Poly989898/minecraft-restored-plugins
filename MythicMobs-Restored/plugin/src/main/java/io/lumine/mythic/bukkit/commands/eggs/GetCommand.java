package io.lumine.mythic.bukkit.commands.eggs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
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
         CommandHelper.sendError(sender, "Command Syntax: /mm egg get <mob_name> [amount]");
         return true;
      } else {
         MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(args[0]).orElseGet(() -> null);
         int amount = 1;
         if (args.length > 1) {
            amount = Integer.valueOf(args[1]);
         }

         if (mm == null) {
            CommandHelper.sendError(sender, "No Mythic Mob loaded with the name " + args[0] + ".");
            return true;
         } else {
            BukkitItemStack egg = this.getPlugin().getMobManager().getEggManager().createMythicEgg(mm);
            ItemStack bukkitEgg = egg.amount(amount).getItemStack();
            if (p.getInventory().addItem(new ItemStack[]{bukkitEgg}).isEmpty()) {
               CommandHelper.sendSuccess(sender, "Got " + ChatColor.AQUA + amount + ChatColor.GREEN + " Mythic Eggs" + ChatColor.GREEN + "!");
            } else {
               CommandHelper.sendError(sender, "Could not get egg: inventory is full.");
            }

            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(), 2
         )
         : null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.eggs.get";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "get";
   }
}
