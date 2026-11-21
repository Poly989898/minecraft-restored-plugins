package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.BukkitAdapter;
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

public class AddPinCommand extends Command<MythicBukkit> {
   public AddPinCommand(MythicBukkit parent) {
      super(parent);
   }

   public AddPinCommand(Command<MythicBukkit> parent) {
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
         AbstractLocation location = BukkitAdapter.adapt(player.getLocation());
         this.getPlugin().getPinManager().createPin(pack, nodeName, location);
         if (!optSilent) {
            CommandHelper.sendSuccess(
               sender,
               "Created Pin <gold><click:copy_to_clipboard:"
                  + nodeName
                  + ">"
                  + nodeName
                  + " <gray>[<yellow><click:copy_to_clipboard:@Pin{pin="
                  + nodeName
                  + "}>Targeter<gray>]"
            );
         }

         return true;
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
      return "createpin";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"addpin", "cp"};
   }
}
