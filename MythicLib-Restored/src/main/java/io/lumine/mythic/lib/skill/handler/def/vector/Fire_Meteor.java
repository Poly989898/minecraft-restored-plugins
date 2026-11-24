package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Fire_Meteor extends SkillHandler<VectorSkillResult> {
   public Fire_Meteor() {
      this.registerModifiers("damage", "knockback", "radius");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 3.0F, 1.0F);
      (new BukkitRunnable() {
         final Location loc = caster.getLocation().clone().add(0.0, 10.0, 0.0);
         final Vector vec = result.getTarget().multiply(1.3).setY(-1).normalize();
         double ti = 0.0;

         public void run() {
            this.ti++;
            if (this.ti > 40.0) {
               this.cancel();
            }

            this.loc.add(this.vec);
            this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 0);
            this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 4, 0.2, 0.2, 0.2, 0.0);
            if (this.loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() || this.loc.getBlock().getType().isSolid()) {
               this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 0.6F);
               this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 10, 2.0, 2.0, 2.0, 0.0);
               this.loc.getWorld().spawnParticle(VParticle.EXPLOSION.get(), this.loc, 32, 0.0, 0.0, 0.0, 0.3);
               this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 32, 0.0, 0.0, 0.0, 0.3);
               double damage = skillMeta.getParameter("damage");
               double knockback = skillMeta.getParameter("knockback");
               double radius = skillMeta.getParameter("radius");

               for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                  if (UtilityMethods.canTarget(caster, entity) && entity.getLocation().distanceSquared(this.loc) < radius * radius) {
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                     entity.setVelocity(entity.getLocation().toVector().subtract(this.loc.toVector()).multiply(0.1 * knockback).setY(0.4 * knockback));
                  }
               }

               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
