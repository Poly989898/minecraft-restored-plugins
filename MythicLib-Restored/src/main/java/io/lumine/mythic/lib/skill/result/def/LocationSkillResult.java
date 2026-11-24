package io.lumine.mythic.lib.skill.result.def;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

public class LocationSkillResult implements SkillResult {
   private final Location target;

   public LocationSkillResult(SkillMetadata skillMeta) {
      this(skillMeta, 50.0);
   }

   public LocationSkillResult(SkillMetadata skillMeta, double range) {
      this.target = this.getTargetLocation(skillMeta.getCaster().getPlayer(), skillMeta.getTargetLocationOrNull(), skillMeta.getTargetEntityOrNull(), range);
   }

   public Location getTarget() {
      return this.target;
   }

   @Override
   public boolean isSuccessful() {
      return this.target != null;
   }

   @Nullable
   private Location getTargetLocation(Player caster, @Nullable Location targetLocation, @Nullable Entity targetEntity, double range) {
      if (targetLocation != null) {
         return targetLocation;
      }

      if (targetEntity != null) {
         return targetEntity.getLocation();
      }

      RayTraceResult rayTrace = caster.rayTraceBlocks(range, FluidCollisionMode.NEVER);
      return rayTrace == null ? null : rayTrace.getHitPosition().toLocation(caster.getWorld());
   }
}
