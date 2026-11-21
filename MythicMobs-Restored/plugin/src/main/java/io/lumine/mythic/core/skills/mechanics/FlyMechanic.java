package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "fly",
   description = "Aura that enables flying on the target entity",
   deprecated = true
)
public class FlyMechanic extends Aura implements ITargetedEntitySkill {
   public FlyMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         new FlyMechanic.FlyMechanicTracker(target, data);
      }

      return SkillResult.SUCCESS;
   }

   private class FlyMechanicTracker extends Aura.AuraTracker {
      private AbstractPlayer target;
      private boolean couldFly;

      public FlyMechanicTracker(AbstractEntity target, SkillMetadata data) {
         super(target, data);
         this.target = target.asPlayer();
         this.start();
      }

      @Override
      public void auraStart() {
         this.executeAuraSkill(FlyMechanic.this.onStartSkill, this.skillMetadata);
         Schedulers.runOnRegion(this.target.getBukkitEntity(), () -> {
            this.couldFly = this.target.getAllowFlight();
            this.target.setAllowFlight(true);
            this.target.setFlying(true);
         });
      }

      @Override
      public void auraStop() {
         Schedulers.runOnRegion(this.target.getBukkitEntity(), () -> {
            this.target.setFlying(false);
            this.target.setAllowFlight(this.couldFly);
         });
         this.executeAuraSkill(FlyMechanic.this.onEndSkill, this.skillMetadata);
      }
   }
}
