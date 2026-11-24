package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Heal extends SkillHandler<SimpleSkillResult> {
   public Heal() {
      this.registerModifiers("heal");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
      caster.getWorld().spawnParticle(Particle.HEART, caster.getLocation().add(0.0, 0.75, 0.0), 16, 1.0, 1.0, 1.0, 0.0);
      caster.getWorld().spawnParticle(VParticle.HAPPY_VILLAGER.get(), caster.getLocation().add(0.0, 0.75, 0.0), 16, 1.0, 1.0, 1.0, 0.0);
      UtilityMethods.heal(caster, skillMeta.getParameter("heal"));
   }
}
