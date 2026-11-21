package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command<MythicBukkit> {
   public InfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length == 0) {
         CommandHelper.sendError(sender, "Usage /mm m[obs] i[nfo] <internal-name>");
         return true;
      } else {
         if (this.getPlugin().getMobManager().getMythicMob(args[0]).isPresent()) {
            MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(args[0]).orElseGet(() -> null);
            sender.sendMessage(ColorString.get("&e&lInformation for &a" + mm.getInternalName() + "&7:"));
            sender.sendMessage(ChatColor.GOLD + "Mob ID: " + ChatColor.GRAY + mm.hashCode());
            sender.sendMessage(ChatColor.GOLD + "Display Name: " + ChatColor.GRAY + mm.getDisplayName());
            sender.sendMessage(ChatColor.GOLD + "EntityType: " + ChatColor.GRAY + mm.getEntityType());
            if (mm.getDamage() != null) {
               sender.sendMessage(ChatColor.GOLD + "Damage: " + ChatColor.GRAY + mm.getDamage().toString() + " (+" + mm.getPerLevelDamage() + " per level)");
            }

            if (mm.getHealth() != null) {
               sender.sendMessage(ChatColor.GOLD + "Health: " + ChatColor.GRAY + mm.getHealth() + " (+" + mm.getPerLevelHealth() + " per level)");
            }

            if (mm.getArmor() != null) {
               sender.sendMessage(ChatColor.GOLD + "Armor: " + ChatColor.GRAY + mm.getArmor().toString() + " (+" + mm.getPerLevelArmor() + " per level)");
            }

            sender.sendMessage(ChatColor.GOLD + "Power per Level: +" + ChatColor.GRAY + mm.getPerLevelPower());
            sender.sendMessage(ChatColor.GOLD + "Located in File: " + ChatColor.GRAY + mm.getFile());
         } else {
            CommandHelper.sendError(sender, "No Mythic Mob loaded with the name " + args[0]);
         }

         return true;
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
      return "mythicmobs.command.mobs.info";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "info";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"i"};
   }
}
