package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Light_Dash extends SkillHandler<SimpleSkillResult> {
   public Light_Dash() {
      this.registerModifiers("damage", "length");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, final SkillMetadata skillMeta) {
      final double damage = skillMeta.getParameter("damage");
      final double length = skillMeta.getParameter("length");
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         final Vector vec = caster.getEyeLocation().getDirection();
         final List<Integer> hit = new ArrayList<>();
         int j = 0;

         public void run() {
            if (this.j++ > 10.0 * Math.min(10.0, length)) {
               this.cancel();
            }

            caster.setVelocity(this.vec);
            caster.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), caster.getLocation().add(0.0, 1.0, 0.0), 0);
            caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 1.0F, 2.0F);

            for (Entity entity : caster.getNearbyEntities(1.0, 1.0, 1.0)) {
               if (!this.hit.contains(entity.getEntityId()) && UtilityMethods.canTarget(caster, entity)) {
                  this.hit.add(entity.getEntityId());
                  skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.PHYSICAL);
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 2L);
   }
}
