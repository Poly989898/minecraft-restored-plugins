package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandCommand extends Command<MythicBukkit> {
   public WandCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 2) {
         CommandHelper.sendError(sender, "Command Syntax: /mm pins wand <pack> <pin>");
         return true;
      } else {
         Player player = (Player)sender;
         String packName = args[0];
         String nodeName = args[1];
         Optional<Pack> maybePack = this.getPlugin().getPackManager().getPack(packName);
         if (maybePack.isEmpty()) {
            CommandHelper.sendError(sender, "Pack <white>" + packName + " <gray>not found.");
            return true;
         } else {
            Pack pack = maybePack.get();
            ItemStack item = this.getPlugin().getPinManager().createWand(pack, nodeName);
            player.getInventory().addItem(new ItemStack[]{item});
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getPackManager().getPacks().stream().map(pack -> pack.getKey()).toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "wand";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"w"};
   }
}
