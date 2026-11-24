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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Cursed_Beam extends SkillHandler<VectorSkillResult> {
   public Cursed_Beam() {
      this.registerModifiers("damage", "duration");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = skillMeta.getParameter("duration");
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0F, 2.0F);
      (new BukkitRunnable() {
         final Vector dir = result.getTarget().multiply(0.3);
         final Location loc = caster.getEyeLocation().clone();
         final double r = 0.4;
         int ti = 0;

         public void run() {
            this.ti++;
            if (this.ti > 50) {
               this.cancel();
            }

            List<Entity> entities = UtilityMethods.getNearbyChunkEntities(this.loc);

            for (double j = 0.0; j < 4.0; j++) {
               this.loc.add(this.dir);

               for (double i = 0.0; i < Math.PI * 2; i += Math.PI / 6) {
                  Vector vec = UtilityMethods.rotate(new Vector(0.4 * Math.cos(i), 0.4 * Math.sin(i), 0.0), this.loc.getDirection());
                  this.loc.add(vec);
                  this.loc.getWorld().spawnParticle(VParticle.WITCH.get(), this.loc, 0);
                  this.loc.add(vec.multiply(-1));
               }

               for (Entity target : entities) {
                  if (UtilityMethods.canTarget(caster, this.loc, target)) {
                     Cursed_Beam.this.effect(target);
                     double damage = skillMeta.getParameter("damage");
                     this.loc.getWorld().playSound(this.loc, VSound.ENTITY_ENDERMAN_TELEPORT.get(), 2.0F, 0.7F);

                     for (Entity entity : entities) {
                        if (UtilityMethods.canTarget(caster, entity) && this.loc.distanceSquared(entity.getLocation().add(0.0, 1.0, 0.0)) < 9.0) {
                           skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                           ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)(duration * 20.0), 0));
                        }
                     }

                     this.cancel();
                     return;
                  }
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   private void effect(final Entity ent) {
      (new BukkitRunnable() {
         final Location loc2 = ent.getLocation();
         double y = 0.0;

         public void run() {
            for (int i = 0; i < 3; i++) {
               this.y += 0.05;

               for (int j = 0; j < 2; j++) {
                  double xz = this.y * Math.PI * 0.8 + j * Math.PI;
                  this.loc2.getWorld().spawnParticle(VParticle.WITCH.get(), this.loc2.clone().add(Math.cos(xz) * 2.5, this.y, Math.sin(xz) * 2.5), 0);
               }
            }

            if (this.y >= 3.0) {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
