package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GetTargetNBTCommand extends Command<MythicBukkit> {
   public GetTargetNBTCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      LivingEntity l = MythicUtil.getTargetedEntity(p);
      if (l == null) {
         CommandHelper.sendError(sender, "You must target a valid entity!");
         return true;
      } else {
         CompoundTag tag = MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getNBTData(BukkitAdapter.adapt(l));
         CommandHelper.sendSuccess(sender, "Target NBT: " + tag.toString());
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getentitynbt";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "gettargetnbt";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"getnbt"};
   }
}
