package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Blind extends SkillHandler<TargetSkillResult> {
   public Blind() {
      this.registerModifiers("duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      Player caster = skillMeta.getCaster().getPlayer();
      target.getWorld().playSound(target.getLocation(), VSound.ENTITY_ENDERMAN_HURT.get(), 1.0F, 2.0F);

      for (double i = 0.0; i < Math.PI * 2; i += 0.1308996938995747) {
         for (double j = 0.0; j < 2.0; j++) {
            Location loc = target.getLocation();
            Vector vec = UtilityMethods.rotate(new Vector(Math.cos(i), 1.0 + Math.cos(i + Math.PI * j) * 0.5, Math.sin(i)), caster.getLocation().getDirection());
            loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), loc.add(vec), 1, new DustOptions(Color.BLACK, 1.0F));
         }
      }

      target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)(skillMeta.getParameter("duration") * 20.0), 0));
   }
}
