package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.skills.projectiles.ProjectileSurfaceMode;
import io.lumine.mythic.core.skills.projectiles.bullet.MEGBullet;
import io.lumine.mythic.core.utils.BlockUtil;
import io.lumine.mythic.core.utils.VectorUtils;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "orbital",
   aliases = {"o"},
   description = "Applies an orbital aura to the target"
)
public class OrbitalMechanic extends Aura implements ITargetedEntitySkill, ITargetedLocationSkill {
   public static final Set<AbstractEntity> BULLET_ENTITIES = new ObjectOpenHashSet();
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the orbital aura"
   )
   protected PlaceholderFloat radius;
   @MythicField(
      name = "hitRadius",
      aliases = {"hr"},
      description = "The hit radius of the orbital aura"
   )
   protected PlaceholderFloat hitRadius;
   @MythicField(
      name = "verticalHitRadius",
      aliases = {"vhr", "vr"},
      description = "The vertical hit radius of the orbital aura"
   )
   protected PlaceholderFloat verticalHitRadius;
   @MythicField(
      name = "points",
      aliases = {"p"},
      description = "The number of points in the orbital aura"
   )
   protected PlaceholderInt points;
   @MythicField(
      name = "velocity",
      description = "The velocity of the orbital aura"
   )
   protected double velocity;
   @MythicField(
      name = "rotate",
      description = "Whether the orbital aura should rotate"
   )
   protected boolean rotate;
   @MythicField(
      name = "reversed",
      description = "Whether the orbital aura should be reversed"
   )
   protected boolean reversed;
   @MythicField(
      name = "startingStep",
      aliases = {"sp"},
      description = "The starting step of the orbital aura"
   )
   protected PlaceholderInt startingStep;
   @MythicField(
      name = "xRotation",
      aliases = {"rotx", "rx"},
      description = "The X rotation of the orbital aura"
   )
   protected PlaceholderDouble xRotation;
   @MythicField(
      name = "yRotation",
      aliases = {"roty", "ry"},
      description = "The Y rotation of the orbital aura"
   )
   protected PlaceholderDouble yRotation;
   @MythicField(
      name = "zRotation",
      aliases = {"rotz", "rz"},
      description = "The Z rotation of the orbital aura"
   )
   protected PlaceholderDouble zRotation;
   @MythicField(
      name = "xOffset",
      aliases = {"offx", "ox"},
      description = "The X offset of the orbital aura"
   )
   protected PlaceholderDouble xOffset;
   @MythicField(
      name = "yOffset",
      aliases = {"offy", "oy"},
      description = "The Y offset of the orbital aura"
   )
   protected PlaceholderDouble yOffset;
   @MythicField(
      name = "zOffset",
      aliases = {"offz", "oz"},
      description = "The Z offset of the orbital aura"
   )
   protected PlaceholderDouble zOffset;
   @Getter
   @MythicField(
      name = "interpolation",
      aliases = {"tickinterpolation", "ti"},
      description = "Interpolates additional points between each tick of the projectile, running onTick multiple times",
      defValue = "0"
   )
   protected int tickInterpolation;
   @MythicField(
      name = "immuneDelay",
      aliases = {"immune", "id"},
      description = "Sets the immunity delay (when the target can be hit by the projectile again)",
      defValue = "2000"
   )
   protected PlaceholderDuration immuneDelay;
   @Getter
   @MythicFields({@MythicField(
         name = "hugSurface",
         description = "Whether the projectile will hug the surface",
         defValue = "false"
      ), @MythicField(
         name = "hugLiquid",
         aliases = {"hugWater", "hugLava"},
         description = "If hugSurface is set, determines whether the projectile will hug liquid",
         defValue = "false"
      )})
   protected ProjectileSurfaceMode surfaceMode = ProjectileSurfaceMode.NONE;
   @MythicField(
      name = "heightFromSurface",
      aliases = {"hfs"},
      description = "    The offset depends on the type of the projectile and if hugSurface is set.\n    NORMAL - how high above the surface the projectile will glide.\n    METEOR - how high above the surface the projectile starts above the target\n",
      defValue = "0.5"
   )
   protected PlaceholderFloat heightFromSurface;
   @MythicField(
      name = "maxClimbHeight",
      aliases = {"mch"},
      description = "The number of attempts the projectile will try to increase its y-location before terminating the projectile",
      defValue = "3"
   )
   protected PlaceholderFloat maxClimbHeight;
   @MythicField(
      name = "maxDropHeight",
      aliases = {"mdh"},
      description = "The number of attempts the projectile will try to decrease its y-location before terminating the projectile",
      defValue = "10"
   )
   protected PlaceholderFloat maxDropHeight;
   @MythicField(
      name = "onHitSkill",
      aliases = {"onhit", "oh"},
      description = "The name of the skill to trigger when the orbital aura hits a target"
   )
   protected String onHitSkillName;
   protected Optional<Skill> onHitSkill = Optional.empty();
   @MythicField(
      name = "hitSelf",
      aliases = {"hs"},
      description = "Whether the orbital aura can hit the caster"
   )
   protected boolean hitSelf;
   @MythicField(
      name = "hitPlayers",
      aliases = {"hp"},
      description = "Whether the orbital aura can hit players"
   )
   protected boolean hitPlayers;
   @MythicField(
      name = "hitNonPlayers",
      aliases = {"hnp"},
      description = "Whether the orbital aura can hit non-players"
   )
   protected boolean hitNonPlayers;
   @MythicField(
      name = "hitConditions",
      aliases = {"conditions", "cond", "c"},
      defValue = "NONE",
      version = "4.9",
      premium = true,
      description = "Conditions applied to the hit target"
   )
   protected List<SkillCondition> hitConditions = null;
   @MythicField(
      name = "stopConditions",
      aliases = {"stpcond"},
      defValue = "NONE",
      version = "4.9",
      premium = true,
      description = "Conditions applied to the stop the projectile when hitting the target"
   )
   protected List<SkillCondition> stopConditions = null;
   @MythicField(
      name = "shareSubHitboxCooldown",
      aliases = {"shcd"},
      description = "",
      defValue = "true"
   )
   protected boolean shareSubHitboxCooldown;
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
   @MythicField(
      name = "castAsOrbital",
      aliases = {"cao"},
      description = "Whether skills are casted as the orbital"
   )
   protected boolean castAsOrbital;
   protected Optional<ProjectileBulletType> bulletType;
   protected ProjectileBullet bullet;
   protected double angularVelocityX = 0.015707963267948967;
   protected double angularVelocityY = 0.018479956785822312;
   protected double angularVelocityZ = 0.02026833970057931;

   public OrbitalMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 4.0F);
      this.hitRadius = mlc.getPlaceholderFloat(new String[]{"hitradius", "hr"}, 1.0F);
      this.verticalHitRadius = mlc.getPlaceholderFloat(new String[]{"verticalhitradius", "vhr", "vr"}, this.hitRadius.toString());
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 32);
      this.startingStep = mlc.getPlaceholderInteger(new String[]{"startingPoint", "sp"}, 0);
      this.tickInterpolation = mlc.getInteger(new String[]{"tickinterpolation", "interpolation", "ti"}, 0);
      this.immuneDelay = mlc.getPlaceholderDuration(new String[]{"immunedelay", "immune", "iD"}, DurationUnit.MILLISECONDS, "2000");
      this.xRotation = mlc.getPlaceholderDouble(new String[]{"rotationx", "rotx", "rx"}, 0.0);
      this.yRotation = mlc.getPlaceholderDouble(new String[]{"rotationy", "roty", "ry"}, 0.0);
      this.zRotation = mlc.getPlaceholderDouble(new String[]{"rotationz", "rotz", "rz"}, 0.0);
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"offsetx", "offx", "ox"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"offsety", "offy", "oy"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"offsetz", "offz", "oz"}, 0.0);
      this.angularVelocityX = mlc.getDouble(new String[]{"angularvelocityx", "avx", "vx"}, 0.0);
      this.angularVelocityY = mlc.getDouble(new String[]{"angularvelocityy", "avy", "vy"}, 0.0);
      this.angularVelocityZ = mlc.getDouble(new String[]{"angularvelocityz", "avz", "vz"}, 0.0);
      this.angularVelocityX = this.angularVelocityX == 0.0 ? 0.0 : Math.PI / this.angularVelocityX;
      this.angularVelocityY = this.angularVelocityY == 0.0 ? 0.0 : Math.PI / this.angularVelocityY;
      this.angularVelocityZ = this.angularVelocityZ == 0.0 ? 0.0 : Math.PI / this.angularVelocityZ;
      this.rotate = mlc.getBoolean(new String[]{"rotate"}, this.angularVelocityX > 0.0 || this.angularVelocityY > 0.0 || this.angularVelocityZ > 0.0);
      this.reversed = mlc.getBoolean(new String[]{"reversed", "reverse", "backwards"}, false);
      this.hitSelf = mlc.getBoolean(new String[]{"hitself", "hs"}, false);
      this.hitPlayers = mlc.getBoolean(new String[]{"hitplayers", "hp"}, true);
      this.hitNonPlayers = mlc.getBoolean(new String[]{"hitnonplayers", "hnp"}, false);
      this.castAsOrbital = mlc.getBoolean(new String[]{"castasorbital", "cao"}, false);
      this.drawHitbox = mlc.getBoolean(new String[]{"drawhitbox"}, false);
      String hitConditionString = mlc.getString(new String[]{"hitconditions", "conditions", "cond", "c"}, "null");
      String stopConditionString = mlc.getString(new String[]{"stopconditions", "stpcond"}, "null");
      String hitTargeterString = mlc.getString(new String[]{"hitTargeter", "htr"}, null);
      this.shareSubHitboxCooldown = mlc.getBoolean(new String[]{"shareSubHitboxCooldown", "shcd"}, true);
      boolean hugSurface = mlc.getBoolean(new String[]{"hugsurface"}, false);
      this.heightFromSurface = mlc.getPlaceholderFloat(new String[]{"heightfromsurface", "hfs"}, 0.5F);
      this.maxClimbHeight = mlc.getPlaceholderFloat(new String[]{"maxclimbheight", "mch"}, 3.0F);
      this.maxDropHeight = mlc.getPlaceholderFloat(new String[]{"maxdropheight", "mdh"}, 10.0F);
      if (hugSurface) {
         boolean hugWater = mlc.getBoolean(new String[]{"hugliquid", "hugwater", "huglava"}, false);
         if (hugWater) {
            this.surfaceMode = ProjectileSurfaceMode.WATER;
         } else {
            this.surfaceMode = ProjectileSurfaceMode.SURFACE;
         }
      }

      this.onHitSkillName = mlc.getString(new String[]{"onhitskill", "onhit", "oh"});
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

      this.getManager().queueSecondPass(() -> {
         if (this.onHitSkillName != null) {
            this.onHitSkill = this.getManager().getSkill(file, this, this.onHitSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded orbital onHit pointing at " + this.onHitSkillName);
         if (MythicBukkit.isVolatile()) {
            if (stopConditionString != null) {
               this.stopConditions = this.getManager().getConditions(stopConditionString);
            }

            if (hitConditionString != null) {
               this.hitConditions = this.getManager().getConditions(hitConditionString);
            }

            if (hitTargeterString != null) {
               this.hitTargeter = Optional.of(this.parseSkillTargeter(hitTargeterString));
            }
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new OrbitalMechanic.OrbitalTracker(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OrbitalMechanic.OrbitalTracker(data, target);
      return SkillResult.SUCCESS;
   }

   public class OrbitalTracker extends Aura.AuraTracker implements IParentSkill, Runnable, ProjectileBulletableTracker {
      private float radius;
      private float power;
      private int step;
      private PlaceholderDouble yOffset;
      protected double xRotation = 0.0;
      protected double yRotation = 0.0;
      protected double zRotation = 0.0;
      @Getter
      private SkillMetadata data;
      @Getter
      private PlaceholderContext context;
      @Getter
      protected AbstractLocation currentLocation;
      @Getter
      protected AbstractLocation previousLocation;
      @Getter
      protected AbstractVector currentVelocity;
      @Getter
      protected BoundingBox hitbox;
      @Getter
      protected ProjectileBullet.BulletTracker bullet = null;
      private ObjectArrayList<AbstractEntity> inRange = new ObjectArrayList();
      private ObjectOpenHashSet<AbstractEntity> targets = new ObjectOpenHashSet();
      private Object2LongOpenHashMap<UUID> immune = new Object2LongOpenHashMap();
      private long immuneDelay;

      public OrbitalTracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         if (OrbitalMechanic.this.castAsOrbital) {
            this.context = PlaceholderContext.builder().meta(data.deepClone()).entity(entity).build();
            this.data = this.context.skillMetadata();
            data.setCallingEvent(this);
         } else {
            this.context = PlaceholderContext.builder().meta(data).entity(entity).build();
            this.data = this.context.skillMetadata();
         }

         this.step = OrbitalMechanic.this.startingStep.get(data);
         this.radius = OrbitalMechanic.this.radius.get(data);
         this.power = data.getPower();
         this.yOffset = OrbitalMechanic.this.yOffset;
         this.immuneDelay = OrbitalMechanic.this.immuneDelay.get(data, entity).getMilliseconds();
         this.xRotation = OrbitalMechanic.this.xRotation.get(data, entity);
         this.yRotation = OrbitalMechanic.this.yRotation.get(data, entity);
         this.zRotation = OrbitalMechanic.this.zRotation.get(data, entity);
         this.start();
      }

      public OrbitalTracker(SkillMetadata data, AbstractLocation location) {
         super(location, data);
         if (OrbitalMechanic.this.castAsOrbital) {
            this.context = PlaceholderContext.builder().meta(data.deepClone()).location(location).build();
            this.data = this.context.skillMetadata();
            data.setCallingEvent(this);
         } else {
            this.context = PlaceholderContext.builder().meta(data).location(location).build();
            this.data = this.context.skillMetadata();
         }

         this.step = OrbitalMechanic.this.startingStep.get(data);
         this.radius = OrbitalMechanic.this.radius.get(data);
         this.power = data.getPower();
         this.yOffset = OrbitalMechanic.this.yOffset;
         this.immuneDelay = OrbitalMechanic.this.immuneDelay.get(data).getMilliseconds();
         this.xRotation = OrbitalMechanic.this.xRotation.get(data);
         this.yRotation = OrbitalMechanic.this.yRotation.get(data);
         this.zRotation = OrbitalMechanic.this.zRotation.get(data);
         this.start();
      }

      public AbstractLocation getLocation() {
         AbstractLocation loc = null;
         if (this.entity.isPresent()) {
            loc = this.entity
               .get()
               .getLocation()
               .add(
                  OrbitalMechanic.this.xOffset.get(this.getSkillMetadata()),
                  this.yOffset.get(this.getSkillMetadata()),
                  OrbitalMechanic.this.zOffset.get(this.getSkillMetadata())
               );
         } else if (this.location.isPresent()) {
            loc = this.location
               .get()
               .clone()
               .add(
                  OrbitalMechanic.this.xOffset.get(this.getSkillMetadata()),
                  this.yOffset.get(this.getSkillMetadata()),
                  OrbitalMechanic.this.zOffset.get(this.getSkillMetadata())
               );
         }

         return loc;
      }

      public AbstractVector getNextPositionOffset() {
         double inc = (Math.PI * 2) / OrbitalMechanic.this.points.get(this.skillMetadata);
         double angle = OrbitalMechanic.this.reversed ? -this.step * inc : this.step * inc;
         AbstractVector v = new AbstractVector(0, 0, 0);
         v.setX(Math.cos(angle) * this.radius);
         v.setZ(Math.sin(angle) * this.radius);
         VectorUtils.rotateVector(v, this.xRotation, this.yRotation, this.zRotation);
         if (OrbitalMechanic.this.rotate) {
            VectorUtils.rotateVector(
               v,
               OrbitalMechanic.this.angularVelocityX * this.step,
               OrbitalMechanic.this.angularVelocityY * this.step,
               OrbitalMechanic.this.angularVelocityZ * this.step
            );
         }

         return v;
      }

      @Override
      public double getVelocityMagnitude() {
         if (this.currentVelocity == null) {
            return 0.0;
         } else {
            double x = this.currentVelocity.getX();
            double y = this.currentVelocity.getY();
            double z = this.currentVelocity.getZ();
            return Math.sqrt(x * x + y * y + z * z);
         }
      }

      @Override
      public void auraStart() {
         if (this.currentLocation == null) {
            this.currentLocation = this.getLocation();
            this.currentLocation = this.currentLocation.add(this.getNextPositionOffset());
            this.previousLocation = this.currentLocation.clone();
            this.currentVelocity = new AbstractVector(0, 0, 0);
         }

         if (OrbitalMechanic.this.bullet != null) {
            Schedulers.ensureSync(() -> {
               this.bullet = OrbitalMechanic.this.bullet.create(this, null);
               this.bullet.spawn(this.previousLocation);
            });
         }

         this.updateHitbox();
         this.evaluatePotentialTargets();
         this.executeAuraSkill(OrbitalMechanic.this.onStartSkill, this.skillMetadata.deepClone().setOrigin(this.previousLocation));
      }

      @Override
      public void auraTick() {
         this.orbitalMove();
         this.updateHitbox();
         this.evaluatePotentialTargets();
         this.executeAuraSkill(
            OrbitalMechanic.this.onTickSkill, this.skillMetadata.deepCloneNoTargets().setOrigin(this.currentLocation).setLocationTarget(this.currentLocation)
         );
         if (this.bullet != null) {
            this.bullet.tick(this.currentLocation);
         }

         this.orbitalTick();
         this.step++;
      }

      @Override
      public void auraStop() {
         this.executeAuraSkill(OrbitalMechanic.this.onEndSkill, this.skillMetadata.deepClone().setOrigin(this.previousLocation));
         if (OrbitalMechanic.this.bulletType.isPresent()) {
            Schedulers.sync().runLater(() -> {
               if (this.bullet != null) {
                  this.bullet.despawn();
               }
            }, 0L);
         }
      }

      private void orbitalMove() {
         if (this.previousLocation == null) {
            this.previousLocation = this.getLocation().add(this.getNextPositionOffset());
            this.currentVelocity = new AbstractVector(0, 0, 0);
         } else {
            this.previousLocation = this.currentLocation == null ? this.getLocation().add(this.getNextPositionOffset()) : this.currentLocation.clone();
         }

         this.currentLocation = this.getLocation().add(this.getNextPositionOffset());
         if (OrbitalMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE
            && (this.currentLocation.getBlockX() != this.previousLocation.getBlockX() || this.currentLocation.getBlockZ() != this.previousLocation.getBlockZ())
            )
          {
            float heightFromSurface = OrbitalMechanic.this.heightFromSurface.get(this.data);
            Block b = BukkitAdapter.adapt(this.currentLocation).subtract(0.0, heightFromSurface, 0.0).getBlock();
            if (BlockUtil.isPathable(b, OrbitalMechanic.this.surfaceMode)) {
               int attempts = 0;
               boolean ok = false;

               while (attempts++ < OrbitalMechanic.this.maxDropHeight.get(this.data)) {
                  b = b.getRelative(BlockFace.DOWN);
                  if (!BlockUtil.isPathable(b, OrbitalMechanic.this.surfaceMode)) {
                     ok = true;
                     break;
                  }

                  this.currentLocation.subtract(0.0, 1.0, 0.0);
               }
            } else {
               int attempts = 0;
               boolean ok = false;

               while (attempts++ < OrbitalMechanic.this.maxClimbHeight.get(this.data)) {
                  b = b.getRelative(BlockFace.UP);
                  this.currentLocation.add(0.0, 1.0, 0.0);
                  if (BlockUtil.isPathable(b)) {
                     ok = true;
                     break;
                  }
               }
            }

            this.currentLocation.setY((int)this.currentLocation.getY() + heightFromSurface);
         }

         this.currentVelocity = this.currentLocation.toVector().subtract(this.previousLocation.toVector());
      }

      private void orbitalTick() {
         this.evaluateTargetsInBB();
         if (!this.targets.isEmpty()) {
            if (OrbitalMechanic.this.onHitSkill.isPresent()) {
               SkillMetadata sData = this.skillMetadata.deepCloneNoTargets();
               sData.setEntityTargets(new ArrayList<>(this.targets));
               sData.setOrigin(this.currentLocation.clone());
               if (OrbitalMechanic.this.onHitSkill.get().isUsable(sData)) {
                  OrbitalMechanic.this.onHitSkill.get().execute(sData);
               }
            }

            this.consumeCharge();
            this.targets.clear();
         }
      }

      public void setRadius(float p) {
         this.radius = p;
      }

      public void multiplyRadius(float p) {
         this.radius *= p;
      }

      public void addRadius(float p) {
         this.radius += p;
      }

      public void multiplyYOffset(float value) {
         this.yOffset = PlaceholderDouble.of(String.valueOf(this.yOffset.get() * value));
      }

      public void addYOffset(float value) {
         this.yOffset = PlaceholderDouble.of(String.valueOf(this.yOffset.get() + value));
      }

      public void setYOffset(float value) {
         this.yOffset = PlaceholderDouble.of(String.valueOf(value));
      }

      protected Optional<AbstractModelEngineSupport> meg() {
         return OrbitalMechanic.this.getPlugin().getCompatibility().getModelEngine();
      }

      public void updateHitbox() {
         float hitRadius = OrbitalMechanic.this.hitRadius.get(this.data);
         float verticalHitRadius = OrbitalMechanic.this.verticalHitRadius.get(this.data);
         if (hitRadius != 0.0F && verticalHitRadius != 0.0F) {
            try {
               this.hitbox = BoundingBox.of(BukkitAdapter.adapt(this.currentLocation), hitRadius, verticalHitRadius, hitRadius);
            } catch (IllegalArgumentException e) {
            }

            if (this.hitbox != null) {
               if (OrbitalMechanic.this.drawHitbox) {
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

      private void evaluatePotentialTargets() {
         if (OrbitalMechanic.this.hitSelf || OrbitalMechanic.this.hitPlayers || OrbitalMechanic.this.hitNonPlayers) {
            ObjectArrayList<AbstractEntity> inRange = new ObjectArrayList();
            AbstractEntity casterEntity = this.data.getCaster().getEntity();
            UUID casterUUID = casterEntity.getUniqueId();
            Optional<AbstractModelEngineSupport> megSupport = this.meg();
            boolean hasMEGSupport = megSupport.isPresent();
            boolean hasHitConditions = OrbitalMechanic.this.hitConditions != null;
            Collection<AbstractEntity> entitiesToProcess = null;
            if (OrbitalMechanic.this.hitTargeter.isPresent()) {
               SkillTargeter targeter = OrbitalMechanic.this.hitTargeter.get();
               AbstractLocation origin = this.currentLocation.clone();
               double dx = origin.getX() - this.previousLocation.getX();
               double dz = origin.getZ() - this.previousLocation.getZ();
               double dy = origin.getY() - this.previousLocation.getY();
               double hLen = Math.sqrt(dx * dx + dz * dz);
               origin.setYaw((float)Math.toDegrees(Math.atan2(-dx, dz)));
               origin.setPitch((float)Math.toDegrees(Math.atan2(dy, hLen)));
               SkillMetadata sData = this.data.deeperClone();
               sData.setOrigin(origin);
               sData = OrbitalMechanic.this.evaluateTargets(sData, targeter);
               if (sData.getEntityTargets() != null && !sData.getEntityTargets().isEmpty()) {
                  entitiesToProcess = sData.getEntityTargets();
               }
            } else {
               float hitRadiusValue = OrbitalMechanic.this.hitRadius.get(this.data);
               entitiesToProcess = OrbitalMechanic.this.getPlugin()
                  .getVolatileCodeHandler()
                  .getWorldHandler()
                  .getEntitiesNearLocation(this.currentLocation, hitRadiusValue);
            }

            if (entitiesToProcess != null && !entitiesToProcess.isEmpty()) {
               boolean hasBulletEntities = !OrbitalMechanic.BULLET_ENTITIES.isEmpty();
               Iterator iterator = entitiesToProcess.iterator();

               while (true) {
                  AbstractEntity processedEntity;
                  while (true) {
                     if (!iterator.hasNext()) {
                        this.inRange = inRange;
                        return;
                     }

                     AbstractEntity entity = (AbstractEntity)iterator.next();
                     processedEntity = entity;
                     if (entity.isLiving()
                        && !entity.isCitizensNPC()
                        && (!hasBulletEntities || !OrbitalMechanic.BULLET_ENTITIES.contains(entity))
                        && (OrbitalMechanic.this.hitSelf || !entity.getUniqueId().equals(casterUUID))
                        && (
                           OrbitalMechanic.this.hitSelf && entity.getUniqueId().equals(casterUUID)
                              || (OrbitalMechanic.this.hitPlayers || !entity.isPlayer()) && (OrbitalMechanic.this.hitNonPlayers || entity.isPlayer())
                        )) {
                        if (!hasHitConditions) {
                           break;
                        }

                        boolean passesConditions = true;

                        for (SkillCondition condition : OrbitalMechanic.this.hitConditions) {
                           if (!condition.evaluateToEntity(this.data, processedEntity)) {
                              passesConditions = false;
                              break;
                           }
                        }

                        if (passesConditions) {
                           break;
                        }
                     }
                  }

                  inRange.add(processedEntity);
               }
            } else {
               this.inRange = inRange;
            }
         }
      }

      public void evaluateTargetsInBB() {
         if (this.hitbox != null && this.inRange != null && !this.inRange.isEmpty()) {
            this.immune.entrySet().removeIf(entry -> (Long)entry.getValue() < System.currentTimeMillis() - this.immuneDelay);
            ObjectListIterator objectListIterator = this.inRange.iterator();

            while (objectListIterator.hasNext()) {
               AbstractEntity original = (AbstractEntity)objectListIterator.next();
               Optional<AbstractModelEngineSupport> support = this.meg();
               AbstractEntity base = support.isPresent() ? support.get().getParent(original) : original;
               if (!(this.bullet instanceof MEGBullet.MEGBulletTracker megBulletTracker && megBulletTracker.getEntityId().equals(base.getUniqueId()))
                  && !base.isDead()
                  && !original.isDead()
                  && (!OrbitalMechanic.this.shareSubHitboxCooldown || !this.immune.containsKey(base.getUniqueId()))
                  && (OrbitalMechanic.this.shareSubHitboxCooldown || !this.immune.containsKey(original.getUniqueId()))) {
                  boolean hit = OrbitalMechanic.this.hitTargeter
                     .<Boolean>map(targeter1 -> true)
                     .orElseGet(
                        () -> support.isPresent() && support.get().overlapsOOBB(this.hitbox, original)
                           ? true
                           : this.hitbox.overlaps(original.getBukkitEntity().getBoundingBox())
                     );
                  if (hit) {
                     AbstractEntity target = OrbitalMechanic.this.shareSubHitboxCooldown ? base : original;
                     this.immune.put(target.getUniqueId(), System.currentTimeMillis() + this.immuneDelay);
                     this.targets.add(target);
                     if (this.canStopAtEntity(this.data, target)) {
                        break;
                     }
                  }
               }
            }
         }
      }

      protected boolean canStopAtEntity(SkillMetadata data, AbstractEntity target) {
         if (OrbitalMechanic.this.stopConditions != null) {
            for (SkillCondition condition : OrbitalMechanic.this.stopConditions) {
               if (!condition.evaluateToEntity(data, target)) {
                  return false;
               }
            }
         }

         return false;
      }
   }
}
