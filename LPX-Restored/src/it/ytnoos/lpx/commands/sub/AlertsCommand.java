package it.ytnoos.lpx.commands.sub;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand extends SubCommand {
   public AlertsCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "alerts", lpxBukkit2.getConfigManager().getMessagesConfig().getAlertsConfig().getPermission(), "Toggle alerts", true);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      boolean flag = this.lpxBukkit.getAlertManager().toggleAlerts((Player)commandSender);
      if (flag) {
         MessageFormatter.send(commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getAlertsConfig().getEnabled());
      } else {
         MessageFormatter.send(commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getAlertsConfig().getDisabled());
      }
   }

}
