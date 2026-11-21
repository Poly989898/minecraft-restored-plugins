package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemovePinCommand extends Command<MythicBukkit> {
   public RemovePinCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optSilent = false;
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         if (args[0].contains("s")) {
            optSilent = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      Player player = (Player)sender;
      String packName = args[0];
      String nodeName = args[1];
      Optional<Pack> maybePack = this.getPlugin().getPackManager().getPack(packName);
      if (maybePack.isEmpty()) {
         return true;
      } else {
         Pack pack = maybePack.get();
         if (this.getPlugin().getPinManager().removePin(pack, nodeName)) {
            if (!optSilent) {
               CommandHelper.sendSuccess(sender, "Pin removed");
            }
         } else if (!optSilent) {
            CommandHelper.sendSuccess(sender, "Pin could not be removed");
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getPackManager().getPacks().stream().map(pack -> pack.getKey()).toList(), 2);
      } else {
         return args.length == 2
            ? CollectionUtils.filterMatches(
               args[1],
               this.getPlugin()
                  .getPinManager()
                  .getPins()
                  .values()
                  .stream()
                  .filter(pin -> pin.getPack().getPropertyNode().equalsIgnoreCase(args[1]))
                  .map(pin -> pin.getPropertyNode())
                  .toList(),
               2
            )
            : Collections.emptyList();
      }
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
      return "removepin";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"rp"};
   }
}
