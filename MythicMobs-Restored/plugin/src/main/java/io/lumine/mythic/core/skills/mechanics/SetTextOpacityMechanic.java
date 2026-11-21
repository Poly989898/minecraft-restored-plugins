package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.projectiles.bullet.ITextBulletTracker;
import io.lumine.mythic.core.utils.TextOpacityFade;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.TextDisplay;

@MythicMechanic(
   name = "setTextOpacity",
   aliases = {"textOpacity"},
   description = "Fades the text opacity of a Text Display entity or TEXT bullet"
)
public class SetTextOpacityMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   @MythicField(
      name = "opacity",
      aliases = {"o", "alpha"},
      defValue = "255",
      description = "The text opacity from 0 to 255."
   )
   private final PlaceholderInt opacity;
   @MythicField(
      name = "interpolationDuration",
      aliases = {"duration", "d"},
      defValue = "-1",
      description = "The fade duration in ticks. A value of -1 uses the display's current interpolation duration."
   )
   private final PlaceholderInt interpolationDuration;

   public SetTextOpacityMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.opacity = mlc.getPlaceholderInteger(new String[]{"opacity", "o", "alpha"}, 255);
      this.interpolationDuration = mlc.getPlaceholderInteger(new String[]{"interpolationduration", "duration", "d"}, -1);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof TextDisplay display) {
         PlaceholderContext placeholderContext = PlaceholderContext.builder().meta(data).entity(target).build();
         int opacity = Math.clamp((long)this.opacity.get(placeholderContext), 0, 255);
         int configuredDuration = this.interpolationDuration.get(placeholderContext);
         onDisplayThread(display, () -> beginFade(display, opacity, configuredDuration));
         return SkillResult.SUCCESS;
      } else {
         return this.cast(data);
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      ITextBulletTracker bullet = ITextBulletTracker.of(data);
      if (bullet == null) {
         return SkillResult.INVALID_TARGET;
      } else {
         PlaceholderContext context = PlaceholderContext.builder().meta(data).build();
         bullet.setTextOpacity(Math.clamp((long)this.opacity.get(context), 0, 255), this.interpolationDuration.get(context));
         return SkillResult.SUCCESS;
      }
   }

   private static void beginFade(TextDisplay display, int opacity, int configuredDuration) {
      int from = display.getTextOpacity() & 255;
      int duration = configuredDuration >= 0 ? configuredDuration : display.getInterpolationDuration();

      for (TextOpacityFade.Step step : TextOpacityFade.plan(from, opacity, duration)) {
         if (step.delayTicks() == 0) {
            applyStep(display, step);
         } else {
            Schedulers.of(display).runLater(() -> {
               if (display.isValid() && (display.getTextOpacity() & 255) == step.expectedOpacity()) {
                  applyStep(display, step);
               }
            }, step.delayTicks());
         }
      }
   }

   private static void applyStep(TextDisplay display, TextOpacityFade.Step step) {
      display.setInterpolationDuration(step.interpolationDuration());
      display.setInterpolationDelay(0);
      display.setTextOpacity((byte)step.opacity());
   }

   private static void onDisplayThread(TextDisplay display, Runnable task) {
      if (ServerVersion.isFolia()) {
         Schedulers.runOnRegion(display, task);
      } else {
         Schedulers.ensureSync(task);
      }
   }
}
