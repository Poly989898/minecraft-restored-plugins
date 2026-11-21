package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:spin",
   aliases = {"spin", "e:spin"},
   description = "Forces the target entity to spin"
)
public class SpinEffect extends Aura implements ITargetedEntitySkill {
   protected PlaceholderInt velocity;

   public SpinEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.velocity = mlc.getPlaceholderInteger(new String[]{"velocity", "v"}, 18);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new SpinEffect.Animator(target, data);
      return SkillResult.SUCCESS;
   }

   private class Animator extends Aura.AuraTracker {
      private float yaw;
      private int velocity;

      public Animator(AbstractEntity target, SkillMetadata data) {
         super(target, data);
         this.velocity = SpinEffect.this.velocity.get(data, target);
         this.yaw = target.getLocation().getYaw();
         this.start();
      }

      @Override
      public void auraTick() {
         AbstractEntity target = this.entity.get();
         this.yaw = (this.yaw + this.velocity) % 360.0F;
         AbstractLocation l = target.getLocation();
         l.setYaw(this.yaw);
         Schedulers.runOnRegion(target.getBukkitEntity(), () -> {
            Vector v = target.getBukkitEntity().getVelocity();
            v.setY(v.getY() + 0.01);
            target.teleport(l);
            target.getBukkitEntity().setVelocity(v);
         });
      }
   }
}
