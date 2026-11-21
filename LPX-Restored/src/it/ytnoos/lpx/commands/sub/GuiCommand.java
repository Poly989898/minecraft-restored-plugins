package it.ytnoos.lpx.commands.sub;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends SubCommand {
   public GuiCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "gui", "lpx.gui.main", "Open GUI", true);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      this.lpxBukkit.getInventoryContainer().mainInventory().open((Player)commandSender);
   }
}
