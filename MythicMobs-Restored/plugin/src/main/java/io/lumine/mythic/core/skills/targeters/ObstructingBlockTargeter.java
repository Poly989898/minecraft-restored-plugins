package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

@MythicTargeter(
   author = "Ashijin",
   name = "obstructingBlock",
   aliases = {},
   description = "Tries to target blocks in front of the caster that are obstructing it"
)
public class ObstructingBlockTargeter extends ILocationSelector {
   public ObstructingBlockTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      Entity entity = BukkitAdapter.adapt(am.getEntity());
      Location location = entity.getLocation();
      float yaw = entity.getLocation().getYaw();
      BlockFace bf = yawToFace(yaw);
      Block frontBlock = location.getBlock().getRelative(bf, 1);
      Block targetBlock = frontBlock;
      int i = 0;

      while (targetBlock.isPassable()) {
         if (++i >= 10) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "-- No solid block found?");
            return targets;
         }

         switch (i) {
            case 1:
               targetBlock = location.getBlock();
               break;
            case 2:
               targetBlock = frontBlock.getRelative(BlockFace.UP, 1);
               break;
            case 3:
               targetBlock = frontBlock.getRelative(BlockFace.UP, 2);
               break;
            case 4:
               targetBlock = frontBlock.getRelative(getLeft(bf), 1);
               break;
            case 5:
               targetBlock = frontBlock.getRelative(getLeft(bf), 1).getRelative(BlockFace.UP, 1);
               break;
            case 6:
               targetBlock = frontBlock.getRelative(getLeft(bf), 1).getRelative(BlockFace.UP, 2);
               break;
            case 7:
               targetBlock = frontBlock.getRelative(getRight(bf), 1);
               break;
            case 8:
               targetBlock = frontBlock.getRelative(getRight(bf), 1).getRelative(BlockFace.UP, 1);
               break;
            case 9:
               targetBlock = frontBlock.getRelative(getRight(bf), 1).getRelative(BlockFace.UP, 2);
         }
      }

      targets.add(BukkitAdapter.adapt(targetBlock.getLocation()));
      return targets;
   }

   public static BlockFace yawToFace(float yaw) {
      yaw = (yaw % 360.0F + 360.0F) % 360.0F;
      yaw += 45.0F;
      int index = (int)(yaw / 90.0F) % 4;
      switch (index) {
         case 0:
            return BlockFace.SOUTH;
         case 1:
            return BlockFace.WEST;
         case 2:
            return BlockFace.NORTH;
         case 3:
            return BlockFace.EAST;
         default:
            return BlockFace.NORTH;
      }
   }

   public static BlockFace getLeft(BlockFace face) {
      switch (face) {
         case NORTH:
            return BlockFace.WEST;
         case EAST:
            return BlockFace.NORTH;
         case SOUTH:
            return BlockFace.EAST;
         case WEST:
            return BlockFace.SOUTH;
         default:
            return BlockFace.NORTH;
      }
   }

   public static BlockFace getRight(BlockFace face) {
      switch (face) {
         case NORTH:
            return BlockFace.EAST;
         case EAST:
            return BlockFace.SOUTH;
         case SOUTH:
            return BlockFace.WEST;
         case WEST:
            return BlockFace.NORTH;
         default:
            return BlockFace.NORTH;
      }
   }
}
