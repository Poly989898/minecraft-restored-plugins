package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.PlayerVariableRegistry;
import io.lumine.mythic.core.skills.variables.Variable;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetBlockCoordinatesCommand extends Command<MythicBukkit> {
   public GetBlockCoordinatesCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      AbstractPlayer player = BukkitAdapter.adapt(p);
      PlayerData profile = this.getPlugin().getPlayerManager().getProfile(p);
      PlayerVariableRegistry variables = profile.getVariables();
      Variable lastCheckedLocation = variables.get("util-last-checked-coordinate");
      CommandHelper.sendHeader(sender);
      AbstractLocation location = player.getLocation();
      int bx = location.getBlockX();
      int by = location.getBlockY();
      int bz = location.getBlockZ();
      double cx = location.center().getX();
      double cy = location.center().getY();
      double cz = location.center().getZ();
      double ex = location.getX();
      double ey = location.getY();
      double ez = location.getZ();
      String bString = bx + "," + by + "," + bz;
      String cString = cx + "," + cy + "," + cz;
      String eString = ex + "," + ey + "," + ez;
      StringBuilder outputSelf = new StringBuilder()
         .append("<gray>[<yellow>")
         .append("<click:copy_to_clipboard:")
         .append(bString)
         .append(">Block</click>")
         .append("<gray>]")
         .append("<gray>[<yellow>")
         .append("<click:copy_to_clipboard:")
         .append(cString)
         .append(">Center</click>")
         .append("<gray>]")
         .append("<gray>[<yellow>")
         .append("<click:copy_to_clipboard:")
         .append(eString)
         .append(">Exact</click>")
         .append("<gray>]")
         .append("<gray> <gold>Coordinates<gray> >> ")
         .append("<aqua>")
         .append(bx)
         .append(",")
         .append(by)
         .append(",")
         .append(bz);
      CommandHelper.send(sender, outputSelf.toString());
      variables.putLocation("util-last-checked-coordinate", location);
      Location bukkitLocation = p.getTargetBlock((HashSet)null, 64).getLocation();
      if (bukkitLocation == null) {
         CommandHelper.sendError(sender, "You must target a valid block!");
         return true;
      } else {
         AbstractLocation locationx = BukkitAdapter.adapt(bukkitLocation);
         by = locationx.getBlockX();
         bz = locationx.getBlockY();
         int bzx = locationx.getBlockZ();
         double cxx = locationx.center().getX();
         double cyx = locationx.center().getY();
         double czx = locationx.center().getZ();
         double exx = locationx.getX();
         double eyx = locationx.getY();
         double ezx = locationx.getZ();
         cString = by + "," + bz + "," + bzx;
         eString = cxx + "," + cyx + "," + czx;
         String eStringx = exx + "," + eyx + "," + ezx;
         StringBuilder outputSelfx = new StringBuilder()
            .append("<gray>[<yellow>")
            .append("<click:copy_to_clipboard:")
            .append(cString)
            .append(">Block</click>")
            .append("<gray>]")
            .append("<gray>[<yellow>")
            .append("<click:copy_to_clipboard:")
            .append(eString)
            .append(">Center</click>")
            .append("<gray>]")
            .append("<gray>[<yellow>")
            .append("<click:copy_to_clipboard:")
            .append(eStringx)
            .append(">Exact</click>")
            .append("<gray>]")
            .append("<gray> <gold>Target <gray>>> <aqua>")
            .append(by)
            .append(",")
            .append(bz)
            .append(",")
            .append(bzx);
         CommandHelper.send(sender, outputSelfx.toString());
         if (lastCheckedLocation != null) {
            location = (AbstractLocation)lastCheckedLocation.get();
            bx = location.getBlockX();
            by = location.getBlockY();
            bz = location.getBlockZ();
            cx = location.center().getX();
            cy = location.center().getY();
            cz = location.center().getZ();
            int ox = location.getBlockX() - player.getLocation().getBlockX();
            int oy = location.getBlockY() - player.getLocation().getBlockY();
            int oz = location.getBlockZ() - player.getLocation().getBlockZ();
            String bStringx = bx + "," + by + "," + bz;
            String cStringx = cx + "," + cy + "," + cz;
            String oString = "x=" + ox + ";y=" + oy + ";z=" + oz;
            StringBuilder outputSelfxx = new StringBuilder()
               .append("<gray>[<yellow>")
               .append("<click:copy_to_clipboard:")
               .append(bStringx)
               .append(">Block</click>")
               .append("<gray>]")
               .append("<gray>[<yellow>")
               .append("<click:copy_to_clipboard:")
               .append(cStringx)
               .append(">Center</click>")
               .append("<gray>]")
               .append("<gray>[<yellow>")
               .append("<click:copy_to_clipboard:")
               .append(oString)
               .append(">Offset</click>")
               .append("<gray>]")
               .append("<gray> <gold>Previous<gray> >> ")
               .append("<aqua>")
               .append(bx)
               .append(",")
               .append(by)
               .append(",")
               .append(bz);
            CommandHelper.send(sender, outputSelfxx.toString());
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getblockcoordinates";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getcoordinates";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"gc", "getblockcoordinates", "getblockcoords", "gbc"};
   }
}
