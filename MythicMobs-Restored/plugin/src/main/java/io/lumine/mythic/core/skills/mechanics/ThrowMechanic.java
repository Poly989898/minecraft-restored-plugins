package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.triggers.meta.ThrownMetadata;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "throw",
   description = "Throws the target entity"
)
public class ThrowMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderFloat velocity;
   protected PlaceholderFloat velocityY;
   private PlaceholderBoolean fromOrigin;
   private PlaceholderBoolean triggers;

   public ThrowMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 1.0F);
      this.velocityY = mlc.getPlaceholderFloat(new String[]{"velocityy", "yvelocity", "vy", "yv"}, 1.0F);
      this.fromOrigin = mlc.getPlaceholderBoolean(new String[]{"fromorigin", "fo"}, false);
      this.triggers = mlc.getPlaceholderBoolean(new String[]{"triggers", "t"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      float throwStrength = this.velocity.get(data, target);
      float throwStrengthY = this.velocityY.get(data, target);
      AbstractLocation location = this.fromOrigin.get(data, target) ? data.getOrigin() : data.getCaster().getLocation();
      AbstractVector appliedVelocity = SkillAdapter.get().throwSkill(location, target, throwStrength / 10.0F, throwStrengthY / 10.0F);
      if (this.triggers.get(data, target)) {
         ActiveMob thrownMob = this.getPlugin().getMobManager().getMythicMobInstance(target);
         if (thrownMob != null) {
            ThrownMetadata meta = new ThrownMetadata(data.getCaster().getEntity(), appliedVelocity, throwStrength, throwStrengthY);
            SkillMetadata triggerData = this.getPlugin()
               .getSkillManager()
               .getEventBus()
               .buildSkillMetadata(SkillTriggers.THROWN_BY_MECHANIC, meta, thrownMob, data.getCaster().getEntity(), thrownMob.getLocation(), true);
            Schedulers.of(target.getBukkitEntity()).run(() -> this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(triggerData, meta));
         }
      }

      return SkillResult.SUCCESS;
   }
}
