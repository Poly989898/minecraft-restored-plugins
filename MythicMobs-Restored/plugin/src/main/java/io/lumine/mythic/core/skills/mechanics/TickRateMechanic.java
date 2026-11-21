package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   name = "tickRate",
   description = "Slows down the target. Players get a slower client, other entities are ticked less often by the server"
)
public class TickRateMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private static final String METADATA_KEY = "clienttickrate";
   @MythicField(
      name = "rate",
      aliases = {"r"},
      description = "The tick rate of the target, from 1 (slowest) to 20 (normal). 0 stops mobs completely and stops every non-player entity on a player's screen. Values above 20 have no effect",
      defValue = "20"
   )
   private final PlaceholderFloat rate;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "How long the change lasts. 0 keeps it until a reset. Players also go back to normal on respawn or world change",
      defValue = "0"
   )
   private final PlaceholderDuration duration;
   @MythicField(
      name = "reset",
      description = "Puts the target back to normal. Players get the server's tick rate",
      defValue = "false"
   )
   private final boolean reset;

   public TickRateMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.rate = mlc.getPlaceholderFloat(new String[]{"rate", "r"}, 20.0F);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
      this.reset = mlc.getBoolean(new String[]{"reset"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (ServerVersion.isBefore(MinecraftVersions.v1_20_4)) {
         return SkillResult.INVALID_VERSION;
      } else {
         VolatileEntityHandler handler = this.getPlugin().getVolatileCodeHandler().getEntityHandler();
         float value = this.rate.get(data, target);
         boolean stopped = value <= 0.0F;
         if (!target.isPlayer()) {
            float tickRate = stopped ? 0.0F : Math.max(1.0F, Math.min(20.0F, value));
            int ticks = this.duration.get(data, target).getTicks();
            Schedulers.runOnRegion(target.getBukkitEntity(), () -> this.setEntityTickRate(handler, target, tickRate, ticks));
            return SkillResult.SUCCESS;
         } else {
            AbstractPlayer player = target.asPlayer();
            target.getMetadata("clienttickrate").ifPresent(task -> ((Promise)task).cancel());
            target.removeMetadata("clienttickrate");
            if (this.reset) {
               handler.resetPlayerTickRate(player);
               return SkillResult.SUCCESS;
            } else {
               handler.setPlayerTickRate(player, value, stopped);
               int ticks = this.duration.get(data, target).getTicks();
               if (ticks > 0) {
                  target.setMetadata("clienttickrate", Schedulers.of(target.getBukkitEntity()).runLater(() -> {
                     target.removeMetadata("clienttickrate");
                     if (player.isOnline()) {
                        handler.resetPlayerTickRate(player);
                     }
                  }, ticks));
               }

               return SkillResult.SUCCESS;
            }
         }
      }
   }

   private void setEntityTickRate(VolatileEntityHandler handler, AbstractEntity target, float tickRate, int ticks) {
      TickRateMechanic.EntityTickRate previous = target.getMetadata("clienttickrate").orElse(null) instanceof TickRateMechanic.EntityTickRate state
         ? state
         : null;
      if (previous != null) {
         previous.task.stop();
      }

      if (this.reset) {
         target.removeMetadata("clienttickrate");
         if (previous != null) {
            previous.resume();
         }
      } else {
         TickRateMechanic.EntityTickRate statex = new TickRateMechanic.EntityTickRate(
            handler, target, tickRate, ticks > 0 ? ticks : -1, previous != null && previous.suspended
         );
         statex.task = Schedulers.of(target.getBukkitEntity()).runRepeating(statex, 1L, 1L);
         target.setMetadata("clienttickrate", statex);
      }
   }

   private static final class EntityTickRate implements Runnable {
      private final VolatileEntityHandler handler;
      private final AbstractEntity entity;
      private final float rate;
      private int remaining;
      private boolean suspended;
      private float budget;
      private Task task;

      private EntityTickRate(VolatileEntityHandler handler, AbstractEntity entity, float rate, int remaining, boolean suspended) {
         this.handler = handler;
         this.entity = entity;
         this.rate = rate;
         this.remaining = remaining;
         this.suspended = suspended;
      }

      @Override
      public void run() {
         if (this.remaining != 0 && this.entity.isValid()) {
            if (this.remaining > 0) {
               this.remaining--;
            }

            this.budget = this.budget + this.rate / 20.0F;
            if (this.budget >= 1.0F) {
               this.budget--;
               this.resume();
            } else if (this.handler.setEntityTicking(this.entity, false)) {
               this.suspended = true;
            }
         } else {
            this.task.stop();
            this.entity.removeMetadata("clienttickrate");
            this.resume();
         }
      }

      private void resume() {
         if (this.suspended) {
            this.suspended = false;
            this.handler.setEntityTicking(this.entity, true);
         }
      }
   }
}
