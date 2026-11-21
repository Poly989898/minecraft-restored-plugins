package it.ytnoos.lpx.commands.sub;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MonitorCommand extends SubCommand {
   public MonitorCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "monitor", "lpx.metrics", "Turn off/on the PTD Monitor", false);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      FoliaScheduler.getAsyncScheduler().runNow(this.lpxBukkit.getPlugin(), object -> {
         if (this.lpxBukkit.getPacketTickMonitor().isRunning()) {
            this.lpxBukkit.getPacketTickMonitor().stop();
            this.sendMessage(commandSender, ChatColor.RED + "Monitor disabled.");
         } else {
            this.lpxBukkit.getPacketTickMonitor().start();
            this.sendMessage(commandSender, ChatColor.GREEN + "Monitor enabled, it's not recommended to keep it enabled forever!");
         }
      });
   }

}
