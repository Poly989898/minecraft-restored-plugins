package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "BerndiVader",
   version = "4.8",
   name = "bloodyScreen",
   aliases = {"effect:bloodyScreen", "e:bloodyScreen", "redScreen", "effect:redScreen", "e:redScreen"},
   description = "Causes the player's screen to be covered in blood"
)
public class BloodyScreenEffect extends SkillMechanic implements ITargetedEntitySkill {
   private static final AbstractLocation LOCATION = new AbstractLocation(99999.0, 0.0, 99999.0);
   @MythicField(
      name = "duration",
      aliases = {"d"},
      version = "4.8",
      description = "How long the effect should last"
   )
   private final PlaceholderDuration duration;
   @MythicField(
      name = "cancel",
      aliases = {"c"},
      version = "4.8",
      description = "If true, will cancel any existing effects immediately"
   )
   private final boolean cancel;

   public BloodyScreenEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
      this.cancel = mlc.getBoolean(new String[]{"cancel", "c"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Optional<Object> maybeTask = target.getMetadata("bloodyscreeneffect");
         if (maybeTask.isPresent()) {
            Promise task = (Promise)maybeTask.get();
            task.cancel(true);
            target.removeMetadata("bloodyscreeneffect");
         }

         int duration = this.duration.get(data, target).getTicks();
         if (this.cancel) {
            this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerWorldBorder(target.asPlayer(), null, -1);
            target.removeMetadata("bloodyscreeneffect");
            return SkillResult.SUCCESS;
         } else {
            if (duration == 0) {
               this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerWorldBorder(target.asPlayer(), LOCATION, 1);
            } else {
               this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerWorldBorder(target.asPlayer(), LOCATION, 1);
               target.setMetadata("bloodyscreeneffect", Schedulers.of(target.getBukkitEntity()).runLater(() -> {
                  if (target.asPlayer().isOnline()) {
                     this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerWorldBorder(target.asPlayer(), null, -1);
                     target.removeMetadata("bloodyscreeneffect");
                  }
               }, duration));
            }

            return SkillResult.SUCCESS;
         }
      }
   }
}
