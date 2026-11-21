package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AddEntityNBTCommand extends Command<MythicBukkit> {
   public AddEntityNBTCommand(Command<MythicBukkit> parent) {
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
         CompoundTag tag2 = CompoundTagBuilder.create().putString("test", "yes").build();
         tag = tag.createBuilder()
            .put("mythicmobs", tag2)
            .putString("gaygrounds", "fuck")
            .putInt("test", 69)
            .putDouble("Health", 20.0)
            .putBoolean("NoAI", true)
            .build();
         MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().setNBTData(BukkitAdapter.adapt(l), tag);
         CommandHelper.sendSuccess(sender, "Added NBT");
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.addentitynbt";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "addentitynbt";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"addenbt"};
   }
}
