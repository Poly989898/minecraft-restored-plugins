package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.util.SmallParticleEffect;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class Weaken extends SkillHandler<TargetSkillResult> {
   public Weaken() {
      this.registerModifiers("ratio", "duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      Player caster = skillMeta.getCaster().getPlayer();
      new ParabolicProjectile(
         caster.getPlayer().getLocation().add(0.0, 1.0, 0.0),
         target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0),
         this.randomVector(caster.getPlayer()),
         () -> {
            if (!target.isDead()) {
               new Weaken.Weakened(target, skillMeta.getParameter("ratio"), skillMeta.getParameter("duration"));
            }
         },
         2,
         VParticle.WITCH.get()
      );
   }

   private Vector randomVector(Player player) {
      double a = Math.toRadians(player.getEyeLocation().getYaw() + 90.0F);
      a += (RANDOM.nextBoolean() ? 1 : -1) * (RANDOM.nextDouble() + 0.5) * Math.PI / 6.0;
      return new Vector(Math.cos(a), 0.8, Math.sin(a)).normalize().multiply(0.4);
   }

   public static class Weakened extends TemporaryListener {
      private final Entity entity;
      private final double coef;

      public Weakened(Entity entity, double ratio, double duration) {
         super();
         this.entity = entity;
         this.coef = 1.0 + ratio / 100.0;
         new SmallParticleEffect(entity, VParticle.WITCH.get());
         this.close((long)(duration * 20.0));
      }

      @EventHandler
      public void a(AttackEvent event) {
         if (event.getEntity().equals(this.entity)) {
            event.getEntity()
               .getWorld()
               .spawnParticle(VParticle.WITCH.get(), this.entity.getLocation().add(0.0, this.entity.getHeight() / 2.0, 0.0), 16, 0.5, 0.5, 0.5, 0.0);
            event.getDamage().multiplicativeModifier(this.coef);
         }
      }
   }
}
