package it.ytnoos.lpx.commands.sub;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import it.ytnoos.lpx.monitor.HealthReport;
import it.ytnoos.lpx.monitor.PacketTickMonitor;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HealthCommand extends SubCommand {
   private final PacketTickMonitor packetTickMonitor;
   private final HealthReport healthReport;

   public HealthCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "health", "lpx.health", "View the netty health report", false);
      this.packetTickMonitor = lpxBukkit2.getPacketTickMonitor();
      this.healthReport = new HealthReport(lpxBukkit2);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      if (!this.packetTickMonitor.isRunning()) {
         MessageFormatter.send(commandSender, ChatColor.RED + "Monitor is not running. Enable it with /lpx monitor");
      } else {
         FoliaScheduler.getAsyncScheduler().runNow(this.lpxBukkit.getPlugin(), object -> {
            String string = this.healthReport.generate(this.packetTickMonitor);
            MessageFormatter.send(commandSender, string);
         });
      }
   }

}
