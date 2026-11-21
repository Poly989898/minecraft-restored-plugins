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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@MythicMechanic(
   author = "YourName",
   name = "beam",
   description = "Creates a laser beam between two points"
)
public class BeamMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "onHitSkill",
      aliases = {"onhit", "oh"},
      description = "The skill to execute when the beam hits an entity"
   )
   protected Optional<Skill> onHitSkill = Optional.empty();
   @MythicField(
      name = "onTickSkill",
      aliases = {"ontick", "ot"},
      description = "The skill to execute each tick"
   )
   protected Optional<Skill> onTickSkill = Optional.empty();
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "Duration of the beam in ticks",
      defValue = "20"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "tickInterval",
      aliases = {"i"},
      description = "Tick interval of the beam",
      defValue = "1"
   )
   protected PlaceholderDuration tickInterval;
   @MythicField(
      name = "material",
      aliases = {"m"},
      description = "Material of the beam",
      defValue = "END_ROD"
   )
   protected Material material;
   @MythicField(
      name = "rotationSpeed",
      aliases = {"rs"},
      description = "Rotation speed of the beam in degrees per tick",
      defValue = "0"
   )
   protected PlaceholderFloat rotationSpeed;
   @MythicField(
      name = "hitRadius",
      aliases = {"r"},
      description = "Hit radius of the beam",
      defValue = "1.0"
   )
   protected double hitRadius;
   @MythicField(
      name = "startYOffset",
      aliases = {"syo"},
      description = "Start Y offset",
      defValue = "0"
   )
   protected PlaceholderDouble startYOffset;
   @MythicField(
      name = "endYOffset",
      aliases = {"eyo"},
      description = "End Y offset",
      defValue = "0"
   )
   protected PlaceholderDouble endYOffset;
   protected String onHitSkillName;
   protected String onTickSkillName;

   public BeamMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onHitSkillName = mlc.getString(new String[]{"onhitskill", "onhit", "oh"});
      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot"});
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "20");
      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"tickinterval", "interval", "i"}, DurationUnit.TICKS, "1");
      String materialName = mlc.getString(new String[]{"material", "m"}, "END_ROD").toUpperCase();
      this.material = Material.getMaterial(materialName);
      if (this.material == null || !this.material.isBlock()) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid material specified, defaulting to END_ROD.");
         this.material = Material.END_ROD;
      }

      this.rotationSpeed = mlc.getPlaceholderFloat(new String[]{"rotationspeed", "rs"}, 0.0F);
      this.hitRadius = mlc.getDouble(new String[]{"hitradius", "radius", "r"}, 1.0);
      this.startYOffset = mlc.getPlaceholderDouble(new String[]{"startyoffset", "syo"}, 0.0);
      this.endYOffset = mlc.getPlaceholderDouble(new String[]{"endyoffset", "eyo"}, 0.0);
      this.getManager().queueSecondPass(() -> {
         if (this.onHitSkillName != null) {
            this.onHitSkill = this.getManager().getSkill(file, this, this.onHitSkillName);
         }

         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getManager().getSkill(file, this, this.onTickSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      try {
         new BeamMechanic.BeamTracker(data, target);
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.errorMechanic(this, "An error occurred while casting BeamMechanic.", e);
         return SkillResult.ERROR;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   public class BeamTracker implements IParentSkill, Runnable, Terminable, TerminableConsumer {
      private final TerminableRegistry terminables = TerminableRegistry.create();
      private Task task;
      private final SkillMetadata data;
      private final AbstractLocation startLocation;
      private final AbstractLocation endLocation;
      private final Display blockDisplay;
      private final double beamLength;
      private final Vector3f beamScale;
      private int ticksRemaining;
      private final int tickInterval;
      private float currentRotation;

      public BeamTracker(SkillMetadata data, AbstractLocation target) {
         this.data = data.deepClone();
         this.data.setCallingEvent(this);
         this.data.setIsAsync(false);
         AbstractLocation casterLocation = data.getCaster().getLocation().clone();
         this.startLocation = casterLocation.add(0.0, BeamMechanic.this.startYOffset.get(data), 0.0);
         this.endLocation = target.clone().add(0.0, BeamMechanic.this.endYOffset.get(data), 0.0);
         this.beamLength = this.startLocation.distance(this.endLocation);
         this.beamScale = new Vector3f(1.0F, 1.0F, (float)this.beamLength);
         this.currentRotation = 0.0F;
         this.ticksRemaining = BeamMechanic.this.duration.get(data).getTicks();
         this.tickInterval = Math.max(1, BeamMechanic.this.tickInterval.get(data).getTicks());
         this.blockDisplay = this.createBeamDisplay();
         this.start();
      }

      private void start() {
         this.task = Schedulers.of(BukkitAdapter.adapt(this.startLocation)).runRepeating(this, 0L, (long)this.tickInterval);
         this.task.bindWith(this);
      }

      private Display createBeamDisplay() {
         AbstractLocation midPoint = this.startLocation.clone().add(this.endLocation).multiply(0.5);
         BlockDisplay display = (BlockDisplay)BukkitAdapter.adapt(midPoint).getWorld().spawnEntity(BukkitAdapter.adapt(midPoint), EntityType.BLOCK_DISPLAY);
         display.setBlock(BeamMechanic.this.material.createBlockData());
         Vector direction = BukkitAdapter.adapt(this.endLocation).toVector().subtract(BukkitAdapter.adapt(this.startLocation).toVector()).normalize();
         Vector3f jomlDirection = new Vector3f((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
         Quaternionf rotationQuaternion = new Quaternionf().lookAlong(jomlDirection, new Vector3f(0.0F, 1.0F, 0.0F));
         Transformation transformation = new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), rotationQuaternion, this.beamScale, new Quaternionf());
         display.setTransformation(transformation);
         return display;
      }

      @Override
      public void run() {
         if (this.ticksRemaining <= 0) {
            this.terminate();
         } else {
            float rotationSpeedValue = BeamMechanic.this.rotationSpeed.get(this.data);
            if (rotationSpeedValue != 0.0F) {
               this.currentRotation += rotationSpeedValue;
               if (this.currentRotation >= 360.0F) {
                  this.currentRotation -= 360.0F;
               }

               this.updateBeamRotation();
            }

            this.detectHits();
            if (BeamMechanic.this.onTickSkill.isPresent() && BeamMechanic.this.onTickSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               sData.setOrigin(this.startLocation);
               BeamMechanic.this.onTickSkill.get().execute(sData);
            }

            this.ticksRemaining = this.ticksRemaining - this.tickInterval;
         }
      }

      private void updateBeamRotation() {
         Quaternionf rightRotation = new Quaternionf().rotateY((float)Math.toRadians(this.currentRotation));
         Transformation transformation = this.blockDisplay.getTransformation();
         Transformation newTransformation = new Transformation(
            transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale(), rightRotation
         );
         this.blockDisplay.setTransformation(newTransformation);
      }

      private void detectHits() {
      }

      @Override
      public void close() {
         if (!this.terminables.hasTerminated() && this.blockDisplay != null && !this.blockDisplay.isDead()) {
            this.blockDisplay.remove();
         }

         this.terminables.closeAndReportException();
      }

      @Override
      public <T extends AutoCloseable> T bind(T terminable) {
         this.terminables.accept((Terminable)terminable);
         return terminable;
      }

      @Override
      public void setCancelled() {
         this.terminate();
      }

      @Override
      public boolean getCancelled() {
         return this.terminables.hasTerminated();
      }
   }
}
