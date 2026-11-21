package it.ytnoos.lpx.commands.sub;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import it.ytnoos.lpx.watchdog.NettyWatchdog;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class WatchdogCommand extends SubCommand {
   public WatchdogCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "watchdog", "lpx.watchdog", "Toggle netty watchdog", false);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      FoliaScheduler.getAsyncScheduler().runNow(this.lpxBukkit.getPlugin(), object -> {
         NettyWatchdog nettyWatchdog = this.lpxBukkit.getNettyWatchdog();
         if (nettyWatchdog.isRunning()) {
            this.sendMessage(commandSender, ChatColor.YELLOW + "Stopping the watchdog thread...");
         } else {
            this.sendMessage(commandSender, ChatColor.YELLOW + "Starting the watchdog thread...");
            this.sendMessage(commandSender, ChatColor.RED + "WARNING! Do not use this in production because it may cause TPS Drops!");
         }

         this.lpxBukkit.getNettyWatchdog().toggle();
         this.sendMessage(commandSender, nettyWatchdog.isRunning() ? ChatColor.GREEN + "Watchdog thread started!" : ChatColor.RED + "Watchdog thread stopped!");
      });
   }

}
