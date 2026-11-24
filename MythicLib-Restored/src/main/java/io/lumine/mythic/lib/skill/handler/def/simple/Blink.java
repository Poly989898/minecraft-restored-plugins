package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Blink extends SkillHandler<SimpleSkillResult> {
   private static final double MIN_RANGE = 1.0;

   public Blink() {
      this.registerModifiers("range");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      double range = Math.max(1.0, skillMeta.getParameter("range"));
      Vector dir = caster.getEyeLocation().getDirection();
      caster.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 0);
      caster.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), caster.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.1);
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 1.0F, 1.0F);
      RayTraceResult rtResult = caster.getWorld().rayTraceBlocks(caster.getEyeLocation(), dir, range, FluidCollisionMode.NEVER, true);
      Location loc = rtResult != null && rtResult.getHitBlock() != null
         ? rtResult.getHitPosition().add(rtResult.getHitBlockFace().getDirection().multiply(0.3)).toLocation(caster.getWorld())
         : caster.getLocation().add(dir.clone().multiply(range));
      loc.setYaw(caster.getLocation().getYaw());
      loc.setPitch(caster.getLocation().getPitch());
      caster.teleport(loc);
      caster.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 0);
      caster.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), caster.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.1);
   }
}
