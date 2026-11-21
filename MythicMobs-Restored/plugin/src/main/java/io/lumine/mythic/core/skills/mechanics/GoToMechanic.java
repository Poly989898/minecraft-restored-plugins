package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

@MythicMechanic(
   author = "Ashijin",
   name = "goto",
   aliases = {"pathto", "navigateto"},
   description = "Path to the target location"
)
public class GoToMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "speed",
      aliases = {"s"},
      description = "The speed modifier",
      defValue = "1"
   )
   protected PlaceholderDouble speedModifier;
   @MythicFields({@MythicField(
         name = "spreadH",
         aliases = {"sh"},
         description = "The horizontal spread to the target location",
         defValue = "0"
      ), @MythicField(
         name = "spreadV",
         aliases = {"sv"},
         description = "The vertical spread to the target location",
         defValue = "0"
      )})
   protected PlaceholderDouble spreadH;
   @MythicFields({@MythicField(
         name = "spreadH",
         aliases = {"sh"},
         description = "The horizontal spread to the target location",
         defValue = "0"
      ), @MythicField(
         name = "spreadV",
         aliases = {"sv"},
         description = "The vertical spread to the target location",
         defValue = "0"
      )})
   protected PlaceholderDouble spreadV;
   private final boolean isStatic;
   private final AtomicLong lastInvalidWorldWarning = new AtomicLong();

   public GoToMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.speedModifier = mlc.getPlaceholderDouble(new String[]{"speed", "s"}, 1.0);
      this.spreadH = mlc.getPlaceholderDouble(new String[]{"spreadh", "sh"}, 0.0);
      this.spreadV = mlc.getPlaceholderDouble(new String[]{"spreadv", "sv"}, 0.0);
      this.isStatic = this.spreadH.isStaticallyEqualTo(0.0) && this.spreadV.isStaticallyEqualTo(0.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return target != null && target.isValid() ? this.castAtLocation(data, target.getLocation()) : SkillResult.INVALID_TARGET;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (data.getCaster() instanceof ActiveMob am && am.isDead()) {
         return SkillResult.INVALID_TARGET;
      } else {
         AbstractEntity caster = data.getCaster().getEntity();
         if (caster == null || !caster.isValid() || caster.isDead()) {
            return SkillResult.INVALID_TARGET;
         } else if (target != null && target.getWorld() != null && target.getWorld().isLoaded()) {
            PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
            if (!this.isStatic) {
               double spreadH = this.spreadH.get(context);
               double spreadV = this.spreadV.get(context);
               if (data.getCaster() instanceof ActiveMob am) {
                  target = MobExecutor.findSafeSpawnLocation(target, (int)spreadH, (int)spreadV, am.getType().getMythicEntity().getHeight() + 1);
               } else {
                  target = MobExecutor.findSafeSpawnLocation(target, (int)spreadH, (int)spreadV, 2);
               }
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Pathing to {0}", target.toString());
            this.getPlugin().getVolatileCodeHandler().getAIHandler().navigateToLocation(data.getCaster().getEntity(), target, this.speedModifier.get(context));
            return SkillResult.SUCCESS;
         } else {
            this.warnInvalidWorld(data, target);
            return SkillResult.INVALID_TARGET;
         }
      }
   }

   private void warnInvalidWorld(SkillMetadata data, AbstractLocation target) {
      long now = System.currentTimeMillis();
      long last = this.lastInvalidWorldWarning.get();
      if (now - last >= 60000L && this.lastInvalidWorldWarning.compareAndSet(last, now)) {
         String world = target != null && target.getWorld() != null ? target.getWorld().getName() : "<missing>";
         MythicLogger.error(
            data.getCaster(),
            "Skipping goto: destination world is missing or unloaded (world={0}, caster={1}, file={2}, skill={3})",
            world,
            data.getCaster().getEntity().getUniqueId(),
            this.getFilePath(),
            this.line
         );
      }
   }
}
