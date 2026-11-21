package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.particles.Laser;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.Location;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:guardianBeam",
   aliases = {"guardianbeam", "e:guardianbeam", "effect:beam", "e:beam"},
   description = "Draws a guardian beam between the origin and the target"
)
public class GuardianBeamEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "onTickSkill",
      aliases = {"ontick", "ot"},
      description = "The meta-skill to execute every tick"
   )
   protected Optional<Skill> onTickSkill = Optional.empty();
   @MythicField(
      name = "onEndSkill",
      aliases = {"onend", "oe"},
      description = "The meta-skill to execute when the beam ends"
   )
   protected Optional<Skill> onEndSkill = Optional.empty();
   @MythicField(
      name = "onStartSkill",
      aliases = {"onstart", "os"},
      description = "The meta-skill to execute when the beam starts"
   )
   protected Optional<Skill> onStartSkill = Optional.empty();
   protected String onTickSkillName;
   protected String onEndSkillName;
   protected String onStartSkillName;
   @MythicField(
      name = "duration",
      aliases = {"maxduration", "md", "d"},
      description = "The duration of the guardian beam effect",
      defValue = "400"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "interval",
      aliases = {"int", "i"},
      description = "How often the guardian beam effect is ticked",
      defValue = "1"
   )
   protected PlaceholderDuration tickInterval;
   @MythicField(
      name = "startYOffset",
      aliases = {"syo"},
      description = "The start y-offset of the guardian beam effect",
      defValue = "1"
   )
   protected PlaceholderFloat startYOffset;
   @MythicField(
      name = "targetYOffset",
      aliases = {"tyo"},
      description = "The target y-offset of the guardian beam effect",
      defValue = "1"
   )
   protected PlaceholderFloat targetYOffset;
   @MythicField(
      name = "fromOrigin",
      aliases = {"fo"},
      description = "Sets the starting location of the guardian beam effect to the origin",
      defValue = "false"
   )
   protected boolean fromOrigin;

   public GuardianBeamEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"interval", "int", "i"}, DurationUnit.TICKS, "1");
      this.duration = mlc.getPlaceholderDuration(new String[]{"maxduration", "duration", "md", "d"}, DurationUnit.TICKS, "400");
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo"}, 1.0F);
      this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo"}, 0.0F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.onStartSkillName = mlc.getString(new String[]{"onstartskill", "onstart", "os"});
      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot"});
      this.onEndSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"});
      this.getManager().queueSecondPass(() -> {
         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getManager().getSkill(file, this, this.onTickSkillName);
         }

         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getManager().getSkill(file, this, this.onEndSkillName);
         }

         if (this.onStartSkillName != null) {
            this.onStartSkill = this.getManager().getSkill(file, this, this.onStartSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new GuardianBeamEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new GuardianBeamEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   private class Animator implements IParentSkill, Runnable, Terminable {
      protected final TerminableRegistry components = TerminableRegistry.create();
      protected SkillMetadata data;
      @Getter
      protected int ticksRemaining;
      protected int tickInterval;
      protected AbstractEntity target = null;
      protected AbstractLocation targetLocation = null;
      protected Laser.GuardianLaser beam;

      public Animator(SkillMetadata data, AbstractEntity entity) {
         this.target = entity;
         this.data = data.deepClone();
         this.data.setCallingEvent(this);
         this.data.setIsAsync(true);
         this.ticksRemaining = GuardianBeamEffect.this.duration.get(data, entity).getTicks();
         this.tickInterval = Math.max(1, GuardianBeamEffect.this.tickInterval.get(data, entity).getTicks());
         this.start();
      }

      public Animator(SkillMetadata data, AbstractLocation location) {
         this.targetLocation = location;
         this.data = data.deepClone();
         this.data.setCallingEvent(this);
         this.data.setIsAsync(true);
         this.ticksRemaining = GuardianBeamEffect.this.duration.get(data).getTicks();
         this.tickInterval = Math.max(1, GuardianBeamEffect.this.tickInterval.get(data).getTicks());
         this.start();
      }

      protected void start() {
         Location start = GuardianBeamEffect.this.fromOrigin
            ? BukkitAdapter.adapt(this.data.getOrigin())
            : BukkitAdapter.adapt(this.data.getCaster().getLocation());
         Location end = this.target == null ? BukkitAdapter.adapt(this.targetLocation) : BukkitAdapter.adapt(this.target.getLocation());
         start = start.add(0.0, GuardianBeamEffect.this.startYOffset.get(this.data), 0.0);
         end = end.add(0.0, GuardianBeamEffect.this.targetYOffset.get(this.data), 0.0);

         try {
            this.beam = new Laser.GuardianLaser(start, end, -1, -1);
         } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
         }

         this.beam.start(GuardianBeamEffect.this.getPlugin());
         this.doStart();
         this.components.accept(Schedulers.sync().runRepeating(this, 0L, (long)this.tickInterval));
      }

      @Override
      public void run() {
         this.ticksRemaining = this.ticksRemaining - this.tickInterval;
         if (this.data.getCaster() != null && this.data.getCaster().getEntity().isDead()) {
            this.terminate();
         } else if (this.ticksRemaining <= 0) {
            this.terminate();
         } else {
            if (this.target != null) {
               this.targetLocation = this.target.getLocation();
               this.beam.moveEnd(BukkitAdapter.adapt(this.targetLocation), this.tickInterval, () -> {});
            }

            this.doTick();
         }
      }

      public void doStart() {
         this.executeSkill(GuardianBeamEffect.this.onStartSkill, this.data);
      }

      public void doTick() {
         this.executeSkill(GuardianBeamEffect.this.onTickSkill, this.data);
      }

      public void doEnd() {
         this.executeSkill(GuardianBeamEffect.this.onEndSkill, this.data);
      }

      @Override
      public void close() {
         if (!this.components.hasTerminated()) {
            this.beam.stop();
            this.doEnd();
         }

         this.components.closeAndReportException();
      }

      @Override
      public void setCancelled() {
         this.terminate();
      }

      @Override
      public boolean getCancelled() {
         return this.components.hasTerminated();
      }

      public boolean executeSkill(Optional<Skill> skill, SkillMetadata data) {
         if (!skill.isPresent()) {
            return false;
         } else {
            data = data.deepClone();
            data.setOrigin(this.targetLocation);
            if (skill.get().isUsable(data)) {
               skill.get().execute(data);
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
