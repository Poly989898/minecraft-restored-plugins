package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Void_Zapper extends io.lumine.mythic.lib.skill.handler.SkillHandler<SimpleSkillResult> {
   public Void_Zapper() {
      this.registerModifiers("damage", "knockback", "length", "max", "extra");
   }

   @NotNull
   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_HURT.get(), 1.0F, 1.0F);
      new Void_Zapper.SkillHandler(skillMeta);
   }

   private Vector getReflection(Vector vec, Vector normal) {
      double projection = vec.dot(normal);
      return vec.clone().add(normal.multiply(-2.0 * projection));
   }

   class SkillHandler {
      private final PlayerMetadata caster;
      private final SkillMetadata skillMeta;
      private int bounces;
      private final int maxBounces;
      private static final double STEP = 0.2;

      SkillHandler(SkillMetadata skillMeta) {
         this.caster = skillMeta.getCaster();
         this.skillMeta = skillMeta;
         this.maxBounces = (int)skillMeta.getParameter("max");
         this.castRay(this.caster.getPlayer().getEyeLocation(), this.caster.getPlayer().getEyeLocation().getDirection(), skillMeta.getParameter("length"));
      }

      void castRay(Location loc, Vector dir, double lengthRemaining) {
         RayTraceResult result = loc.getWorld()
            .rayTrace(loc, dir, lengthRemaining, FluidCollisionMode.NEVER, true, 0.2, entity -> UtilityMethods.canTarget(this.caster.getPlayer(), entity));
         double traveled = result == null ? lengthRemaining : result.getHitPosition().distance(loc.toVector());
         this.draw(loc, dir, traveled);
         this.bounces++;
         if (result != null) {
            if (result.getHitEntity() != null) {
               this.hit((LivingEntity)result.getHitEntity(), dir);
            } else if (this.bounces++ < this.maxBounces) {
               this.castRay(
                  result.getHitPosition().toLocation(loc.getWorld()),
                  Void_Zapper.this.getReflection(dir, result.getHitBlockFace().getDirection()),
                  lengthRemaining - traveled
               );
            }
         }
      }

      void hit(LivingEntity target, Vector dir) {
         double damage = this.skillMeta.getParameter("damage") * (1.0 + this.skillMeta.getParameter("extra") * this.bounces / 100.0);
         this.skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
         target.setVelocity(dir.multiply(this.skillMeta.getParameter("knockback")));
      }

      void draw(Location loc, Vector dir, double length) {
         for (double dis = 0.0; dis < length; dis += 0.2) {
            Location intermediate = loc.clone().add(dir.clone().multiply(dis));
            loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), intermediate, 1, new DustOptions(Color.PURPLE, 1.2F));
         }
      }
   }
}
