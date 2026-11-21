package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:enderbeam",
   aliases = {"enderbeam"},
   description = "Creates an endercrystal beam pointing at the target"
)
public class EnderBeamMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration of the enderbeam effect",
      defValue = "60"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "yOffset",
      aliases = {"yo", "y"},
      description = "The y-offset of the enderbeam effect",
      defValue = "0"
   )
   protected PlaceholderFloat yOffset;

   public EnderBeamMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "60");
      this.yOffset = mlc.getPlaceholderFloat(new String[]{"yoffset", "yo", "y"}, 0.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new EnderBeamMechanic.Animator(BukkitAdapter.adapt(data.getCaster().getEntity()), BukkitAdapter.adapt(target), this.duration.get(data).getTicks());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new EnderBeamMechanic.Animator(BukkitAdapter.adapt(data.getCaster().getEntity()), BukkitAdapter.adapt(target), this.duration.get(data, target).getTicks());
      return SkillResult.SUCCESS;
   }

   private class Animator implements Runnable {
      private EnderCrystal crystal;
      private Location locationSource;
      private Entity entitySource;
      private Entity entity;
      private Location location;
      private int interval = 1;
      private int duration;
      private int iteration;
      private Task taskId;

      public Animator(Entity source, Location location, int duration) {
         this.entitySource = source;
         this.location = location;
         this.start(duration);
      }

      public Animator(Entity source, Entity entity, int duration) {
         this.entitySource = source;
         this.entity = entity;
         this.start(duration);
      }

      public Animator(Location source, Location location, int duration) {
         this.locationSource = source;
         this.location = location;
         this.start(duration);
      }

      public Animator(Location source, Entity entity, int duration) {
         this.locationSource = source;
         this.entity = entity;
         this.start(duration);
      }

      protected void start(int duration) {
         this.crystal = (EnderCrystal)this.entitySource
            .getWorld()
            .spawnEntity(this.entitySource.getLocation().add(0.0, EnderBeamMechanic.this.yOffset.get(), 0.0), VolatileEntityType.END_CRYSTAL);
         this.crystal.setShowingBottom(false);
         this.crystal.setInvulnerable(true);
         this.duration = duration;
         this.iteration = 0;
         this.taskId = Schedulers.of(this.crystal).runRepeating(this, 0L, (long)this.interval);
      }

      @Override
      public void run() {
         if (this.iteration > this.duration) {
            this.crystal.remove();
            this.taskId.terminate();
         } else {
            Location source;
            if (this.locationSource != null) {
               source = this.locationSource;
            } else {
               source = this.entitySource.getLocation();
            }

            source = source.add(0.0, EnderBeamMechanic.this.yOffset.get(), 0.0);
            Location target;
            if (this.location != null) {
               target = this.location;
            } else {
               target = this.entity.getLocation();
            }

            try {
               if (ServerVersion.isFolia()) {
                  this.crystal.teleportAsync(source);
               } else {
                  this.crystal.teleport(source);
               }

               this.crystal.setBeamTarget(target);
            } catch (Throwable e) {
               e.printStackTrace();
            }

            this.iteration++;
         }
      }
   }
}
