package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
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
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

@MythicMechanic(
   author = "Ashijin",
   name = "raytraceTo",
   premium = true,
   aliases = {},
   description = "Executes a skill with the result of a raytrace to the target location"
)
public class RaytraceToMechanic extends SkillMechanic implements ITargetedLocationSkill {
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
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat targetYOffset;
   protected boolean fromOrigin;
   protected boolean useEyeLocation;
   protected PlaceholderFloat startForwardOffset;
   protected PlaceholderFloat startSideOffset;

   public RaytraceToMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, mlc, "RayTraceTo mechanic requires Mythic Premium");
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
         this.getPlugin().getSkillManager().queueSecondPass(() -> {
            if (this.strEntitySkill != null) {
               this.entitySkill = this.getPlugin().getSkillManager().getSkill(file, this, this.strEntitySkill);
               if (this.entitySkill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find RayTrace Entity MetaSkill " + this.entitySkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded RayTraceMechanic EntitySkill successfully");
               }
            }

            if (this.strLocationSkill != null) {
               this.locationSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.strLocationSkill);
               if (this.locationSkill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find RayTrace Location MetaSkill " + this.locationSkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded RayTraceMechanic LocationSkill successfully");
               }
            }

            if (this.strHeadshotSkill != null) {
               this.headshotSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.strHeadshotSkill);
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
         this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo", "ystartoffset", "ys"}, 0.0F);
         this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo", "ytargetoffset", "yt"}, 0.0F);
         this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
         this.useEyeLocation = mlc.getBoolean(new String[]{"useeyelocation", "uel"}, false);
         this.startForwardOffset = mlc.getPlaceholderFloat(new String[]{"forwardoffset", "startfoffset", "sfo"}, 0.0F);
         this.startSideOffset = mlc.getPlaceholderFloat(new String[]{"sideoffset", "soffset", "sso"}, 0.0F);
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "RayTraceTo mechanic requires Mythic Premium");
         return SkillResult.REQUIRES_PREMIUM;
      } else {
         SkillCaster am = data.getCaster();
         AbstractLocation origin = data.getOrigin();
         float startYOffset = this.startYOffset.get(data);
         AbstractLocation targetLocation = target.clone().add(0.0, this.targetYOffset.get(data), 0.0);
         AbstractLocation startLocation;
         if (this.fromOrigin) {
            startLocation = origin.add(0.0, startYOffset, 0.0);
         } else if (this.useEyeLocation) {
            startLocation = am.getEntity().getEyeLocation().add(0.0, startYOffset, 0.0);
         } else {
            startLocation = am.getEntity().getLocation().add(0.0, startYOffset, 0.0);
         }

         float startForwardOffset = this.startForwardOffset.get(data);
         if (startForwardOffset != 0.0F) {
            startLocation = MythicUtil.move(this.faulty, startLocation, startForwardOffset, 0.0, 0.0);
         }

         float startSideOffset = this.startSideOffset.get(data);
         if (startSideOffset != 0.0F) {
            startLocation = MythicUtil.move(this.faulty, startLocation, 0.0, 0.0, startSideOffset);
         }

         AbstractVector aDirection = targetLocation.toVector().subtract(startLocation.toVector()).normalize();
         LivingEntity caster = (LivingEntity)data.getCaster().getEntity().getBukkitEntity();
         if (this.projectileVelocityHorizNoise > 0.0F) {
            float noise = (float)(this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * this.projectileVelocityHorizNoise);
            aDirection.rotate(noise);
         }

         if (this.projectileVelocityVertNoise > 0.0F) {
            float noise = (float)(this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * this.projectileVelocityVertNoise);
            aDirection.add(new AbstractVector(0.0F, noise, 0.0F)).normalize();
         }

         double maxDistance = this.maxDistance.get(data);
         Optional<AbstractModelEngineSupport> megSupport = this.getPlugin().getCompatibility().getModelEngine();
         RayTraceResult result = this.getPlugin()
            .getVolatileCodeHandler()
            .getWorldHandler()
            .rayTrace(
               BukkitAdapter.adapt(startLocation), BukkitAdapter.adapt(aDirection), maxDistance, this.fluidCollisionMode, this.rayWidth.get(data), entity -> {
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
               }, material -> this.ignorePassableBlocks ? !material.isSolid() : false
            );
         if (result != null) {
            if (this.entitySkill.isPresent() && result.getHitEntity() != null) {
               AbstractEntity aEntity = BukkitAdapter.adapt(result.getHitEntity());
               boolean headshot = false;
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "RaytraceTo hit {0}", aEntity.getUniqueId());
               if (result.getHitPosition().getY() - aEntity.getLocation().getY() >= result.getHitEntity().getBoundingBox().getHeight() - 0.4) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Mechanic was a headshot");
                  data.setPower(data.getPower() * this.headshotMultiplier.get(data));
                  headshot = true;
               }

               SkillMetadata entityData = data.deepClone().setEntityTarget(aEntity);
               if (this.headshotSkill.isPresent() && headshot) {
                  Skill headshotSkill = this.headshotSkill.get();
                  if (headshotSkill.isUsable(entityData)) {
                     headshotSkill.execute(entityData);
                  }
               }

               Skill skill = this.entitySkill.get();
               if (skill.isUsable(entityData)) {
                  skill.execute(entityData);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "RaytraceTo skill failed: not usable");
               }
            }

            if (this.locationSkill.isPresent()) {
               AbstractLocation aLocation = BukkitAdapter.adapt(result.getHitPosition().toLocation(caster.getWorld()));
               SkillMetadata locationData = data.deepClone().setLocationTarget(aLocation);
               Skill skill = this.locationSkill.get();
               if (skill.isUsable(locationData)) {
                  skill.execute(locationData);
               }
            }
         } else if (this.locationSkill.isPresent()) {
            AbstractLocation aLocation = BukkitAdapter.adapt(
               ((Player)BukkitAdapter.adapt(data.getCaster().getEntity()))
                  .getTargetBlock(this.getPlugin().getConfiguration().getTransparentBlocks(), (int)maxDistance)
                  .getLocation()
            );
            SkillMetadata locationData = data.deepClone().setLocationTarget(aLocation);
            Skill skill = this.locationSkill.get();
            if (skill.isUsable(locationData)) {
               skill.execute(locationData);
            }
         }

         return SkillResult.SUCCESS;
      }
   }
}
