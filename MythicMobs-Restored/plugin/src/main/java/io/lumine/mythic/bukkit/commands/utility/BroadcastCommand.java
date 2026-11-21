package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BroadcastCommand extends Command<MythicBukkit> {
   public BroadcastCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "Input Required");
         return true;
      } else {
         boolean optAsTarget = false;
         if (args != null && args.length > 1 && args[0].startsWith("-")) {
            if (args[0].contains("t")) {
               optAsTarget = true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
         }

         String input = "";

         for (int i = 0; i < args.length; i++) {
            input = input.concat(args[i]) + " ";
         }

         PlaceholderString text = PlaceholderString.of(input);
         if (sender instanceof Player player) {
            AbstractEntity targetEntity = null;
            LivingEntity targetedEntity = MythicUtil.getTargetedEntity(player);
            if (targetedEntity != null) {
               targetEntity = BukkitAdapter.adapt(targetedEntity);
            }

            AbstractEntity castingEntity;
            if (optAsTarget) {
               castingEntity = BukkitAdapter.adapt(targetedEntity);
            } else {
               castingEntity = BukkitAdapter.adapt(player);
            }

            SkillCaster caster = this.getPlugin().getSkillManager().getCaster(castingEntity);
            SkillMetadata data = this.getPlugin()
               .getSkillManager()
               .getEventBus()
               .buildSkillMetadata(SkillTriggers.API, caster, castingEntity, castingEntity.getLocation(), true);
            String out = text.get(data, targetEntity);

            for (Player p : Players.all()) {
               Text.sendMessage(p, out);
            }
         } else {
            String out = text.get();

            for (Player player : Players.all()) {
               Text.sendMessage(player, out);
            }
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.broadcast";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "broadcast";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"b"};
   }
}
