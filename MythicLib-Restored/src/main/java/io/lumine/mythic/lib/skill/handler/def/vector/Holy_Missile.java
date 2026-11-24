package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Holy_Missile extends SkillHandler<VectorSkillResult> {
   public Holy_Missile() {
      this.registerModifiers("damage", "duration");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = skillMeta.getParameter("duration") * 10.0;
      final double damage = skillMeta.getParameter("damage");
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
      (new BukkitRunnable() {
         final Vector vec = result.getTarget().multiply(0.45);
         final Location loc = caster.getLocation().clone().add(0.0, 1.3, 0.0);
         double ti = 0.0;

         public void run() {
            if (this.ti++ > duration) {
               this.cancel();
            }

            this.loc.getWorld().playSound(this.loc, VSound.BLOCK_NOTE_BLOCK_HAT.get(), 2.0F, 1.0F);
            List<Entity> entities = UtilityMethods.getNearbyChunkEntities(this.loc);

            for (int j = 0; j < 2; j++) {
               this.loc.add(this.vec);
               if (this.loc.getBlock().getType().isSolid()) {
                  this.cancel();
               }

               for (double i = -Math.PI; i < Math.PI; i += Math.PI / 2) {
                  Vector v = new Vector(Math.cos(i + this.ti / 4.0), Math.sin(i + this.ti / 4.0), 0.0);
                  v = UtilityMethods.rotate(v, this.loc.getDirection());
                  this.loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), this.loc, 0, v.getX(), v.getY(), v.getZ(), 0.08);
               }

               for (Entity entity : entities) {
                  if (UtilityMethods.canTarget(caster, this.loc, entity)) {
                     this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 1);
                     this.loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), this.loc, 32, 0.0, 0.0, 0.0, 0.2);
                     this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                     this.cancel();
                     return;
                  }
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
