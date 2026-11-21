package io.lumine.mythic.bukkit.commands.pins;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.bukkit.utils.worldedit.Regions;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddRegionCommand extends Command<MythicBukkit> {
   public AddRegionCommand(MythicBukkit parent) {
      super(parent);
   }

   public AddRegionCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      String packName = args[0];
      String nodeName = args[1];
      Collection<String> tags = Sets.newConcurrentHashSet();
      if (args.length > 2) {
         for (String tag : args[2].split(",")) {
            tags.add(tag.trim());
         }
      }

      Optional<Pack> maybePack = this.getPlugin().getPackManager().getPack(packName);
      if (maybePack.isEmpty()) {
         return true;
      } else {
         Pack pack = maybePack.get();
         Optional<Region> maybeRegion = Regions.getPlayerSelection(player);
         if (maybeRegion.isEmpty()) {
            CommandHelper.sendError(sender, "No region selected. Use WorldEdit wand to select.");
            return true;
         } else {
            this.getPlugin().getPinManager().createRegion(pack, nodeName, maybeRegion.get(), tags);
            CommandHelper.sendSuccess(
               sender,
               "Created Region <gold><click:copy_to_clipboard:"
                  + nodeName
                  + ">"
                  + nodeName
                  + " <gray>[<yellow><click:copy_to_clipboard:@Pin{pin="
                  + nodeName
                  + "}>Targeter<gray>]"
            );
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
      return "createregion";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"addregion", "cr"};
   }
}
