package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GetPathStringCommand extends Command<MythicBukkit> {
   public GetPathStringCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      new GetPathStringCommand.PathStringGetter((Player)sender);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getpathstring";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getpathstring";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"getpath", "gps"};
   }

   private class PathStringGetter implements Terminable, TerminableConsumer {
      private final TerminableRegistry components = TerminableRegistry.create();
      private final List<Location> locations = new ArrayList<>();
      private final Player player;

      public PathStringGetter(Player player) {
         this.player = player;
         CommandHelper.sendSuccess(player, "Place blocks to record coordinates for a goto or patrol goal. Type anything when you're finished to end!");
         Events.subscribe(BlockPlaceEvent.class).handler(event -> {
            if (event.getPlayer().equals(player)) {
               Location loc = event.getBlock().getLocation();
               this.locations.add(loc);
               event.setCancelled(true);
               CommandHelper.sendSuccess(player, "Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to PathString");
            }
         }).bindWith(this);
         Events.subscribe(AsyncPlayerChatEvent.class).handler(event -> {
            if (event.getPlayer().equals(player)) {
               event.setCancelled(true);
               this.closeAndReportException();
            }
         }).bindWith(this);
         Events.subscribe(PlayerQuitEvent.class).handler(event -> {
            if (event.getPlayer().equals(player)) {
               this.closeAndReportException();
            }
         }).bindWith(this);
      }

      @Override
      public void close() throws Exception {
         this.components.closeAndReportException();
         String string = "";

         for (Location loc : this.locations) {
            string = string + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ";";
         }

         CommandHelper.sendSuccess(this.player, "PathString sent to console.");
         MythicLogger.log("PathString: " + string);
      }

      @Override
      public <T extends AutoCloseable> T bind(T terminable) {
         this.components.accept((Terminable)terminable);
         return terminable;
      }
   }
}
