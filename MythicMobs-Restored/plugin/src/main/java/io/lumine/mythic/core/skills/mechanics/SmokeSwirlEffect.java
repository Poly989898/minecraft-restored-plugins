package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:smokeswirl",
   aliases = {"smokeswirl", "e:smokeswirl"}
)
public class SmokeSwirlEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderDuration duration;
   protected PlaceholderDuration interval;

   public SmokeSwirlEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "5");
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "i"}, DurationUnit.TICKS, "1");
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new SmokeSwirlEffect.Animator(BukkitAdapter.adapt(target), Math.max(1, this.interval.get(data).getTicks()), this.duration.get(data).getTicks());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new SmokeSwirlEffect.Animator(
         BukkitAdapter.adapt(target), Math.max(1, this.interval.get(data, target).getTicks()), this.duration.get(data, target).getTicks()
      );
      return SkillResult.SUCCESS;
   }

   private class Animator implements Runnable {
      private Entity entity;
      private Location location;
      private int interval;
      private int duration;
      private int iteration;
      private int taskId;
      private int[] x = new int[]{1, 1, 0, -1, -1, -1, 0, 1};
      private int[] z = new int[]{0, 1, 1, 1, 0, -1, -1, -1};
      private int[] v = new int[]{7, 6, 3, 0, 1, 2, 5, 8};

      public Animator(Location location, int interval, int duration) {
         this(interval, duration);
         this.location = location;
      }

      public Animator(Entity entity, int interval, int duration) {
         this(interval, duration);
         this.entity = entity;
      }

      protected Animator(int interval, int duration) {
         this.interval = interval;
         this.duration = duration;
         this.iteration = 0;
         this.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(MythicBukkit.inst(), this, 0L, interval);
      }

      @Override
      public void run() {
         if (this.iteration * this.interval > this.duration) {
            Bukkit.getScheduler().cancelTask(this.taskId);
         } else {
            int i = this.iteration % 8;
            Location loc;
            if (this.location != null) {
               loc = this.location;
            } else {
               loc = this.entity.getLocation();
            }

            loc.getWorld().playEffect(loc.clone().add(this.x[i], 0.0, this.z[i]), Effect.SMOKE, this.v[i]);
            this.iteration++;
         }
      }
   }
}
