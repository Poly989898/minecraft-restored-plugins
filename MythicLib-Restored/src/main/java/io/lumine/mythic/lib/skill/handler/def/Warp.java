package io.lumine.mythic.lib.skill.handler.def;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Warp extends SkillHandler<Warp.WarpSkillResult> {
   public Warp() {
      this.registerModifiers("range");
   }

   @NotNull
   public Warp.WarpSkillResult getResult(SkillMetadata meta) {
      return new Warp.WarpSkillResult(meta);
   }

   public void whenCast(Warp.WarpSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 2.0F);
      Location loc = result.getLocation();
      new ParabolicProjectile(caster.getLocation().add(0.0, 1.0, 0.0), loc.clone().add(0.0, 1.0, 0.0), () -> {
         if (!UtilityMethods.isInvalidated(caster)) {
            caster.teleport(loc);
            caster.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 0);
            caster.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), caster.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.1);
            caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 1.0F, 1.0F);
         }
      }, 2, VParticle.INSTANT_EFFECT.get());
   }

   public static class WarpSkillResult implements SkillResult {
      private final Location loc;

      public WarpSkillResult(SkillMetadata meta) {
         Player caster = meta.getCaster().getPlayer();
         RayTraceResult res = caster.rayTraceBlocks(meta.getParameter("range"), FluidCollisionMode.NEVER);
         this.loc = res == null ? null : res.getHitPosition().toLocation(caster.getWorld());
         if (this.loc != null) {
            Vector dir = caster.getEyeLocation().getDirection();
            this.loc.setDirection(dir);
            this.loc.add(dir.setY(0).multiply(-0.5));
         }
      }

      @Override
      public boolean isSuccessful() {
         return this.loc != null;
      }

      public Location getLocation() {
         return this.loc;
      }
   }
}
