package litebans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@PlatformMarker(obtainByteValue = 0)
public abstract class BukkitLiteCommandManager extends LiteCommandManager implements CommandExecutor {
   public BukkitLiteCommandManager(Platform platform) {
      super(platform);
   }

   public boolean onCommand(CommandSender commandSender, Command command, String text, String[] texts) {
      LiteSender wrapSender2 = this.platform.wrapSender(commandSender);
      this.handleLiteSender(wrapSender2, command.getName(), texts);
      return true;
   }
}
