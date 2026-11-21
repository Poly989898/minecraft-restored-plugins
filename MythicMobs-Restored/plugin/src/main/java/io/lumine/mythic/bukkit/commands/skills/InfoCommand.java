package io.lumine.mythic.bukkit.commands.skills;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.skills.MetaSkill;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command<MythicBukkit> {
   public InfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length == 0) {
         CommandHelper.sendError(sender, "Usage /mm s[kills] i[nfo] <internal-name>");
         return true;
      } else {
         if (this.getPlugin().getSkillManager().getSkill(args[0]).isPresent()) {
            MetaSkill mm = (MetaSkill)this.getPlugin().getSkillManager().getSkill(args[0]).get();
            sender.sendMessage(ColorString.get("&e&lInformation for &a" + mm.getInternalName() + "&7:"));
            sender.sendMessage(ColorString.get("&6Skill ID: &7" + mm.hashCode()));
            sender.sendMessage(ColorString.get("&6Located in File: &7" + mm.getFile()));
         } else {
            CommandHelper.sendError(sender, "No Mythic Skill loaded with the name " + args[0]);
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getSkillManager().getSkills().stream().map(Skill::getInternalName).collect(Collectors.toList()), 2
         )
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.skills.info";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "info";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"i"};
   }
}
