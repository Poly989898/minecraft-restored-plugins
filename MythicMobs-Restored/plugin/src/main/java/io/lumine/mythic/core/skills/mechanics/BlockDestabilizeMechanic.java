package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

@MythicMechanic(
   author = "Ashijin",
   name = "blockDestabilize",
   aliases = {"destabilizeBlock", "destabilizeBlocks"},
   description = "Turns a block into a falling block"
)
public class BlockDestabilizeMechanic extends SkillMechanic implements ITargetedLocationSkill {
   public BlockDestabilizeMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.doIt(data.getOrigin(), target);
      return SkillResult.SUCCESS;
   }

   private void doIt(AbstractLocation source, AbstractLocation target) {
      int centerX = target.getBlockX();
      int centerY = target.getBlockY();
      int centerZ = target.getBlockZ();
      World world = BukkitAdapter.adapt(target.getWorld());
      Block b = world.getBlockAt(centerX, centerY, centerZ);
      BlockData material = b.getBlockData();
      b.setType(Material.AIR);
      Location l = new Location(world, b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5);
      FallingBlock fb = world.spawnFallingBlock(l, material);
      fb.setDropItem(false);
      b.setType(Material.AIR);
   }
}
