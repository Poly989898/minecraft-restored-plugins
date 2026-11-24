package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Targeted_Fireball extends SkillHandler<TargetSkillResult> {
   public Targeted_Fireball() {
      this.registerModifiers("ignite", "damage");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         final Location loc = caster.getLocation().add(0.0, 1.3, 0.0);
         int j = 0;

         public void run() {
            this.j++;
            if (!target.isDead() && target.getWorld().equals(this.loc.getWorld()) && this.j <= 200) {
               Vector dir = target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0).subtract(this.loc).toVector().normalize();
               this.loc.add(dir.multiply(0.6));
               this.loc.setDirection(dir);

               for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 6) {
                  Vector rotated = UtilityMethods.rotate(new Vector(Math.cos(a), Math.sin(a), 0.0), this.loc.getDirection());
                  this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 0, rotated.getX(), rotated.getY(), rotated.getZ(), 0.06);
               }

               this.loc.getWorld().playSound(this.loc, VSound.BLOCK_NOTE_BLOCK_HAT.get(), 1.0F, 1.0F);
               if (target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0).distanceSquared(this.loc) < 1.3) {
                  this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 8);
                  this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 32, 0.0, 0.0, 0.0, 0.1);
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.0F);
                  target.setFireTicks((int)(target.getFireTicks() + skillMeta.getParameter("ignite") * 20.0));
                  skillMeta.getCaster().attack(target, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                  this.cancel();
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
