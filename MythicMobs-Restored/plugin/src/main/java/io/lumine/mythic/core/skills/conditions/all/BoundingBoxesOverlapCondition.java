package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@MythicCondition(
   author = "Ashijin",
   name = "boundingBoxesOverlap",
   aliases = {"bbsoverlap"},
   version = "5.7",
   description = "Tests the caster's bb overlaps the target's bb"
)
public class BoundingBoxesOverlapCondition extends SkillCondition implements IEntityComparisonCondition {
   private double shiftForward;

   public BoundingBoxesOverlapCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.shiftForward = mlc.getDouble(new String[]{"shiftforward", "so", "forwardoffset", "fo", "forward", "f"}, 0.0);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
         Boolean result = Schedulers.of(entity.getBukkitEntity()).supply(() -> this.checkOverlap(entity, target)).join();
         return result != null ? result : false;
      } else {
         return this.checkOverlap(entity, target);
      }
   }

   private boolean checkOverlap(AbstractEntity entity, AbstractEntity target) {
      BoundingBox bb = entity.getBukkitEntity().getBoundingBox();
      BoundingBox targetBB = target.getBukkitEntity().getBoundingBox();
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BoundingBoxes Overlap {0} vs {1}", bb, targetBB);
      if (this.shiftForward == 0.0) {
         return bb.overlaps(targetBB);
      } else {
         Vector direction = entity.getBukkitEntity().getLocation().getDirection();
         Vector shift = direction.normalize().multiply(this.shiftForward);
         double shiftedMinX = bb.getMinX() + shift.getX();
         double shiftedMaxX = bb.getMaxX() + shift.getX();
         double shiftedMinZ = bb.getMinZ() + shift.getZ();
         double shiftedMaxZ = bb.getMaxZ() + shift.getZ();
         BoundingBox shiftedBB = new BoundingBox(shiftedMinX, bb.getMinY(), shiftedMinZ, shiftedMaxX, bb.getMaxY(), shiftedMaxZ);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Shifted BoundingBoxes Overlap {0} vs {1}", shiftedBB, targetBB);
         return shiftedBB.overlaps(targetBB);
      }
   }
}
