package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Arcane_Rift extends SkillHandler<VectorSkillResult> {
   public Arcane_Rift() {
      this.registerModifiers("damage", "amplifier", "speed", "duration");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return meta.getCaster().getPlayer().isOnGround() ? new VectorSkillResult(meta) : new VectorSkillResult((Vector)null);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double damage = skillMeta.getParameter("damage");
      final double slowDuration = skillMeta.getParameter("duration");
      final double slowAmplifier = skillMeta.getParameter("amplifier");
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_DEATH.get(), 2.0F, 0.5F);
      (new BukkitRunnable() {
            final Vector vec = result.getTarget().setY(0).normalize().multiply(0.5 * skillMeta.getParameter("speed"));
            final Location loc = caster.getLocation();
            final int duration = (int)(20.0 * Math.min(skillMeta.getParameter("duration"), 10.0));
            final List<Integer> hit = new ArrayList<>();
            int ti = 0;

            public void run() {
               if (this.ti++ > this.duration) {
                  this.cancel();
               }

               this.loc.add(this.vec);
               this.loc.getWorld().spawnParticle(VParticle.WITCH.get(), this.loc, 5, 0.5, 0.0, 0.5, 0.0);

               for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                  if (UtilityMethods.canTarget(caster, entity)
                     && this.loc.distanceSquared(entity.getLocation()) < 2.0
                     && !this.hit.contains(entity.getEntityId())) {
                     this.hit.add(entity.getEntityId());
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC);
                     ((LivingEntity)entity).addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), (int)(slowDuration * 20.0), (int)slowAmplifier));
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
