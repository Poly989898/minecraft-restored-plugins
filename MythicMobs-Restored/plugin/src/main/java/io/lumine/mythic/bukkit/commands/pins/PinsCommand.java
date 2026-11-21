package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class PinsCommand extends Command<MythicBukkit> {
   public PinsCommand(MythicBukkit parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new AddPinCommand(this),
            new AddRegionCommand(this),
            new RemovePinCommand(this),
            new RemoveRegionCommand(this),
            new RedefineRegionCommand(this),
            new ListPinsCommand(this),
            new ListRegionsCommand(this),
            new MovePinCommand(this),
            new SwapPinCommand(this),
            new ToggleViewingPinsCommand(this),
            new WandCommand(this),
            new TeleportCommand(this)
         }
      );
   }

   public PinsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new AddPinCommand(this),
            new RemovePinCommand(this),
            new ListPinsCommand(this),
            new MovePinCommand(this),
            new SwapPinCommand(this),
            new ToggleViewingPinsCommand(this),
            new WandCommand(this),
            new TeleportCommand(this)
         }
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "pins";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"pin", "p"};
   }
}
