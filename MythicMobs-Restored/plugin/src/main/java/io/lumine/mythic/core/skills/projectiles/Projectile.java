package io.lumine.mythic.core.skills.projectiles;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.events.Subscription;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.projectiles.bullet.MEGBullet;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicField;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class Projectile extends SkillMechanic {
   public static final Set<AbstractEntity> BULLET_ENTITIES = new ObjectOpenHashSet();
   private static final Set<Projectile.ProjectileTracker> ACTIVE_TRACKERS = ConcurrentHashMap.newKeySet();
   private static volatile Subscription worldUnloadSubscription;
   @MythicField(
      name = "onTickSkill",
      aliases = {"ontick", "ot"},
      description = "The meta-skill to execute every time the projectile is ticked"
   )
   protected Optional<Skill> onTickSkill = Optional.empty();
   @MythicField(
      name = "onHitSkill",
      aliases = {"onhit", "oh"},
      description = "The meta-skill to execute when the projectile hits and entity"
   )
   protected Optional<Skill> onHitSkill = Optional.empty();
   @MythicField(
      name = "onBounceSkill",
      aliases = {"onbounce", "ot"},
      description = "The meta-skill to execute when the projectile bounces",
      premium = true
   )
   protected Optional<Skill> onBounceSkill = Optional.empty();
   @MythicField(
      name = "onEndSkill",
      aliases = {"onend", "oe"},
      description = "The meta-skill to execute when the projectile ends"
   )
   protected Optional<Skill> onEndSkill = Optional.empty();
   @MythicField(
      name = "onStartSkill",
      aliases = {"onstart", "os"},
      description = "The meta-skill to execute when the projectile starts"
   )
   protected Optional<Skill> onStartSkill = Optional.empty();
   @MythicField(
      name = "onHitBlockSkill",
      aliases = {"onhitblockskill", "onhitblock", "ohb"},
      description = "The meta-skill to execute when the projectile hits a block"
   )
   protected Optional<Skill> onHitBlockSkill = Optional.empty();
   @MythicField(
      name = "onInteractSkill",
      aliases = {"onInteract"},
      description = "The meta-skill to execute when the projectile is interacted with"
   )
   protected Optional<Skill> onInteractSkill = Optional.empty();
   protected String onTickSkillName;
   protected String onHitSkillName;
   protected String onBounceSkillName;
   protected String onEndSkillName;
   protected String onStartSkillName;
   protected String onHitBlockName;
   protected String onInteractName;
   protected Projectile.ProjectileType type;
   @MythicField(
      name = "fromOrigin",
      aliases = {"fo"},
      description = "Whether to start the projectile at the origin or from the caster",
      defValue = "true"
   )
   protected boolean fromOrigin;
   @MythicField(
      name = "doEndSkillOnHit",
      aliases = {"esoh"},
      description = "If the skill hits an entity, should it still use the End Skill?",
      defValue = "true"
   )
   protected boolean doEndSkillOnHit;
   @MythicField(
      name = "requireLineOfSight",
      aliases = {"rlos", "los"},
      description = "Whether the starting point must have line-of-sight to the origin",
      defValue = "PLAYERS_ONLY"
   )
   protected Projectile.ProjectileTriOption requireLineOfSight;
   @MythicField(
      name = "bulletType",
      aliases = {"bullet", "b"},
      description = "    The bullet type of the projectile.\n    Bullet Types:\n    - ARROW\n    - BLOCK\n    - ITEM\n    - MYTHICITEM\n    - MOB\n    - TRACKING\n",
      defValue = "NONE"
   )
   protected Optional<ProjectileBulletType> bulletType;
   protected ProjectileBullet bullet;
   @MythicField(
      name = "maxDuration",
      aliases = {"duration", "md", "d"},
      description = "The max duration of the projectile will persist. Bare numbers are ticks.",
      defValue = "400"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "interval",
      aliases = {"int", "i"},
      description = "How often the projectile is updated. Bare numbers are ticks.",
      defValue = "1"
   )
   protected PlaceholderDuration tickInterval;
   @Getter
   @MythicField(
      name = "interpolation",
      aliases = {"tickinterpolation", "ti"},
      description = "Interpolates additional points between each tick of the projectile, running onTick multiple times",
      defValue = "0"
   )
   protected int tickInterpolation;
   @MythicField(
      name = "horizontalRadius",
      aliases = {"hradius", "hr", "r"},
      description = "The horizontal hit radius of the projectile",
      defValue = "1.25"
   )
   protected PlaceholderFloat hitRadius;
   @MythicField(
      name = "verticalRadius",
      aliases = {"vradius", "vr"},
      description = "The vertical hit radius of the projectile",
      defValue = "1.25"
   )
   protected PlaceholderFloat verticalHitRadius;
   @MythicField(
      name = "maxRange",
      aliases = {"mr"},
      description = "The max range in blocks the projectile will travel",
      defValue = "40"
   )
   protected PlaceholderFloat range;
   @MythicField(
      name = "deathDelay",
      aliases = {"death", "dd"},
      description = "Delays the removal of project bullets when the projectile is terminated. Bare numbers are ticks.",
      defValue = "2"
   )
   protected PlaceholderDuration deathDelay;
   @MythicField(
      name = "startYOffset",
      aliases = {"syo"},
      description = "The start y-offset",
      defValue = "1"
   )
   protected PlaceholderDouble startYOffset;
   @MythicField(
      name = "startForwardOffset",
      aliases = {"forwardoffset", "sfo"},
      description = "The start forward offset",
      defValue = "1"
   )
   protected PlaceholderFloat startForwardOffset;
   @MythicField(
      name = "startSideOffset",
      aliases = {"ssoffset", "sso"},
      description = "How far to the side of the caster the projectile is",
      defValue = "0"
   )
   protected PlaceholderDouble startSideOffset;
   @MythicField(
      name = "endSideOffset",
      aliases = {"endoffset", "esoffset", "eso"},
      description = "How far to the side of the target location the projectile will end up",
      defValue = "0"
   )
   protected PlaceholderDouble endSideOffset;
   @Getter
   @MythicField(
      name = "targetYOffset",
      aliases = {"targety", "tyo"},
      description = "The y-offset of the target location",
      defValue = "0"
   )
   protected PlaceholderDouble targetYOffset;
   protected Optional<SkillTargeter> projectileStartDirection;
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The velocity of the projectile",
      defValue = "5"
   )
   protected PlaceholderFloat projectileVelocity;
   @MythicField(
      name = "verticalOffset",
      aliases = {"vo"},
      description = "An angular vertical offset of the projectile, using slope by default.",
      defValue = "0"
   )
   protected PlaceholderAngle projectileVelocityVertOffset;
   @MythicField(
      name = "horizontalOffset",
      aliases = {"ho"},
      description = "An angular horizontal offset of the projectile, using degrees by default.",
      defValue = "0"
   )
   protected PlaceholderAngle projectileVelocityHorizOffset;
   @MythicField(
      name = "accuracy",
      aliases = {"ac", "a"},
      description = "The accuracy of the projectile",
      defValue = "1"
   )
   protected float projectileVelocityAccuracy;
   @MythicField(
      name = "verticalNoise",
      aliases = {"vn"},
      description = "The vertical noise/randomness of the projectile velocity"
   )
   protected float projectileVelocityVertNoise;
   @MythicField(
      name = "horizontalNoise",
      aliases = {"hn"},
      description = "The horizontal noise/randomness of the projectile velocity"
   )
   protected float projectileVelocityHorizNoise;
   protected float projectileVelocityVertNoiseBase;
   protected float projectileVelocityHorizNoiseBase;
   @MythicField(
      name = "stopAtEntity",
      aliases = {"se"},
      description = "Terminates the projectile when hitting an entity",
      defValue = "true"
   )
   protected boolean stopOnHitEntity;
   @MythicField(
      name = "stopAtBlock",
      aliases = {"sb"},
      description = "Terminates the projectile when hitting a block",
      defValue = "true"
   )
   protected boolean stopOnHitGround;
   @MythicField(
      name = "powerAffectsVelocity",
      aliases = {"pav"},
      description = "Whether the skill's power affects velocity",
      defValue = "true"
   )
   protected boolean powerAffectsVelocity;
   @MythicField(
      name = "powerAffectsRange",
      aliases = {"par"},
      description = "Whether the skill's power affects the maximum range",
      defValue = "true"
   )
   protected boolean powerAffectsRange;
   @MythicField(
      name = "interactable",
      aliases = {},
      description = "Is the projectile hittable? (1.19.4+ only)",
      defValue = "false"
   )
   protected boolean interactable;
   @MythicField(
      name = "hitSelf",
      description = "Can hit the caster",
      defValue = "false"
   )
   protected boolean hitSelf = false;
   @MythicField(
      name = "hitTarget",
      aliases = {"ht"},
      description = "Can hit the target",
      defValue = "true"
   )
   protected boolean hitTarget = true;
   @MythicField(
      name = "hitPlayers",
      aliases = {"hp"},
      description = "Can hit players",
      defValue = "true"
   )
   protected boolean hitPlayers = true;
   @MythicField(
      name = "hitNonPlayers",
      aliases = {"hnp"},
      description = "Can hit non player entities",
      defValue = "false"
   )
   protected boolean hitNonPlayers = false;
   @MythicField(
      name = "hitTargetOnly",
      aliases = {"hto"},
      description = "Can hit the target only",
      defValue = "false"
   )
   protected boolean hitTargetOnly = false;
   protected boolean hitArmorStands = false;
   @MythicField(
      name = "immuneDelay",
      aliases = {"immune", "id"},
      description = "Sets the immunity delay (when the target can be hit by the projectile again). Bare numbers are milliseconds.",
      defValue = "2000"
   )
   protected PlaceholderDuration immuneDelay;
   @MythicField(
      name = "shareSubHitboxCooldown",
      aliases = {"shcd"},
      description = "Draw the hitbox of the projectile, useful for debugging",
      defValue = "false"
   )
   protected boolean shareSubHitboxCooldown;
   protected String hitTargeterString;
   @MythicField(
      name = "hitTargeter",
      aliases = {"htr"},
      description = "Custom targeter for more robust hit detection"
   )
   protected Optional<SkillTargeter> hitTargeter = Optional.empty();
   @MythicField(
      name = "drawHitbox",
      description = "Draw the hitbox of the projectile, useful for debugging",
      defValue = "false"
   )
   protected boolean drawHitbox = false;
   protected String stopConditionString;
   @MythicField(
      name = "stopConditions",
      aliases = {"stpcond"},
      defValue = "NONE",
      version = "4.9",
      premium = true,
      description = "Conditions applied to the stop the projectile when hitting the target"
   )
   protected List<SkillCondition> stopConditions = null;
   protected String hitBlockConditionString;
   @MythicField(
      name = "hitBlockConditions",
      aliases = {"hbcond"},
      defValue = "NONE",
      version = "5.13",
      premium = true,
      description = "Conditions applied to the hit block"
   )
   protected List<SkillCondition> hitBlockConditions = null;
   protected String hitConditionString;
   @MythicField(
      name = "hitConditions",
      aliases = {"conditions", "cond", "c"},
      defValue = "NONE",
      version = "4.9",
      premium = true,
      description = "Conditions applied to the hit target"
   )
   protected List<SkillCondition> hitConditions = null;

   private static void ensureWorldUnloadListener() {
      if (worldUnloadSubscription == null) {
         synchronized (ACTIVE_TRACKERS) {
            if (worldUnloadSubscription == null) {
               worldUnloadSubscription = Events.subscribe(WorldUnloadEvent.class).handler(event -> {
                  UUID unloaded = event.getWorld().getUID();

                  for (Projectile.ProjectileTracker tracker : ACTIVE_TRACKERS) {
                     AbstractLocation start = tracker.getStartLocation();
                     if (start == null || unloaded.equals(start.getWorld().getUniqueId())) {
                        tracker.suppressEndSkill = true;
                        tracker.terminate();
                     }
                  }
               });
            }
         }
      }
   }

   public Projectile(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot", "skill", "s", "meta", "m"});
      this.onHitSkillName = mlc.getString(new String[]{"onhitskill", "onhit", "oh"});
      this.onBounceSkillName = mlc.getString(new String[]{"onbounceskill", "onbounce"});
      this.onEndSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"});
      this.onStartSkillName = mlc.getString(new String[]{"onstartskill", "onstart", "os"});
      this.onHitBlockName = mlc.getString(new String[]{"onhitblockskill", "onhitblock", "ohb"});
      this.onInteractName = mlc.getString(new String[]{"oninteractskill", "oninteract"});
      String bulletType = mlc.getString(new String[]{"bullettype", "bullet", "b"}, "NONE");

      try {
         this.bulletType = ProjectileBulletType.get(bulletType);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid bullet type specified");
         this.bulletType = Optional.empty();
      }

      if (this.bulletType.isPresent()) {
         this.bullet = this.bulletType.get().create(this, mlc);
      }

      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"interval", "int", "i"}, DurationUnit.TICKS, "1");
      this.tickInterpolation = mlc.getInteger(new String[]{"tickinterpolation", "interpolation", "ti"}, 0);
      this.hitRadius = mlc.getPlaceholderFloat(new String[]{"horizontalradius", "hradius", "hr", "r"}, 1.25F);
      this.duration = mlc.getPlaceholderDuration(new String[]{"maxduration", "duration", "md", "d"}, DurationUnit.TICKS, "400");
      this.range = mlc.getPlaceholderFloat(new String[]{"maxrange", "mr"}, 40.0F);
      this.deathDelay = mlc.getPlaceholderDuration(new String[]{"deathdelay", "death", "dd"}, DurationUnit.TICKS, "2");
      this.verticalHitRadius = mlc.getPlaceholderFloat(new String[]{"verticalradius", "vradius", "vr"}, 1.25F);
      this.startYOffset = mlc.getPlaceholderDouble(new String[]{"startyoffset", "syo"}, 1.0);
      this.startForwardOffset = mlc.getPlaceholderFloat(new String[]{"forwardoffset", "startfoffset", "sfo"}, 1.0F);
      this.targetYOffset = mlc.getPlaceholderDouble(new String[]{"targetyoffset", "targety", "tyo"}, 0.0);
      String projectileStartDirection = mlc.getString(new String[]{"startingdirection", "startingdir", "startdir", "sdir"}, null);
      if (projectileStartDirection != null) {
         this.projectileStartDirection = Optional.of(this.parseSkillTargeter(projectileStartDirection));
      } else {
         this.projectileStartDirection = Optional.empty();
      }

      float sideOffset = mlc.getFloat(new String[]{"sideoffset", "soffset", "so"}, 0.0F);
      this.startSideOffset = mlc.getPlaceholderDouble(new String[]{"startsideoffset", "ssoffset", "sso"}, sideOffset);
      this.endSideOffset = mlc.getPlaceholderDouble(new String[]{"endsideoffset", "endoffset", "esoffset", "eso"}, sideOffset);
      this.projectileVelocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 5.0F);
      this.projectileVelocityVertOffset = mlc.getPlaceholderAngle(new String[]{"verticaloffset", "vo"}, AngleUnit.SLOPE, null);
      this.projectileVelocityHorizOffset = mlc.getPlaceholderAngle(new String[]{"horizontaloffset", "ho"}, AngleUnit.DEGREES, null);
      this.projectileVelocityAccuracy = mlc.getFloat(new String[]{"accuracy", "ac", "a"}, 1.0F);
      float defNoise = (1.0F - this.projectileVelocityAccuracy) * 45.0F;
      this.projectileVelocityVertNoise = mlc.getFloat(new String[]{"verticalnoise", "vn"}, defNoise) / 10.0F;
      this.projectileVelocityHorizNoise = mlc.getFloat(new String[]{"horizontalnoise", "hn"}, defNoise);
      this.projectileVelocityVertNoiseBase = 0.0F - this.projectileVelocityVertNoise / 2.0F;
      this.projectileVelocityHorizNoiseBase = 0.0F - this.projectileVelocityHorizNoise / 2.0F;
      this.stopOnHitEntity = mlc.getBoolean(new String[]{"stopatentity", "se"}, true);
      this.stopOnHitGround = mlc.getBoolean(new String[]{"stopatblock", "sb"}, true);
      this.powerAffectsVelocity = mlc.getBoolean(new String[]{"poweraffectsvelocity", "pav"}, true);
      this.powerAffectsRange = mlc.getBoolean(new String[]{"poweraffectsrange", "par"}, true);
      this.doEndSkillOnHit = mlc.getBoolean(new String[]{"doEndSkillOnHit", "esoh"}, true);
      this.hitSelf = mlc.getBoolean(new String[]{"hitself"}, false);
      this.hitPlayers = mlc.getBoolean(new String[]{"hitplayers", "hp"}, true);
      this.hitNonPlayers = mlc.getBoolean(new String[]{"hitnonplayers", "hnp"}, false);
      this.hitTarget = mlc.getBoolean(new String[]{"hittarget", "ht"}, true);
      this.hitTargetOnly = mlc.getBoolean(new String[]{"hittargetonly", "hto"}, false);
      this.interactable = mlc.getBoolean(new String[]{"interactable"}, false);
      this.immuneDelay = mlc.getPlaceholderDuration(new String[]{"immunedelay", "immune", "iD"}, DurationUnit.MILLISECONDS, "2000");
      this.hitTargeterString = mlc.getString(new String[]{"hitTargeter", "htr"}, null);
      this.shareSubHitboxCooldown = mlc.getBoolean(new String[]{"shareSubHitboxCooldown", "shcd"}, true);
      this.stopConditionString = mlc.getString(new String[]{"stopconditions", "stpcond"}, "null");
      this.hitConditionString = mlc.getString(new String[]{"hitconditions", "conditions", "cond", "c"}, "null");
      this.hitBlockConditionString = mlc.getString(new String[]{"hitblockconditions", "hbcond"}, "null");
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.drawHitbox = mlc.getBoolean(new String[]{"drawhitbox"}, false);
      String requireLineOfSight = mlc.getString(new String[]{"requirelineofsight", "requirelos", "rlos", "los"}, "PLAYERS_ONLY");

      try {
         this.requireLineOfSight = Projectile.ProjectileTriOption.valueOf(requireLineOfSight.toUpperCase());
      } catch (Throwable ex) {
         MythicLogger.errorMechanic(this, "Invalid input for requireLineOfSight option '" + requireLineOfSight + "'");
         this.requireLineOfSight = Projectile.ProjectileTriOption.PLAYERS_ONLY;
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getManager().getSkill(file, this, this.onTickSkillName);
         }

         if (this.onHitSkillName != null) {
            this.onHitSkill = this.getManager().getSkill(file, this, this.onHitSkillName);
         }

         if (this.onBounceSkillName != null) {
            this.onBounceSkill = this.getManager().getSkill(file, this, this.onBounceSkillName);
         }

         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getManager().getSkill(file, this, this.onEndSkillName);
         }

         if (this.onStartSkillName != null) {
            this.onStartSkill = this.getManager().getSkill(file, this, this.onStartSkillName);
         }

         if (this.onHitBlockName != null) {
            this.onHitBlockSkill = this.getManager().getSkill(file, this, this.onHitBlockName);
         }

         if (this.onInteractName != null) {
            this.onInteractSkill = this.getManager().getSkill(file, this, this.onInteractName);
         }

         if (MythicBukkit.isVolatile()) {
            if (this.stopConditionString != null) {
               this.stopConditions = this.getManager().getConditions(this.stopConditionString);
            }

            if (this.hitConditionString != null) {
               this.hitConditions = this.getManager().getConditions(this.hitConditionString);
            }

            if (this.hitTargeterString != null) {
               this.hitTargeter = Optional.of(this.parseSkillTargeter(this.hitTargeterString));
            }

            if (this.hitBlockConditionString != null) {
               this.hitBlockConditions = this.getManager().getConditions(this.hitBlockConditionString);
            }
         }
      });
   }

   protected Optional<AbstractModelEngineSupport> meg() {
      return this.getPlugin().getCompatibility().getModelEngine();
   }

   protected boolean canStopAtLocation(SkillMetadata data, AbstractLocation target) {
      if (!this.stopOnHitGround) {
         return false;
      } else {
         if (this.hitBlockConditions != null) {
            for (SkillCondition condition : this.hitBlockConditions) {
               if (!condition.evaluateLocation(data, target)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   protected boolean canStopAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!this.stopOnHitEntity) {
         return false;
      } else {
         if (this.stopConditions != null) {
            for (SkillCondition condition : this.stopConditions) {
               if (!condition.evaluateToEntity(data, target)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   static String consumeUniformHitBone(Map<UUID, String> hitBones, Collection<UUID> targetIds) {
      String boneId = null;
      boolean uniform = true;

      for (UUID id : targetIds) {
         String bone = hitBones.remove(id);
         if (bone != null && (boneId == null || boneId.equals(bone))) {
            boneId = bone;
         } else {
            uniform = false;
         }
      }

      return uniform ? boneId : null;
   }

   public abstract class ProjectileTracker implements IParentSkill, ProjectileBulletableTracker, Runnable, Terminable, TerminableConsumer {
      protected final TerminableRegistry components = TerminableRegistry.create();
      private Task task;
      @Getter
      @Deprecated
      protected SkillMetadata data;
      @Getter
      protected PlaceholderContext context;
      @Getter
      protected double distanceTraveled = 0.0;
      @Getter
      protected int chargesRemaining;
      @Getter
      protected int ticksRemaining;
      @Getter
      protected int deathDelay;
      @Getter
      protected int tickInterval;
      @Getter
      protected float ticksPerSecond;
      @Getter
      protected long immuneDelay;
      @Getter @Setter
      protected boolean hasHitEntity = false;
      @Getter @Setter
      protected boolean suppressEndSkill = false;
      @Getter
      protected ProjectileBullet.BulletTracker bullet = null;
      protected float power;
      @Getter
      protected AbstractLocation startLocation;
      @Getter
      protected AbstractLocation previousLocation;
      @Getter
      protected AbstractLocation currentLocation;
      @Getter @Setter
      protected AbstractVector currentVelocity;
      @Getter
      protected double velocityMagnitude;
      @Getter
      protected BoundingBox hitbox;
      @Getter
      protected AbstractEntity interactionEntity;
      @Getter
      protected float maxDistanceSquared;
      private boolean reachedMaxRange;
      protected float resolvedHitRadius;
      protected ObjectArrayList<AbstractEntity> inRange = new ObjectArrayList();
      protected ObjectOpenHashSet<AbstractEntity> targets = new ObjectOpenHashSet();
      protected Object2LongOpenHashMap<UUID> immune = new Object2LongOpenHashMap();
      private final Map<UUID, String> hitSubHitboxes = new ConcurrentHashMap<>();
      private final Map<UUID, Long> immuneForeign = new ConcurrentHashMap<>();

      @Deprecated
      public ProjectileTracker(SkillMetadata data, AbstractLocation target) {
         this(PlaceholderContext.builder().meta(data).location(target).build());
      }

      public ProjectileTracker(PlaceholderContext context) {
         this.context = context.toBuilder().meta(((SkillMetadata)context.meta()).deepClone().setIsAsync(true)).build();
         this.data = this.context.skillMetadata();
         this.power = this.data.getPower();
         this.tickInterval = Math.max(1, Projectile.this.tickInterval.get(context).getTicks());
         this.ticksPerSecond = 20.0F / this.tickInterval;
         this.ticksRemaining = Projectile.this.duration.get(context).getTicks();
         this.deathDelay = Projectile.this.deathDelay.get(context).getTicks();
         this.immuneDelay = Projectile.this.immuneDelay.get(context).getMilliseconds();
         this.velocityMagnitude = Projectile.this.projectileVelocity.get(context) / this.ticksPerSecond;
         float parsedRange = Projectile.this.range.get(context);
         this.maxDistanceSquared = parsedRange * parsedRange;
         if (Projectile.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (Projectile.this.tickInterpolation + 1);
         }

         if (ServerVersion.isFolia() && this.data.getCaster().getEntity() != null) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.MECHANIC,
               "++ Projectile fired by Entity {0}: skill = {1}",
               this.data.getCaster().getEntity().getUniqueId(),
               Projectile.this.line
            );
         } else {
            MythicLogger.debug(
               MythicLogger.DebugLevel.MECHANIC,
               "++ Projectile fired by Entity {0}: skill = {1}",
               this.data.getCaster().getEntity().getName(),
               Projectile.this.line
            );
         }
      }

      private void evaluatePotentialTargets() {
         if (Projectile.this.hitSelf || Projectile.this.hitPlayers || Projectile.this.hitNonPlayers) {
            ObjectArrayList<AbstractEntity> inRange = this.inRange;
            inRange.clear();
            AbstractEntity casterEntity = this.data.getCaster().getEntity();
            UUID casterUUID = casterEntity.getUniqueId();
            Optional<AbstractModelEngineSupport> megSupport = Projectile.this.meg();
            boolean hasMEGSupport = megSupport.isPresent();
            boolean hasHitConditions = Projectile.this.hitConditions != null;
            Collection<AbstractEntity> entitiesToProcess = null;
            if (Projectile.this.hitTargeter.isPresent()) {
               SkillTargeter targeter = Projectile.this.hitTargeter.get();
               AbstractLocation origin = this.currentLocation.clone();
               double dx = origin.getX() - this.previousLocation.getX();
               double dz = origin.getZ() - this.previousLocation.getZ();
               double dy = origin.getY() - this.previousLocation.getY();
               double hLen = Math.sqrt(dx * dx + dz * dz);
               origin.setYaw((float)Math.toDegrees(Math.atan2(-dx, dz)));
               origin.setPitch((float)Math.toDegrees(Math.atan2(dy, hLen)));
               SkillMetadata sData = this.data.deeperClone();
               sData.setOrigin(origin);
               sData = Projectile.this.evaluateTargets(sData, targeter);
               if (sData.getEntityTargets() != null && !sData.getEntityTargets().isEmpty()) {
                  entitiesToProcess = sData.getEntityTargets();
               }
            } else {
               float hitRadiusValue = this.resolvedHitRadius;
               if (this.hitbox != null) {
                  entitiesToProcess = Projectile.this.getPlugin()
                     .getVolatileCodeHandler()
                     .getWorldHandler()
                     .getEntitiesNearLocation(this.currentLocation, hitRadiusValue, this.hitbox.getMinY(), this.hitbox.getMaxY(), null);
               } else {
                  entitiesToProcess = Projectile.this.getPlugin()
                     .getVolatileCodeHandler()
                     .getWorldHandler()
                     .getEntitiesNearLocation(this.currentLocation, hitRadiusValue);
               }
            }

            if (entitiesToProcess != null && !entitiesToProcess.isEmpty()) {
               inRange.ensureCapacity(entitiesToProcess.size());
               boolean hasBulletEntities = !Projectile.BULLET_ENTITIES.isEmpty();
               Iterator iterator = entitiesToProcess.iterator();

               while (true) {
                  AbstractEntity entity;
                  while (true) {
                     if (!iterator.hasNext()) {
                        return;
                     }

                     entity = (AbstractEntity)iterator.next();
                     if (entity.isLiving() && !entity.isCitizensNPC() && (!hasBulletEntities || !Projectile.BULLET_ENTITIES.contains(entity))) {
                        boolean targetIsSelf = entity.getUniqueId().equals(casterUUID);
                        if (hasMEGSupport) {
                           AbstractEntity parent = megSupport.get().getParent(entity);
                           if (parent != null && parent.getUniqueId().equals(casterUUID)) {
                              targetIsSelf = true;
                           }
                        }

                        if ((Projectile.this.hitSelf || !targetIsSelf)
                           && (
                              Projectile.this.hitSelf && targetIsSelf
                                 || (Projectile.this.hitPlayers || !entity.isPlayer()) && (Projectile.this.hitNonPlayers || entity.isPlayer())
                           )) {
                           if (!hasHitConditions) {
                              break;
                           }

                           boolean passesConditions = true;

                           for (SkillCondition condition : Projectile.this.hitConditions) {
                              if (!condition.evaluateToEntity(this.data, entity)) {
                                 passesConditions = false;
                                 break;
                              }
                           }

                           if (passesConditions) {
                              break;
                           }
                        }
                     }
                  }

                  inRange.add(entity);
               }
            }
         }
      }

      public void evaluateTargetsInBB() {
         if (this.hitbox != null && this.inRange != null && !this.inRange.isEmpty()) {
            long immuneCutoff = System.currentTimeMillis() - this.immuneDelay;
            ObjectIterator<Entry<UUID>> immuneIterator = Object2LongMaps.fastIterator(this.immune);

            while (immuneIterator.hasNext()) {
               if (((Entry)immuneIterator.next()).getLongValue() < immuneCutoff) {
                  immuneIterator.remove();
               }
            }

            this.immuneForeign.values().removeIf(expiry -> expiry < immuneCutoff);
            Optional<AbstractModelEngineSupport> support = Projectile.this.meg();
            boolean hasMEGSupport = support.isPresent();

            for (int i = 0; i < this.inRange.size(); i++) {
               AbstractEntity original = this.inRange.get(i);
               if (this.components.hasTerminated()) {
                  break;
               }

               AbstractEntity base = hasMEGSupport ? support.get().getParent(original) : original;
               if (!(this.bullet instanceof MEGBullet.MEGBulletTracker megBulletTracker && megBulletTracker.getEntityId().equals(base.getUniqueId()))
                  && !base.isDead()
                  && (base == original || !original.isDead())) {
                  AbstractEntity target = Projectile.this.shareSubHitboxCooldown ? base : original;
                  if (!this.immune.containsKey(target.getUniqueId()) && !this.immuneForeign.containsKey(target.getUniqueId())) {
                     boolean hit;
                     if (Projectile.this.hitTargeter.isPresent()) {
                        hit = true;
                     } else if (hasMEGSupport && support.get().overlapsOOBB(this.hitbox, original)) {
                        hit = true;
                     } else {
                        Entity bukkitTarget = original.getBukkitEntity();
                        if (ServerVersion.isFolia() && bukkitTarget != null && !FoliaHelper.isOwnedByCurrentRegion(bukkitTarget)) {
                           BoundingBox hitboxSnapshot = this.hitbox.clone();
                           AbstractLocation originSnapshot = this.currentLocation.clone();
                           Schedulers.of(bukkitTarget).run(() -> this.handleForeignHit(original, target, hitboxSnapshot, originSnapshot));
                           continue;
                        }

                        hit = this.hitbox.overlaps(bukkitTarget.getBoundingBox());
                     }

                     if (hit) {
                        this.immune.put(target.getUniqueId(), System.currentTimeMillis() + this.immuneDelay);
                        String hitBoneId = hasMEGSupport ? support.get().getSubHitboxBoneId(original.getUniqueId()) : null;
                        MythicProjectileHitEvent hitEvent = Events.callAndReturn(new MythicProjectileHitEvent(this, target));
                        if (!hitEvent.isCancelled()) {
                           this.hasHitEntity = true;
                           if (hitBoneId != null) {
                              this.hitSubHitboxes.put(target.getUniqueId(), hitBoneId);
                           }

                           this.targets.add(target);
                           if (Projectile.this.canStopAtEntity(this.data, target)) {
                              this.terminate();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      private void handleForeignHit(AbstractEntity original, AbstractEntity target, BoundingBox hitboxSnapshot, AbstractLocation originSnapshot) {
         if (!this.components.hasTerminated() && !original.isDead() && !target.isDead()) {
            if (hitboxSnapshot.overlaps(original.getBukkitEntity().getBoundingBox())) {
               if (this.immuneForeign.putIfAbsent(target.getUniqueId(), System.currentTimeMillis() + this.immuneDelay) == null) {
                  MythicProjectileHitEvent hitEvent = Events.callAndReturn(new MythicProjectileHitEvent(this, target));
                  if (!hitEvent.isCancelled()) {
                     this.hasHitEntity = true;
                     if (Projectile.this.onHitSkill.isPresent()) {
                        SkillMetadata sData = this.data.deepCloneNoTargets();
                        sData.setEntityTarget(target);
                        sData.setOrigin(originSnapshot);
                        this.applyHitboxVariable(sData, List.of(target));
                        if (Projectile.this.onHitSkill.get().isUsable(sData)) {
                           Projectile.this.onHitSkill.get().execute(sData);
                        }
                     }

                     if (Projectile.this.canStopAtEntity(this.data, target)) {
                        Schedulers.of(this.data.getCaster().getEntity().getBukkitEntity()).run(this::terminate);
                     }
                  }
               }
            }
         }
      }

      protected void executeOnHit(Collection<AbstractEntity> targets, Runnable onHitExecution) {
         Entity anchor = targets.iterator().next().getBukkitEntity();
         if (anchor != null) {
            Schedulers.runOnRegion(anchor, onHitExecution);
         } else {
            onHitExecution.run();
         }
      }

      protected void applyHitboxVariable(SkillMetadata sData, Collection<AbstractEntity> targets) {
         if (!Projectile.this.meg().isEmpty()) {
            List<UUID> targetIds = new ArrayList<>(targets.size());

            for (AbstractEntity target : targets) {
               targetIds.add(target.getUniqueId());
            }

            String boneId = Projectile.consumeUniformHitBone(this.hitSubHitboxes, targetIds);
            VariableRegistry vars = sData.getVariables();
            if (boneId != null) {
               vars.putString("hitbox", boneId);
               vars.putString("meg-hitbox", boneId);
            } else {
               vars.remove("hitbox");
               vars.remove("meg-hitbox");
            }
         }
      }

      public boolean executeProjectileSkill(Optional<Skill> skill, SkillMetadata data, boolean atCaster) {
         if (skill == null) {
            return true;
         } else if (skill.isEmpty()) {
            return true;
         } else {
            data = data.deepClone();
            if (atCaster) {
               data.setEntityTarget(data.getCaster().getEntity());
            } else {
               AbstractLocation location = Projectile.this.bulletType.isPresent() && Projectile.this.bulletType.get() == ProjectileBulletType.ARROW
                  ? this.previousLocation.clone()
                  : this.currentLocation.clone();
               data.setLocationTargets(List.of(location));
               data.setOrigin(location);
            }

            if (skill.get().isUsable(data)) {
               VariableRegistry vars = data.getVariables();
               skill.get().execute(data);
               return true;
            } else {
               return false;
            }
         }
      }

      public void setPower(float p) {
         this.power = p;
      }

      public void multiplyPower(float p) {
         this.power *= p;
      }

      public void addPower(float p) {
         this.power += p;
      }

      public void updateHitbox() {
         float hitRadius = Projectile.this.hitRadius.get(this.context);
         float verticalHitRadius = Projectile.this.verticalHitRadius.get(this.context);
         this.resolvedHitRadius = hitRadius;
         if (hitRadius != 0.0F && verticalHitRadius != 0.0F) {
            try {
               this.hitbox = BoundingBox.of(BukkitAdapter.adapt(this.currentLocation), hitRadius, verticalHitRadius, hitRadius);
            } catch (IllegalArgumentException e) {
            }

            if (this.hitbox != null) {
               if (Projectile.this.interactable && this.interactionEntity != null) {
                  this.interactionEntity.teleport(this.currentLocation.clone().subtract(0.0, verticalHitRadius, 0.0));
               }

               if (Projectile.this.drawHitbox) {
                  Vector min = this.hitbox.getMin();
                  Vector max = this.hitbox.getMax();
                  double particleDistance = 0.5;
                  World world = BukkitAdapter.adapt(this.currentLocation.getWorld());
                  Particle particle = Particle.ELECTRIC_SPARK;

                  for (double y : new double[]{min.getY(), max.getY()}) {
                     for (double z : new double[]{min.getZ(), max.getZ()}) {
                        for (double x = min.getX(); x <= max.getX(); x += particleDistance) {
                           world.spawnParticle(particle, x, y, z, 0);
                        }
                     }
                  }

                  for (double x : new double[]{min.getX(), max.getX()}) {
                     for (double z : new double[]{min.getZ(), max.getZ()}) {
                        for (double y = min.getY(); y <= max.getY(); y += particleDistance) {
                           world.spawnParticle(particle, x, y, z, 0);
                        }
                     }
                  }

                  for (double x : new double[]{min.getX(), max.getX()}) {
                     for (double y : new double[]{min.getY(), max.getY()}) {
                        for (double z = min.getZ(); z <= max.getZ(); z += particleDistance) {
                           world.spawnParticle(particle, x, y, z, 0);
                        }
                     }
                  }
               }
            }
         } else {
            this.hitbox = null;
         }
      }

      public boolean start() {
         Projectile.ensureWorldUnloadListener();
         Projectile.ACTIVE_TRACKERS.add(this);
         this.bind((Terminable)() -> Projectile.ACTIVE_TRACKERS.remove(this));
         if (ServerVersion.isFolia()) {
            UUID casterUUID = this.data.getCaster().getEntity().getUniqueId();
            Events.subscribe(EntityRemoveEvent.class)
               .filter(event -> event.getEntity().getUniqueId().equals(casterUUID))
               .handler(event -> this.terminate())
               .bindWith(this);
         }

         Runnable startRunnable = () -> {
            this.projectileStart();
            this.data.setCallingEvent(this);
            this.updateHitbox();
            if (this.bullet != null) {
               this.bullet.spawn(this.currentLocation);
            }

            if (Projectile.this.interactable) {
               float vhr = Projectile.this.verticalHitRadius.get(this.context);
               Location loc = BukkitAdapter.adapt(this.currentLocation).subtract(0.0, vhr, 0.0);
               Interaction bukkitInteractionEntity = (Interaction)loc.getWorld().spawnEntity(loc, EntityType.INTERACTION);
               bukkitInteractionEntity.setInteractionHeight(vhr * 2.0F);
               bukkitInteractionEntity.setInteractionWidth(Projectile.this.hitRadius.get(this.context) * 2.0F);
               bukkitInteractionEntity.setResponsive(true);
               bukkitInteractionEntity.setPersistent(false);
               this.interactionEntity = BukkitAdapter.adapt(bukkitInteractionEntity);
               Events.subscribe(PlayerInteractEntityEvent.class)
                  .filter(
                     event -> event.getRightClicked().getType() == EntityType.INTERACTION
                        && event.getRightClicked().getUniqueId().equals(this.interactionEntity.getUniqueId())
                  )
                  .handler(event -> {
                     if (Projectile.this.onInteractSkill.isPresent() && Projectile.this.onInteractSkill.get().isUsable(this.data)) {
                        SkillMetadata sData = this.data.deepClone();
                        AbstractPlayer theTrigger = BukkitAdapter.adapt(event.getPlayer());
                        sData.setTrigger(theTrigger);
                        sData.setEntityTarget(theTrigger);
                        sData.setOrigin(this.currentLocation.clone());
                        Projectile.this.onInteractSkill.get().execute(sData);
                     }
                  })
                  .bindWith(this);
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(
                     event -> event.getEntity().getType() == EntityType.INTERACTION
                        && event.getEntity().getUniqueId().equals(this.interactionEntity.getUniqueId())
                  )
                  .handler(event -> {
                     if (Projectile.this.onInteractSkill.isPresent() && Projectile.this.onInteractSkill.get().isUsable(this.data)) {
                        SkillMetadata sData = this.data.deepClone();
                        AbstractEntity theTrigger = BukkitAdapter.adapt(event.getDamager());
                        sData.setTrigger(theTrigger);
                        sData.setEntityTarget(theTrigger);
                        sData.setOrigin(this.currentLocation.clone());
                        Projectile.this.onInteractSkill.get().execute(sData);
                     }
                  })
                  .bindWith(this);
            }

            if (Projectile.this.onStartSkill.isPresent() && Projectile.this.onStartSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               ObjectOpenHashSet<AbstractLocation> targets = new ObjectOpenHashSet();
               targets.add(this.startLocation);
               sData.setLocationTargets(targets);
               sData.setOrigin(this.currentLocation.clone());
               if (this.bullet != null) {
                  sData.setMetadata("bullet", this.bullet);
               }

               Projectile.this.onStartSkill.get().execute(sData);
            }

            this.task = Schedulers.of(this.data.getCaster().getEntity().getBukkitEntity()).runRepeating(this, 0L, (long)this.tickInterval);
            this.task.bindWith(this);
            if (this.requireLineOfSight()) {
               AbstractLocation origin;
               if (Projectile.this.fromOrigin) {
                  origin = this.data.getOrigin().clone().add(0.0, Projectile.this.startYOffset.get(this.context), 0.0);
               } else {
                  origin = this.data.getCaster().getLocation().add(0.0, Projectile.this.startYOffset.get(this.context), 0.0);
               }

               RayTraceResult traceResult = Projectile.this.getPlugin()
                  .getVolatileCodeHandler()
                  .getWorldHandler()
                  .rayTraceBlock(origin, this.currentLocation, FluidCollisionMode.NEVER, true);
               if (traceResult != null && traceResult.getHitBlock() != null && !traceResult.getHitBlock().isEmpty()) {
                  AbstractLocation hitLocation = BukkitAdapter.adapt(traceResult.getHitPosition()).toLocation(this.currentLocation.getWorld());
                  hitLocation.setPitch(this.currentLocation.getPitch());
                  hitLocation.setYaw(this.currentLocation.getYaw());
                  this.currentLocation = hitLocation;
                  this.terminate();
               }
            }
         };
         if (ServerVersion.isFolia() && this.data.getCaster().getEntity().getBukkitEntity() != null) {
            Schedulers.of(this.data.getCaster().getEntity().getBukkitEntity()).run(startRunnable);
         } else {
            Promise.start().thenRunSync(startRunnable);
         }

         return true;
      }

      @Override
      public int getTimesRan() {
         return this.task.getTimesRan();
      }

      private boolean requireLineOfSight() {
         return Projectile.this.requireLineOfSight == Projectile.ProjectileTriOption.TRUE
            || Projectile.this.requireLineOfSight == Projectile.ProjectileTriOption.PLAYERS_ONLY && this.data.getCaster().getEntity().isPlayer();
      }

      public void setDirection(AbstractVector direction) {
         this.currentVelocity = direction.normalize().multiply(this.getVelocityMagnitude());
      }

      protected void clampToMaxRange() {
         if (!(this.currentLocation.distanceSquared(this.startLocation) < this.maxDistanceSquared)) {
            AbstractVector offset = this.previousLocation.toVector().subtract(this.startLocation.toVector());
            AbstractVector step = this.currentLocation.toVector().subtract(this.previousLocation.toVector());
            double a = step.lengthSquared();
            double b = offset.dot(step);
            double c = offset.lengthSquared() - this.maxDistanceSquared;
            double t = c < 0.0 ? (Math.sqrt(b * b - a * c) - b) / a : 0.0;
            this.currentLocation.subtract(step.multiply(1.0 - t));
            this.reachedMaxRange = true;
         }
      }

      @Override
      public void run() {
         if (this.data.getCaster() != null && this.data.getCaster().getEntity().isDead()) {
            this.terminate();
         } else if (this.reachedMaxRange || this.currentLocation.distanceSquared(this.startLocation) >= this.maxDistanceSquared) {
            this.terminate();
         } else if (this.ticksRemaining <= 0) {
            this.terminate();
         } else {
            if (Projectile.this.tickInterpolation > 0) {
               for (int i = 0; i <= Projectile.this.tickInterpolation; i++) {
                  this.projectileMove();
                  if (this.bullet != null) {
                     this.bullet.tick(this.getCurrentLocation());
                  }

                  this.updateHitbox();
                  this.evaluatePotentialTargets();
                  this.projectileTick();
                  if (this.reachedMaxRange) {
                     break;
                  }
               }
            } else {
               this.projectileMove();
               if (this.bullet != null) {
                  this.bullet.tick(this.getCurrentLocation());
               }

               this.updateHitbox();
               this.evaluatePotentialTargets();
               this.projectileTick();
            }

            this.ticksRemaining = this.ticksRemaining - this.tickInterval;
         }
      }

      public abstract void projectileStart();

      public abstract void projectileMove();

      public abstract void projectileTick();

      public void projectileEnd() {
      }

      @Override
      public void close() {
         if (!this.components.hasTerminated()) {
            this.projectileEnd();
            if (Projectile.this.bulletType.isPresent()) {
               if (ServerVersion.isFolia()) {
                  if (this.bullet != null) {
                     Schedulers.of(BukkitAdapter.adapt(this.currentLocation)).runLater(this.bullet::despawn, this.deathDelay);
                  }
               } else {
                  Schedulers.of(this.data.getCaster().getEntity().getBukkitEntity()).runLater(() -> {
                     if (this.bullet != null) {
                        this.bullet.despawn();
                     }
                  }, this.deathDelay);
               }
            }

            if (Projectile.this.interactable && this.interactionEntity != null) {
               this.interactionEntity.remove();
            }

            if (!this.suppressEndSkill
               && Projectile.this.onEndSkill.isPresent()
               && (Projectile.this.doEndSkillOnHit || !this.hasHitEntity)
               && Projectile.this.onEndSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               Projectile.this.onEndSkill.get().execute(sData.setOrigin(this.currentLocation).setLocationTarget(this.currentLocation));
            }

            if (this.inRange != null) {
               this.inRange.clear();
            }
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

      @Override
      public boolean hasTerminated() {
         return this.components.hasTerminated();
      }

      @Override
      public <T extends AutoCloseable> T bind(T terminable) {
         this.components.accept((Terminable)terminable);
         return terminable;
      }
   }

   public static enum ProjectileTriOption {
      TRUE,
      FALSE,
      PLAYERS_ONLY;
   }

   protected static enum ProjectileType {
      NORMAL,
      METEOR;
   }
}
