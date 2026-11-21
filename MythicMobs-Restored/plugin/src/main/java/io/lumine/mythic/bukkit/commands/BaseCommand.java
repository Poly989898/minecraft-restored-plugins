package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.debug.DebugCommand;
import io.lumine.mythic.bukkit.commands.dialogs.DialogsCommand;
import io.lumine.mythic.bukkit.commands.eggs.EggsCommand;
import io.lumine.mythic.bukkit.commands.items.ItemsCommand;
import io.lumine.mythic.bukkit.commands.menus.MenuCommand;
import io.lumine.mythic.bukkit.commands.mobs.MobsCommand;
import io.lumine.mythic.bukkit.commands.pins.PinsCommand;
import io.lumine.mythic.bukkit.commands.skills.SkillsCommand;
import io.lumine.mythic.bukkit.commands.spawners.SpawnersCommand;
import io.lumine.mythic.bukkit.commands.test.TestCommand;
import io.lumine.mythic.bukkit.commands.utility.UtilitiesCommand;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@Getter
public class BaseCommand extends Command<MythicBukkit> {
   private ItemsCommand itemsCommand = new ItemsCommand(this);
   private MobsCommand mobsCommand = new MobsCommand(this);
   private PinsCommand pinsCommand;
   private SkillsCommand skillsCommand = new SkillsCommand(this);
   private SpawnersCommand spawnersCommand = new SpawnersCommand(this);
   private TestCommand testCommand;
   private UtilitiesCommand utilitiesCommand;

   public BaseCommand(MythicBukkit plugin) {
      super(plugin);
      this.pinsCommand = new PinsCommand(this);
      this.testCommand = new TestCommand(this);
      this.utilitiesCommand = new UtilitiesCommand(this);
      this.addSubCommands(
         new Command[]{
            new InfoCommand(this),
            new ReloadCommand(this),
            new SaveCommand(this),
            new DebugCommand(this),
            new SignalCommand(this),
            new EggsCommand(this),
            new MenuCommand(this),
            new VersionCommand(this),
            new DialogsCommand(this),
            new CameraCommand(this),
            this.itemsCommand,
            this.mobsCommand,
            this.spawnersCommand,
            this.skillsCommand,
            this.pinsCommand,
            this.testCommand,
            this.utilitiesCommand
         }
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (MythicBukkit.isVolatile()) {
         CommandHelper.sendCommandMessage(
            sender,
            new Component[]{
               Text.parse("&e/mm &fmobs &7\u25ba &7&oMob-related commands"),
               Text.parse("&e/mm &fitems &7\u25ba &7&oItems-related commands"),
               Text.parse("&e/mm &fskills &7\u25ba &7&oSkill-related commands"),
               Text.parse("&e/mm &fspawners &7\u25ba &7&oSpawner-related commands"),
               Text.parse("&e/mm &futility &7\u25ba &7&oSome helpful utility commands"),
               Text.parse("&e/mm &amenu &7\u25ba &7&oOpen the main menu"),
               Text.parse("&e/mm &areload &7\u25ba &7&oReloads all configuration files and mobs"),
               Text.parse("&e/mm &adebug &6[level] &7\u25ba &7&oEnables console debugging output"),
               Text.parse("&e/mm &aversion &7\u25ba &7&oPlugin version information")
            }
         );
      } else {
         CommandHelper.sendCommandMessage(
            sender,
            new Component[]{
               Text.parse("&e/mm &fmobs &7\u25ba &7&oMob-related commands"),
               Text.parse("&e/mm &fitems &7\u25ba &7&oItems-related commands"),
               Text.parse("&e/mm &fskills &7\u25ba &7&oSkill-related commands"),
               Text.parse("&e/mm &fspawners &7\u25ba &7&oSpawner-related commands"),
               Text.parse("&e/mm &futility &7\u25ba &7&oSome helpful utility commands"),
               Text.parse("&e/mm &areload &7\u25ba &7&oReloads all configuration files and mobs"),
               Text.parse("&e/mm &adebug &6[level] &7\u25ba &7&oEnables console debugging output"),
               Text.parse("&e/mm &aversion &7\u25ba &7&oPlugin version information")
            }
         );
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.base";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return null;
   }
}
