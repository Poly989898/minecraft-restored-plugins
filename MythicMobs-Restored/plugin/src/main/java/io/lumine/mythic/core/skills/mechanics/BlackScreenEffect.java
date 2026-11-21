package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   version = "4.8",
   name = "blackScreen",
   aliases = {"effect:blackScreen", "e:blackScreen"},
   description = "Causes the player's screen to black out"
)
public class BlackScreenEffect extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "duration",
      aliases = {"d"},
      version = "4.8",
      description = "How long the effect should last"
   )
   private int duration;
   @MythicField(
      name = "cancel",
      aliases = {"c"},
      version = "4.8",
      description = "If true, will cancel any existing effects immediately"
   )
   private boolean cancel;

   public BlackScreenEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.auraName = Optional.of(PlaceholderString.of("#blackScreen"));
      this.maxStacks = PlaceholderInt.of("1");
      this.refreshDuration = true;
      this.interval = PlaceholderDuration.of("10", DurationUnit.TICKS);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         new BlackScreenEffect.BlackScreenEffectTracker(target, data);
      }

      return SkillResult.SUCCESS;
   }

   public class BlackScreenEffectTracker extends Aura.AuraTracker {
      public BlackScreenEffectTracker(AbstractEntity target, SkillMetadata data) {
         super(target, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.executeAuraSkill(BlackScreenEffect.this.onStartSkill, this.skillMetadata);
         BlackScreenEffect.this.getPlugin().getVolatileCodeHandler().getEntityHandler().forcePlayCredits(this.entity.get().asPlayer(), 1.0F);
      }

      @Override
      public void auraTick() {
         BlackScreenEffect.this.getPlugin().getVolatileCodeHandler().getEntityHandler().forcePlayCredits(this.entity.get().asPlayer(), 1.0F);
         this.executeAuraSkill(BlackScreenEffect.this.onTickSkill, this.skillMetadata);
      }

      @Override
      public void auraStop() {
         BlackScreenEffect.this.getPlugin().getVolatileCodeHandler().getEntityHandler().forceCloseWindow(this.entity.get().asPlayer());
         this.executeAuraSkill(BlackScreenEffect.this.onEndSkill, this.skillMetadata);
      }
   }
}
