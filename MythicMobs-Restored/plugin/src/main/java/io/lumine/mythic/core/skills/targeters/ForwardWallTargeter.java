package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@MythicTargeter(
   author = "Ashijin",
   name = "forwardwall",
   aliases = {},
   description = "Targets a plane in front of the caster"
)
public class ForwardWallTargeter extends ILocationSelector {
   protected double forward;
   protected double yoffset;
   protected int height;
   protected int width;

   public ForwardWallTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.forward = mlc.getDouble(new String[]{"forward", "f", "amount", "a"}, 5.0);
      this.yoffset = mlc.getDouble(new String[]{"yoffset", "y"}, 0.0);
      this.height = mlc.getInteger(new String[]{"height", "h"}, 2);
      this.width = mlc.getInteger(new String[]{"width", "w"}, 3);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      AbstractLocation location = am.getEntity()
         .getLocation()
         .add(am.getEntity().getLocation().getDirection().normalize().multiply(this.forward))
         .add(0.0, this.yoffset, 0.0);
      targets.add(location);
      Location base = BukkitAdapter.adapt(location);
      float yaw = Math.round(am.getLocation().getYaw() / 90.0F);
      BlockFace right = getRight(yaw);
      if (right != null) {
         for (int h = -this.height; h <= this.height; h++) {
            Block add = base.getBlock().getRelative(BlockFace.DOWN, h);

            for (int w = -this.width; w <= this.width; w++) {
               Block add2 = add.getRelative(right, w);
               targets.add(BukkitAdapter.adapt(add2.getLocation()));
            }
         }
      }

      return targets;
   }

   public static BlockFace getLeft(float yaw) {
      if (yaw == -4.0F || yaw == 0.0F || yaw == 4.0F) {
         return BlockFace.EAST;
      } else if (yaw == -1.0F || yaw == 3.0F) {
         return BlockFace.NORTH;
      } else if (yaw == -2.0F || yaw == 2.0F) {
         return BlockFace.WEST;
      } else {
         return yaw != -3.0F && yaw != 1.0F ? null : BlockFace.SOUTH;
      }
   }

   public static BlockFace getRight(float yaw) {
      if (yaw == -4.0F || yaw == 0.0F || yaw == 4.0F) {
         return BlockFace.WEST;
      } else if (yaw == -1.0F || yaw == 3.0F) {
         return BlockFace.SOUTH;
      } else if (yaw == -2.0F || yaw == 2.0F) {
         return BlockFace.EAST;
      } else {
         return yaw != -3.0F && yaw != 1.0F ? null : BlockFace.NORTH;
      }
   }
}
