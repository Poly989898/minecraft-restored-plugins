package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.bukkit.utils.worldedit.Regions;
import io.lumine.mythic.core.skills.pins.PinRegion;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedefineRegionCommand extends Command<MythicBukkit> {
   public RedefineRegionCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      String nodeName = args[0];
      Optional<PinRegion> maybeRegion = this.getPlugin().getPinManager().getRegion(nodeName);
      if (maybeRegion.isEmpty()) {
         CommandHelper.sendError(sender, "Region not found");
         return true;
      } else {
         PinRegion region = maybeRegion.get();
         Optional<Region> maybeSelectedRegion = Regions.getPlayerSelection(player);
         if (maybeSelectedRegion.isEmpty()) {
            CommandHelper.sendError(sender, "No region selected. Use WorldEdit wand to select.");
            return true;
         } else {
            region.setRegion(maybeSelectedRegion.get());
            region.save();
            this.getPlugin().getPinManager().generateGridIndex();
            CommandHelper.sendSuccess(sender, "Region redefined");
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[1],
            this.getPlugin()
               .getPinManager()
               .getRegions()
               .values()
               .stream()
               .filter(pin -> pin.getPack().getPropertyNode().equalsIgnoreCase(args[0]))
               .map(pin -> pin.getPropertyNode())
               .toList(),
            2
         )
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
      return "redefineregion";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"rdr"};
   }
}
