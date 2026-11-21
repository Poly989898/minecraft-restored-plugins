package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.items.MythicItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command<MythicBukkit> {
   public InfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(args[0]);
      if (maybeItem.isPresent()) {
         MythicItem mi = maybeItem.get();
         sender.sendMessage(ChatColor.GOLD + "Stats for " + ChatColor.BOLD + ChatColor.GRAY + mi.getInternalName() + ":");
         sender.sendMessage(
            ChatColor.GOLD
               + "displayName: "
               + ChatColor.GRAY
               + mi.getDisplayName()
               + ChatColor.GRAY
               + " / "
               + ChatColor.translateAlternateColorCodes('&', mi.getDisplayName())
         );
         sender.sendMessage(
            ChatColor.GOLD
               + "Material: "
               + ChatColor.GRAY
               + mi.getMaterial().toString()
               + ChatColor.GRAY
               + " / "
               + ChatColor.GRAY
               + Material.getMaterial(mi.getMaterialName())
         );
         sender.sendMessage(ChatColor.GOLD + "Model: " + ChatColor.GRAY + mi.getCustomModelData());
         sender.sendMessage(ChatColor.GOLD + "Amount: " + ChatColor.GRAY + mi.getAmount());
         sender.sendMessage(ChatColor.GOLD + "Located in File: " + ChatColor.GRAY + mi.getFile());
      } else {
         CommandHelper.sendError(sender, "No Mythic Item loaded with the name " + args[0] + ".");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         List<String> itemNames = new ArrayList<>();

         for (MythicItem mythicItem : this.getPlugin().getItemManager().getItems()) {
            if (!mythicItem.isHidden()) {
               itemNames.add(mythicItem.getInternalName());
            }
         }

         return CollectionUtils.filterMatches(args[0], itemNames, 2);
      } else {
         return null;
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.info";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "info";
   }
}
