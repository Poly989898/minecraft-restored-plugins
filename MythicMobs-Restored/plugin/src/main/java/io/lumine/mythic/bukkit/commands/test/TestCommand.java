package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class TestCommand extends Command<MythicBukkit> {
   public TestCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new AddThreatCommand(this),
            new CastCommand(this),
            new MechanicCommand(this),
            new ReduceThreatCommand(this),
            new TauntCommand(this),
            new SpawnersCommand(this),
            new TestAPICommand(this)
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
      return "mythicmobs.command.test";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "test";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"t"};
   }
}
