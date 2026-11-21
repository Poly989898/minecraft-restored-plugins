package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

@MythicMechanic(
   author = "Ashijin",
   name = "raytrace",
   premium = true,
   aliases = {},
   description = "Executes a skill with the result of a raytrace"
)
public class RaytraceMechanic extends SkillMechanic implements IMetaSkill {
   protected String strEntitySkill;
   protected String strLocationSkill;
   protected String strHeadshotSkill;
   protected Optional<Skill> entitySkill = Optional.empty();
   protected Optional<Skill> locationSkill = Optional.empty();
   protected Optional<Skill> headshotSkill = Optional.empty();
   protected String rtConditionString;
   protected PlaceholderDouble maxDistance;
   protected PlaceholderDouble rayWidth;
   protected boolean ignorePassableBlocks;
   protected boolean ignoreEntities;
   protected FluidCollisionMode fluidCollisionMode;
   protected float projectileVelocityAccuracy;
   protected float projectileVelocityVertNoise;
   protected float projectileVelocityHorizNoise;
   protected float projectileVelocityVertNoiseBase;
   protected float projectileVelocityHorizNoiseBase;
   protected PlaceholderFloat headshotMultiplier;
   @MythicField(
      name = "raytraceConditions",
      aliases = {"rconditions", "rcond", "rc"},
      defValue = "NONE",
      version = "4.10",
      description = "Conditions applied to the bounce target"
   )
   protected List<SkillCondition> raytraceConditions = null;

   public RaytraceMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, mlc, "RayTrace mechanic requires Mythic Premium");
      } else {
         this.strEntitySkill = mlc.getString(new String[]{"entityskill", "eskill", "es"}, null);
         this.strLocationSkill = mlc.getString(new String[]{"locationskill", "lskill", "ls"}, null);
         this.strHeadshotSkill = mlc.getString(new String[]{"headshotskill", "hsskill", "hs"}, null);
         this.maxDistance = mlc.getPlaceholderDouble(new String[]{"maxdistance", "distance", "md", "d"}, 50.0);
         this.rayWidth = mlc.getPlaceholderDouble(new String[]{"raywidth", "rw", "w"}, 0.2);
         this.ignorePassableBlocks = mlc.getBoolean(new String[]{"ignorepassableblocks", "ignorepassable", "ip"}, true);
         this.ignoreEntities = mlc.getBoolean(new String[]{"ignoreentities", "ignoreentity", "ie"}, false);

         try {
            String strFluidCollisionMode = mlc.getString(new String[]{"fluidcollisionmode", "fcm"}, "NEVER");
            this.fluidCollisionMode = FluidCollisionMode.valueOf(strFluidCollisionMode.toUpperCase());
         } catch (Error | Exception e) {
            this.fluidCollisionMode = FluidCollisionMode.NEVER;
         }

         this.projectileVelocityAccuracy = mlc.getFloat(new String[]{"accuracy", "ac", "a"}, 1.0F);
         float defNoise = (1.0F - this.projectileVelocityAccuracy) * 45.0F;
         this.projectileVelocityVertNoise = mlc.getFloat(new String[]{"verticalnoise", "vn"}, defNoise) / 10.0F;
         this.projectileVelocityHorizNoise = mlc.getFloat(new String[]{"horizontalnoise", "hn"}, defNoise) * 5.0F;
         this.projectileVelocityVertNoiseBase = 0.0F - this.projectileVelocityVertNoise / 2.0F;
         this.projectileVelocityHorizNoiseBase = 0.0F - this.projectileVelocityHorizNoise / 2.0F;
         this.headshotMultiplier = mlc.getPlaceholderFloat(new String[]{"headshotmultiplier", "hsmultipler", "hsm"}, 1.0F);
         this.rtConditionString = mlc.getString(new String[]{"raytraceConditions", "rconditions", "rcond", "rc"}, null);
         this.getManager().queueSecondPass(() -> {
            if (this.strEntitySkill != null) {
               this.entitySkill = this.getManager().getSkill(file, this, this.strEntitySkill);
               if (this.entitySkill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find RayTrace Entity MetaSkill " + this.entitySkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded RayTraceMechanic EntitySkill successfully");
               }
            }

            if (this.strLocationSkill != null) {
               this.locationSkill = this.getManager().getSkill(file, this, this.strLocationSkill);
               if (this.locationSkill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find RayTrace Location MetaSkill " + this.locationSkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded RayTraceMechanic LocationSkill successfully");
               }
            }

            if (this.strHeadshotSkill != null) {
               this.headshotSkill = this.getManager().getSkill(file, this, this.strHeadshotSkill);
               if (this.headshotSkill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find RayTrace Location MetaSkill " + this.headshotSkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded RayTraceMechanic LocationSkill successfully");
               }
            }

            if (this.rtConditionString != null) {
               this.raytraceConditions = this.getPlugin().getSkillManager().getConditions(this.rtConditionString);
            }
         });
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "RayTrace mechanic requires Mythic Premium");
         return SkillResult.REQUIRES_PREMIUM;
      } else {
         LivingEntity caster = (LivingEntity)data.getCaster().getEntity().getBukkitEntity();
         AbstractLocation aSLocation = data.getCaster().getEntity().getEyeLocation();
         Location location = BukkitAdapter.adapt(aSLocation);
         AbstractVector aDirection = aSLocation.getDirection();
         if (this.projectileVelocityHorizNoise > 0.0F) {
            float noise = (float)(this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * this.projectileVelocityHorizNoise);
            aDirection.rotate(noise);
         }

         if (this.projectileVelocityVertNoise > 0.0F) {
            float noise = (float)(this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * this.projectileVelocityVertNoise);
            aDirection.add(new AbstractVector(0.0F, noise, 0.0F)).normalize();
         }

         Optional<AbstractModelEngineSupport> megSupport = this.getPlugin().getCompatibility().getModelEngine();
         RayTraceResult result = this.getPlugin()
            .getVolatileCodeHandler()
            .getWorldHandler()
            .rayTrace(location, BukkitAdapter.adapt(aDirection), this.maxDistance.get(data), this.fluidCollisionMode, this.rayWidth.get(data), entity -> {
               if (this.ignoreEntities) {
                  return false;
               } else if (entity.getUniqueId().equals(caster.getUniqueId())) {
                  return false;
               } else {
                  if (megSupport.isPresent()) {
                     AbstractModelEngineSupport meg = megSupport.get();
                     if (meg.getParentUUID(entity.getUniqueId()).equals(caster.getUniqueId())) {
                        return false;
                     }
                  }

                  if (this.raytraceConditions != null) {
                     for (SkillCondition condition : this.raytraceConditions) {
                        if (!condition.evaluateEntity(BukkitAdapter.adapt(entity))) {
                           return false;
                        }
                     }
                  }

                  return true;
               }
            }, material -> this.ignorePassableBlocks ? !material.isSolid() : false);
         if (result != null) {
            if (this.entitySkill.isPresent() && result.getHitEntity() != null) {
               AbstractEntity aEntity = BukkitAdapter.adapt(result.getHitEntity());
               boolean headshot = false;
               if (result.getHitPosition().getY() - aEntity.getLocation().getY() >= result.getHitEntity().getBoundingBox().getHeight() - 0.4) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Mechanic was a headshot");
                  data.setPower(data.getPower() * this.headshotMultiplier.get(data));
                  headshot = true;
               }

               SkillMetadata entityData = data.deepClone().setEntityTarget(aEntity);
               entityData.setOrigin(aSLocation);
               Skill entitySkill = this.entitySkill.get();
               if (this.headshotSkill.isPresent() && headshot) {
                  Skill headshotSkill = this.headshotSkill.get();
                  if (headshotSkill.isUsable(entityData)) {
                     headshotSkill.execute(entityData);
                  }
               }

               if (entitySkill.isUsable(entityData)) {
                  entitySkill.execute(entityData);
               }
            }

            if (this.locationSkill.isPresent()) {
               Location loc = result.getHitPosition().toLocation(caster.getWorld());
               AbstractLocation aLocation = BukkitAdapter.adapt(loc);
               SkillMetadata locationData = data.deepClone().setLocationTarget(aLocation);
               locationData.setOrigin(aSLocation);
               if (result.getHitBlock() != null) {
                  locationData.getVariables().putString("hit-block-type", result.getHitBlock().getType().toString());
               } else {
                  locationData.getVariables().putString("hit-block-type", "AIR");
               }

               Skill skill = this.locationSkill.get();
               if (skill.isUsable(locationData)) {
                  skill.execute(locationData);
               }
            }
         } else if (this.locationSkill.isPresent()) {
            AbstractLocation aLocationx = BukkitAdapter.adapt(
               ((Player)BukkitAdapter.adapt(data.getCaster().getEntity()))
                  .getTargetBlock(this.getPlugin().getConfiguration().getTransparentBlocks(), (int)this.maxDistance.get(data))
                  .getLocation()
            );
            SkillMetadata locationDatax = data.deepClone().setLocationTarget(aLocationx);
            Skill skill = this.locationSkill.get();
            if (skill.isUsable(locationDatax)) {
               skill.execute(locationDatax);
            }
         }

         return SkillResult.SUCCESS;
      }
   }
}
