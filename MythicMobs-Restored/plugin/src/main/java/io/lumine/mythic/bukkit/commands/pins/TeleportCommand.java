package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.pins.Pin;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends Command<MythicBukkit> {
   public TeleportCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      String nodeName = args[0];
      Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(nodeName);
      if (maybePin.isEmpty()) {
         CommandHelper.sendError(sender, "Pin not found.");
         return true;
      } else {
         Pin pin = maybePin.get();
         AbstractLocation aLocation = pin.getLocations().get(0);
         Location location = BukkitAdapter.adapt(aLocation);
         if (ServerVersion.isFolia()) {
            player.teleportAsync(location);
         } else {
            player.teleport(location);
         }

         player.playSound(location, "block.respawn_anchor.deplete", 1.0F, 2.0F);
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getPinManager().getPins().keySet().stream().toList(), 2)
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
      return "warp";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"teleport", "tp"};
   }
}
