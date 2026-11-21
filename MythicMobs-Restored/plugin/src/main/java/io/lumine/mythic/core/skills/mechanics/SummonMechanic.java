package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "summon",
   aliases = {"spawnMob", "spawnMobs", "pirateSummon"},
   description = "Summons mobs at the target location"
)
public class SummonMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected MythicMob mm;
   protected BukkitEntityType me;
   protected PlaceholderString strType;
   protected PlaceholderInt amount;
   protected PlaceholderDouble level;
   protected PlaceholderFloat yaw;
   protected PlaceholderFloat pitch;
   protected PlaceholderInt noise;
   protected PlaceholderInt yNoise;
   protected PlaceholderDouble force;
   protected PlaceholderDouble yForce;
   protected boolean summonerIsOwner;
   protected boolean summonerIsParent;
   protected boolean summonerIsFaction;
   protected boolean yUpOnly;
   protected boolean onSurface;
   protected boolean inheritFaction;
   protected boolean inheritThreatTable;
   protected boolean copyThreatTable;
   protected boolean inheritDespawnOption;
   protected boolean useTargetYaw;
   protected boolean useTargetPitch;
   @MythicField(
      name = "onSummon",
      aliases = {"then"},
      version = "4.14",
      description = "Skill to execute when the destination is reached"
   )
   protected Optional<Skill> onSummonSkill = Optional.empty();
   protected String onSummonSkillName;

   public SummonMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.level = mlc.getPlaceholderDouble(new String[]{"level", "l"}, -1.0);
      this.yaw = mlc.getPlaceholderFloat(new String[]{"yaw"}, null);
      this.pitch = mlc.getPlaceholderFloat(new String[]{"pitch"}, null);
      this.noise = mlc.getPlaceholderInteger(new String[]{"noise", "n", "radius", "r"}, 0);
      this.yNoise = mlc.getPlaceholderInteger(new String[]{"ynoise", "yn", "yradius", "yr"}, this.noise.toString());
      this.yUpOnly = mlc.getBoolean(new String[]{"yradiusuponly", "yradiusonlyup", "yruo", "yu"}, false);
      this.force = mlc.getPlaceholderDouble(new String[]{"force", "f", "velocity", "v"}, 0.0);
      this.yForce = mlc.getPlaceholderDouble(new String[]{"yforce", "yf", "yvelocity", "yv"}, this.force.get());
      this.onSurface = mlc.getBoolean(new String[]{"onsurface", "os", "s"}, false);
      this.copyThreatTable = mlc.getBoolean(new String[]{"copythreattable", "ctt"}, false);
      this.inheritFaction = mlc.getBoolean(new String[]{"inheritfaction", "if"}, true);
      this.inheritThreatTable = mlc.getBoolean(new String[]{"inheritthreattable", "itt"}, false);
      this.summonerIsOwner = mlc.getBoolean(new String[]{"summonerisowner", "sio"}, true);
      this.summonerIsParent = mlc.getBoolean(new String[]{"summonerisparent", "sip"}, true);
      this.summonerIsFaction = mlc.getBoolean(new String[]{"summonerisfaction", "sif"}, false);
      this.inheritDespawnOption = mlc.getBoolean(new String[]{"inheritdespawn", "inheritdespawnoption", "ido"}, false);
      this.useTargetYaw = mlc.getBoolean(new String[]{"usetargetyaw", "uty"}, false);
      this.useTargetPitch = mlc.getBoolean(new String[]{"usetargetpitch", "utp"}, false);
      this.strType = mlc.getPlaceholderString(new String[]{"type", "t", "mob", "m"}, "SKELETON");
      if (this.strType.isStatic()) {
         this.getPlugin().getSkillManager().queueSecondPass(() -> {
            this.mm = this.getPlugin().getMobManager().getMythicMob(this.strType.get()).orElse(null);
            if (this.mm == null) {
               this.me = BukkitEntityType.getMythicEntity(this.strType.get());
               if (this.me == null) {
                  MythicLogger.errorMechanicConfig(this, mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
               }
            }
         });
      } else {
         this.mm = null;
         this.me = null;
      }

      this.onSummonSkillName = mlc.getString(new String[]{"onsummonskill", "onsummon", "then"});
      this.getManager().queueSecondPass(() -> {
         if (this.onSummonSkillName != null) {
            this.onSummonSkill = this.getManager().getSkill(file, this, this.onSummonSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      int amount = this.amount.get(context);
      double level = this.level.get(context);
      level = level == -1.0 ? context.meta().getCaster().getLevel() : level;
      MythicMob mythicSpawnType = this.mm;
      BukkitEntityType bukkitEntityType = this.me;
      if (mythicSpawnType == null) {
         mythicSpawnType = this.getPlugin().getMobManager().getMythicMob(this.strType.get(context)).orElse(null);
         if (mythicSpawnType == null) {
            bukkitEntityType = BukkitEntityType.getMythicEntity(this.strType.get(context));
            if (bukkitEntityType == null) {
               MythicLogger.error(
                  "The 'type' attribute must be a valid MythicMob or MythicEntity type. Spawned from a placeholder summon mechanic, no MLC available. Mob String: "
                     + this.strType.get()
               );
            }
         }
      }

      if (mythicSpawnType != null) {
         int noise = this.noise.get(context);
         if (noise > 0) {
            for (int i = 1; i <= amount; i++) {
               int height = mythicSpawnType.getMythicEntity() == null ? 2 : mythicSpawnType.getMythicEntity().getHeight();
               AbstractLocation l = MobExecutor.findSafeSpawnLocation(context.location(), noise, this.yNoise.get(context), height, this.yUpOnly, this.onSurface);
               float yaw;
               if (this.yaw != null) {
                  yaw = this.yaw.get(context);
               } else if (this.useTargetYaw) {
                  yaw = context.location().getYaw();
               } else {
                  yaw = context.meta().getCaster().getLocation().getYaw();
               }

               context.location().setYaw(yaw);
               float pitch;
               if (this.pitch != null) {
                  pitch = this.pitch.get(context);
               } else if (this.useTargetPitch) {
                  pitch = context.location().getPitch();
               } else {
                  pitch = context.meta().getCaster().getLocation().getPitch();
               }

               context.location().setPitch(pitch);
               ActiveMob ams = mythicSpawnType.spawn(
                  l,
                  level,
                  SpawnReason.SUMMON,
                  null,
                  null,
                  this.getExecutionAudience(context.skillMetadata()),
                  this.summonerIsParent ? context.meta().getCaster() : null
               );
               if (ams != null) {
                  double force = this.force.get(context);
                  double yForce = this.yForce.get(context);
                  ams.getEntity().setRotation(yaw, pitch);
                  ams.getEntity()
                     .setVelocity(
                        new AbstractVector(
                           (Numbers.randomDouble() - 0.5) * force, (Numbers.randomDouble() - 0.5) * yForce, (Numbers.randomDouble() - 0.5) * force
                        )
                     );
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, l.toString());
                  if (context.meta().getCaster() instanceof ActiveMob am) {
                     if (this.summonerIsOwner) {
                        ams.setOwnerUUID(context.meta().getCaster().getEntity().getUniqueId());
                     }

                     if (this.inheritFaction) {
                        ams.setFaction(am.getFaction());
                     }

                     if (this.inheritDespawnOption) {
                        ams.setDespawnMode(am.getDespawnMode());
                     }

                     if (am.hasThreatTable()) {
                        if (this.copyThreatTable) {
                           try {
                              ams.importThreatTable(am.getThreatTable().clone());
                              ams.getThreatTable().targetHighestThreat();
                           } catch (CloneNotSupportedException e) {
                              e.printStackTrace();
                           }
                        } else if (this.inheritThreatTable) {
                           ams.importThreatTable(am.getThreatTable());
                           ams.getThreatTable().targetHighestThreat();
                        }
                     }
                  } else if (this.summonerIsOwner) {
                     ams.setOwnerUUID(context.meta().getCaster().getEntity().getUniqueId());
                  }

                  if (this.onSummonSkill.isPresent()) {
                     SkillMetadata summonData = context.skillMetadata().deepClone();
                     if (summonData.getEntityTargets() != null) {
                        summonData.getEntityTargets().clear();
                     }

                     summonData.setEntityTarget(ams.getEntity());
                     this.onSummonSkill.get().execute(summonData);
                  }
               }
            }
         } else {
            for (int i = 1; i <= amount; i++) {
               float yawx;
               if (this.yaw != null) {
                  yawx = this.yaw.get(context);
               } else if (this.useTargetYaw) {
                  yawx = context.location().getYaw();
               } else {
                  yawx = context.meta().getCaster().getLocation().getYaw();
               }

               context.location().setYaw(yawx);
               float pitchx;
               if (this.pitch != null) {
                  pitchx = this.pitch.get(context);
               } else if (this.useTargetPitch) {
                  pitchx = context.location().getPitch();
               } else {
                  pitchx = context.meta().getCaster().getLocation().getPitch();
               }

               context.location().setPitch(pitchx);
               ActiveMob ams = mythicSpawnType.spawn(
                  context.location(),
                  level,
                  SpawnReason.SUMMON,
                  null,
                  null,
                  this.getExecutionAudience(context.skillMetadata()),
                  this.summonerIsParent ? context.meta().getCaster() : null
               );
               if (ams != null) {
                  double forcex = this.force.get(context);
                  double yForcex = this.yForce.get(context);
                  ams.getEntity().setRotation(yawx, pitchx);
                  ams.getEntity()
                     .setVelocity(
                        new AbstractVector(
                           (Numbers.randomDouble() - 0.5) * forcex, (Numbers.randomDouble() - 0.5) * yForcex, (Numbers.randomDouble() - 0.5) * forcex
                        )
                     );
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, context.location().toString());
                  if (context.meta().getCaster() instanceof ActiveMob am) {
                     if (this.summonerIsOwner) {
                        ams.setOwnerUUID(context.meta().getCaster().getEntity().getUniqueId());
                     }

                     if (this.inheritFaction) {
                        ams.setFaction(am.getFaction());
                     }

                     if (this.inheritDespawnOption) {
                        ams.setDespawnMode(am.getDespawnMode());
                     }

                     if (this.copyThreatTable) {
                        try {
                           ams.importThreatTable(am.getThreatTable().clone());
                           ams.getThreatTable().targetHighestThreat();
                        } catch (CloneNotSupportedException ex) {
                           ex.printStackTrace();
                        }
                     } else if (this.inheritThreatTable) {
                        ams.importThreatTable(am.getThreatTable());
                        ams.getThreatTable().targetHighestThreat();
                     }
                  } else if (this.summonerIsOwner) {
                     ams.setOwnerUUID(context.meta().getCaster().getEntity().getUniqueId());
                  }

                  if (this.onSummonSkill.isPresent()) {
                     SkillMetadata summonData = context.skillMetadata().deepClone();
                     if (summonData.getEntityTargets() != null) {
                        summonData.getEntityTargets().clear();
                     }

                     summonData.setEntityTarget(ams.getEntity());
                     this.onSummonSkill.get().execute(summonData);
                  }
               }
            }
         }

         return SkillResult.SUCCESS;
      } else if (bukkitEntityType == null) {
         MythicLogger.error("SummonMechanic: Mob Type {0} not found", this.strType);
         return SkillResult.SUCCESS;
      } else {
         int noise = this.noise.get(context);
         if (noise > 0) {
            for (int i = 1; i <= amount; i++) {
               AbstractLocation lx = MobExecutor.findSafeSpawnLocation(
                  context.location(), noise, this.yNoise.get(context), bukkitEntityType.getHeight(), this.yUpOnly, this.onSurface
               );
               Entity entity = bukkitEntityType.spawn(BukkitAdapter.adapt(lx), SpawnReason.SUMMON, this.prespawnAudienceHide(context.skillMetadata()));
               this.applyAudienceVisibility(context.skillMetadata(), BukkitAdapter.adapt(entity));
               double forcexx = this.force.get(context);
               double yForcexx = this.yForce.get(context);
               entity.setVelocity(
                  new Vector((Numbers.randomDouble() - 0.5) * forcexx, (Numbers.randomDouble() - 0.5) * yForcexx, (Numbers.randomDouble() - 0.5) * forcexx)
               );
               if (this.onSummonSkill.isPresent()) {
                  SkillMetadata summonData = context.skillMetadata().deepClone();
                  if (summonData.getEntityTargets() != null) {
                     summonData.getEntityTargets().clear();
                  }

                  summonData.setEntityTarget(BukkitAdapter.adapt(entity));
                  this.onSummonSkill.get().execute(summonData);
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1} with noise", this.strType, lx.toString());
            }
         } else {
            for (int i = 1; i <= amount; i++) {
               Entity entity = bukkitEntityType.spawn(
                  BukkitAdapter.adapt(context.location()), SpawnReason.SUMMON, this.prespawnAudienceHide(context.skillMetadata())
               );
               this.applyAudienceVisibility(context.skillMetadata(), BukkitAdapter.adapt(entity));
               double forcexx = this.force.get(context);
               double yForcexx = this.yForce.get(context);
               entity.setVelocity(
                  new Vector((Numbers.randomDouble() - 0.5) * forcexx, (Numbers.randomDouble() - 0.5) * yForcexx, (Numbers.randomDouble() - 0.5) * forcexx)
               );
               if (this.onSummonSkill.isPresent()) {
                  SkillMetadata summonData = context.skillMetadata().deepClone();
                  if (summonData.getEntityTargets() != null) {
                     summonData.getEntityTargets().clear();
                  }

                  summonData.setEntityTarget(BukkitAdapter.adapt(entity));
                  this.onSummonSkill.get().execute(summonData);
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, context.location().toString());
            }
         }

         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.cast(PlaceholderContext.builder().meta(data).entity(target).build());
      return SkillResult.SUCCESS;
   }

   private void spawnMythicMob() {
   }

   private Consumer<Entity> prespawnAudienceHide(SkillMetadata data) {
      return data.hasExecutionAudience() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_20) ? entity -> entity.setVisibleByDefault(false) : null;
   }

   private Set<UUID> getExecutionAudience(SkillMetadata data) {
      return !data.hasExecutionAudience() ? null : data.getExecutionAudience().get();
   }

   private void applyAudienceVisibility(SkillMetadata data, AbstractEntity spawnedEntity) {
      if (data.hasExecutionAudience()) {
         Set<UUID> audience = data.getExecutionAudience().get();
         spawnedEntity.getDataContainer().set(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY, audience.toArray(new UUID[0]));
         this.getPlugin().getMobManager().registerAudienceTrackedEntity(spawnedEntity.getUniqueId());
         this.getPlugin().getMobManager().applyAudienceVisibility(spawnedEntity, audience);
      }
   }
}
