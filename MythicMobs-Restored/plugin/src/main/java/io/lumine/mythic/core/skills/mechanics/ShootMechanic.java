package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import lombok.Getter;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@MythicMechanic(
   author = "Ashijin",
   name = "shoot",
   aliases = {"shootprojetile"},
   description = "Shoots a projectile at the target location"
)
public class ShootMechanic extends DamagingMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @Getter
   protected ShootMechanic.ProjectileType projectileType = ShootMechanic.ProjectileType.REGULAR;
   protected PlaceholderDouble damage;
   protected PlaceholderInt maxdistance;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat startForwardOffset;
   protected PlaceholderFloat startSideOffset;
   protected PlaceholderDouble projectileVelocity;
   protected PlaceholderFloat projectileVelocityVertOffset;
   protected PlaceholderFloat projectileVelocityHorizOffset;
   protected float projectileVelocityAccuracy;
   protected float projectileVelocityVertNoise;
   protected float projectileVelocityHorizNoise;
   protected float projectileVelocityVertNoiseBase;
   protected float projectileVelocityHorizNoiseBase;
   protected boolean powerAffectsVelocity;
   protected Class<? extends Projectile> projectileClass;
   protected ItemStack projectileItem;
   protected Optional<Skill> onTickSkill = Optional.empty();
   protected Optional<Skill> onHitSkill = Optional.empty();
   protected Optional<Skill> onEndSkill = Optional.empty();
   protected PlaceholderDuration tickInterval;
   protected String onTickSkillName;
   protected String onHitSkillName;
   protected String onEndSkillName;
   protected String onStartSkillName;
   @Getter
   protected Boolean adjustVelocity;
   @Getter
   protected Boolean bounce;
   @Getter
   protected Boolean canPickup;
   @Getter
   protected int pierceLevel;
   @Getter
   protected int knockbackStrength;
   @Getter
   protected PlaceholderDuration expirationTime;
   protected boolean gravity;
   protected boolean calculateFiringAngle;
   @Getter
   protected Boolean fromOrigin;
   @Getter
   protected boolean offsetFromCaster;
   @Getter
   protected PlaceholderString potionEffectType;
   @Getter
   protected PlaceholderDuration potionDuration;
   @Getter
   protected PlaceholderInt potionAmplifier;
   protected boolean overwrite;
   protected boolean ambientParticles;
   protected boolean hasParticles;
   protected boolean hasIcon;
   @Getter
   protected String potionColor;
   @Getter
   protected PlaceholderString tridentItem;
   @Getter
   protected PlaceholderString thrownItem;
   protected Optional<String> disguise;

   public ShootMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strProjectile = mlc.getString(new String[]{"type", "t"}, "arrow");
      this.damage = mlc.getPlaceholderDouble(new String[]{"damage", "d", "amount"}, 5.0);
      this.maxdistance = mlc.getPlaceholderInteger(new String[]{"maxdistance", "md"}, 64);
      this.projectileVelocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v"}, 1.0);
      this.projectileVelocityVertOffset = mlc.getPlaceholderFloat(new String[]{"verticaloffset", "vo"}, 0.0F);
      this.projectileVelocityHorizOffset = mlc.getPlaceholderFloat(new String[]{"horizontaloffset", "ho"}, 0.0F);
      this.powerAffectsVelocity = mlc.getBoolean(new String[]{"poweraffectsvelocity", "pav"}, true);
      this.bounce = mlc.getBoolean(new String[]{"bounce"}, false);
      this.canPickup = mlc.getBoolean(new String[]{"pickup"}, false);
      this.knockbackStrength = mlc.getInteger(new String[]{"knockback", "kb"}, 0);
      this.pierceLevel = mlc.getInteger(new String[]{"pierceLevel", "pl"}, 0);
      this.gravity = mlc.getBoolean(new String[]{"gravity", "g"}, true);
      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"interval", "int", "i"}, DurationUnit.TICKS, "4");
      this.expirationTime = mlc.getPlaceholderDuration(new String[]{"expiration", "expire", "e", "duration"}, DurationUnit.TICKS, "100");
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo"}, 0.0F);
      this.startForwardOffset = mlc.getPlaceholderFloat(new String[]{"forwardoffset", "startfoffset", "sfo"}, 1.0F);
      float sideOffset = mlc.getFloat(new String[]{"sideoffset", "soffset", "so"}, 0.0F);
      this.startSideOffset = mlc.getPlaceholderFloat(new String[]{"startsideoffset", "ssoffset", "sso"}, sideOffset);
      this.adjustVelocity = mlc.getBoolean(new String[]{"adjustvelocity", "av"}, true);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.calculateFiringAngle = mlc.getBoolean(new String[]{"calculatefiringangle", "cfa"}, false);
      this.disguise = Optional.ofNullable(mlc.getString(new String[]{"disguise", "d"}, null));
      this.projectileVelocityAccuracy = mlc.getFloat(new String[]{"accuracy", "ac", "a"}, 1.0F);
      float defNoise = (1.0F - this.projectileVelocityAccuracy) * 45.0F;
      this.projectileVelocityVertNoise = mlc.getFloat(new String[]{"verticalnoise", "vn"}, defNoise) / 10.0F;
      this.projectileVelocityHorizNoise = mlc.getFloat(new String[]{"horizontalnoise", "hn"}, defNoise);
      this.projectileVelocityVertNoiseBase = 0.0F - this.projectileVelocityVertNoise / 2.0F;
      this.projectileVelocityHorizNoiseBase = 0.0F - this.projectileVelocityHorizNoise / 2.0F;
      this.thrownItem = mlc.getPlaceholderString(new String[]{"item"}, "");
      String toUpperCase = strProjectile.toUpperCase();
      switch (toUpperCase) {
         case "ARROW":
            this.projectileClass = Arrow.class;
            break;
         case "SNOWBALL":
            this.projectileClass = Snowball.class;
            break;
         case "EGG":
            this.projectileClass = Egg.class;
            break;
         case "ENDERPEARL":
            this.projectileClass = EnderPearl.class;
            break;
         case "POTION":
         case "SPLASH_POTION":
         case "LINGERING_POTION":
            if (strProjectile.toUpperCase().equals("LINGERING_POTION")) {
               this.projectileType = ShootMechanic.ProjectileType.LINGERING_POTION;
            } else {
               this.projectileType = ShootMechanic.ProjectileType.POTION;
            }

            this.projectileClass = ThrownPotion.class;
            this.potionEffectType = mlc.getPlaceholderString(new String[]{"potiontype", "ptype", "effect", "pt", "pe"}, "SLOW");
            this.potionDuration = mlc.getPlaceholderDuration(new String[]{"potionduration", "pduration", "pd"}, DurationUnit.TICKS, "100");
            this.potionAmplifier = mlc.getPlaceholderInteger(new String[]{"potionlevel", "plevel", "lvl", "pl"}, 1);
            this.overwrite = mlc.getBoolean(new String[]{"overwrite", "ow", "override", "or", "force"}, false);
            this.potionColor = mlc.getString(new String[]{"potioncolor", "pc"}, "#FFFFFF");
            this.ambientParticles = mlc.getBoolean(new String[]{"ambientparticles", "ambient"}, false);
            this.hasParticles = mlc.getBoolean(new String[]{"hasparticles", "particles"}, true);
            this.hasIcon = mlc.getBoolean(new String[]{"hasicon", "icon"}, true);
            break;
         case "ITEM":
            this.projectileType = ShootMechanic.ProjectileType.ITEM;
            this.projectileClass = Snowball.class;
            break;
         case "BLOCK":
         case "FALLING_BLOCK":
            this.projectileType = ShootMechanic.ProjectileType.BLOCK;
            this.projectileClass = Snowball.class;
            break;
         case "TRIDENT":
            this.projectileClass = Trident.class;
            this.tridentItem = mlc.getPlaceholderString(new String[]{"tridentitem", "titem", "ti"}, "");
            this.projectileType = ShootMechanic.ProjectileType.TRIDENT;
            break;
         case "WIND_CHARGE":
         case "WINDCHARGE":
            this.projectileClass = WindCharge.class;
            this.projectileType = ShootMechanic.ProjectileType.WIND_CHARGE;
            break;
         case "BREEZE_WIND_CHARGE":
         case "BREEZEWINDCHARGE":
            this.projectileClass = BreezeWindCharge.class;
            this.projectileType = ShootMechanic.ProjectileType.WIND_CHARGE;
            break;
         default:
            this.projectileClass = Arrow.class;
      }

      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot", "skill", "s", "meta", "m"});
      this.onHitSkillName = mlc.getString(new String[]{"onhitskill", "onhit", "oh"});
      this.onEndSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"});
      if (!this.fromOrigin
         && this.startForwardOffset.isStaticallyEqualTo(0.0F)
         && this.startSideOffset.isStaticallyEqualTo(0.0F)
         && this.startYOffset.isStaticallyEqualTo(0.0F)) {
         this.offsetFromCaster = false;
      } else {
         this.offsetFromCaster = true;
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getManager().getSkill(file, this, this.onTickSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Shoot Loaded onTick pointing at " + this.onTickSkillName);
         if (this.onHitSkillName != null) {
            this.onHitSkill = this.getManager().getSkill(file, this, this.onHitSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded onHit pointing at " + this.onHitSkillName);
         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getManager().getSkill(file, this, this.onEndSkillName);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded onEnd pointing at " + this.onEndSkillName);
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.shoot(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      return this.shoot(data, target);
   }

   public SkillResult shoot(SkillMetadata data, AbstractLocation target) {
      SkillCaster caster = data.getCaster();
      AbstractLocation origin;
      if (this.fromOrigin) {
         origin = data.getOrigin().clone();
      } else {
         origin = caster.getLocation();
      }

      float power = data.getPower();
      if (!this.onTickSkill.isPresent() && this.onTickSkillName != null) {
         this.onTickSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onTickSkillName);
      }

      if (!this.onHitSkill.isPresent() && this.onHitSkillName != null) {
         this.onHitSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onHitSkillName);
      }

      if (!this.onEndSkill.isPresent() && this.onEndSkillName != null) {
         this.onEndSkill = this.getPlugin().getSkillManager().getSkill(this.file, this, this.onEndSkillName);
      }

      float v = (float)this.projectileVelocity.get(data) * 3.0F;
      if (this.powerAffectsVelocity) {
         v *= power;
      }

      float startYOffset = this.startYOffset.get(data);
      if (startYOffset > 0.0F) {
         origin = origin.add(0.0, startYOffset, 0.0);
      }

      float startForwardOffset = this.startForwardOffset.get(data);
      float startSideOffset = this.startSideOffset.get(data);
      if (startForwardOffset != 0.0F || startSideOffset != 0.0F) {
         origin = MythicUtil.move(this.faulty, origin, -startForwardOffset, 0.0, startSideOffset);
      }

      AbstractVector vector;
      if (this.fromOrigin) {
         vector = target.toVector().rotate(0.001F).subtract(origin.toVector()).normalize();
      } else if (caster.getEntity().isPlayer() && this.adjustVelocity) {
         float yaw = caster.getEntity().getEyeLocation().getYaw();
         float pitch = caster.getEntity().getEyeLocation().getPitch();
         double xVel = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
         double yVel = -Math.sin(Math.toRadians(pitch));
         double zVel = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
         vector = new AbstractVector(xVel, yVel, zVel).normalize();
      } else {
         vector = target.toVector().rotate(0.001F).subtract(caster.getEntity().getLocation().toVector()).normalize();
      }

      if (this.projectileVelocityHorizOffset.get(data) != 0.0F || this.projectileVelocityHorizNoise > 0.0F) {
         float noise = 0.0F;
         if (this.projectileVelocityHorizNoise > 0.0F) {
            noise = (float)(this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * this.projectileVelocityHorizNoise);
         }

         vector.rotate(this.projectileVelocityHorizOffset.get(data) + noise);
      }

      if (this.projectileVelocityVertOffset.get(data) != 0.0F || this.projectileVelocityVertNoise > 0.0F) {
         float noise = 0.0F;
         if (this.projectileVelocityVertNoise > 0.0F) {
            noise = (float)(this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * this.projectileVelocityVertNoise);
         }

         vector.add(new AbstractVector(0.0F, this.projectileVelocityVertOffset.get(data) + noise, 0.0F)).normalize();
      }

      vector = vector.multiply(v);
      AbstractEntity projectile;
      if (this.projectileType == ShootMechanic.ProjectileType.ITEM
         || this.projectileType == ShootMechanic.ProjectileType.BLOCK
         || this.projectileType == ShootMechanic.ProjectileType.POTION
         || this.projectileType == ShootMechanic.ProjectileType.LINGERING_POTION
         || this.projectileType == ShootMechanic.ProjectileType.TRIDENT
         || this.projectileType == ShootMechanic.ProjectileType.WIND_CHARGE) {
         projectile = SkillAdapter.get().shootProjectile(caster, target, origin, this.projectileClass, vector, this);
      } else if (this.calculateFiringAngle && this.gravity) {
         projectile = SkillAdapter.get().shootArcProjectile(caster, target, origin, this.projectileClass, v, this.fromOrigin);
      } else {
         projectile = SkillAdapter.get().shootProjectile(caster, target, origin, this.projectileClass, vector, this);
      }

      if (!this.gravity) {
         projectile.setGravity(false);
      }

      if (!this.canPickup && projectile.getBukkitEntity() instanceof Arrow arrow) {
         arrow.setPickupStatus(PickupStatus.DISALLOWED);
      }

      if (this.disguise.isPresent()) {
         this.getPlugin().getCompatibility();
         if (CompatibilityManager.LibsDisguises.enabled) {
            Entity entity = BukkitAdapter.adapt(projectile);
            this.getPlugin().getCompatibility();
            DisguiseAPI.disguiseEntity(entity, CompatibilityManager.LibsDisguises.getParsedDisguise(this.disguise.get()));
         }
      }

      projectile.getDataContainer().set(MobKeys.IS_PROJECTILE, PersistentDataType.BYTE, (byte)1);
      new ShootMechanic.ProjectileTracker(data, projectile, power);
      return SkillResult.SUCCESS;
   }

   protected class ProjectileTracker implements Runnable, Terminable {
      private final TerminableRegistry registry = TerminableRegistry.create();
      private SkillMetadata data;
      private SkillCaster caster;
      private AbstractEntity projectile;
      private boolean hasEnded = false;
      private int pierce;
      private final int tickInterval;
      private final int expirationTime;
      private float power;

      public ProjectileTracker(SkillMetadata caster, AbstractEntity projectile, float power) {
         this.caster = caster.getCaster();
         this.data = caster.deepClone();
         this.data.setIsAsync(true);
         this.projectile = projectile;
         this.pierce = ShootMechanic.this.pierceLevel;
         this.tickInterval = Math.max(1, ShootMechanic.this.tickInterval.get(caster).getTicks());
         this.expirationTime = ShootMechanic.this.expirationTime.get(caster).getTicks();
         this.power = power;
         this.registry
            .accept(
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(event -> event.getDamager() instanceof Projectile)
                  .filter(event -> event.getEntity() instanceof LivingEntity)
                  .filter(event -> event.getDamager().getUniqueId().equals(projectile.getUniqueId()))
                  .handler(this::doHitSkill)
            );
         this.registry.accept(Schedulers.async().runRepeating(this, 0L, (long)this.tickInterval));
      }

      @Override
      public void run() {
         if (!this.projectile.isValid() || this.projectile.getBukkitEntity() instanceof Projectile && this.projectile.getBukkitEntity().isOnGround()) {
            this.doEndSkill();
            this.close();
         }

         if (ShootMechanic.this.onTickSkill.isPresent() && ShootMechanic.this.onTickSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            HashSet<AbstractLocation> targets = new HashSet<>();
            targets.add(this.projectile.getLocation().clone());
            sData.setLocationTargets(targets);
            sData.setOrigin(this.projectile.getLocation().clone());
            sData.setPower(this.power);
            ShootMechanic.this.onTickSkill.get().execute(sData);
         }

         if (this.projectile.getLocation().distanceGreaterOrEq(this.data.getOrigin(), ShootMechanic.this.maxdistance.get(this.data))) {
            this.doEndSkill();
            Schedulers.of(this.projectile.getBukkitEntity()).run(() -> {
               this.projectile.remove();
               this.registry.terminate();
            });
         }
      }

      public void doHitSkill(EntityDamageByEntityEvent event) {
         if (this.hasEnded) {
            event.setCancelled(true);
         } else {
            Projectile projectile = (Projectile)event.getDamager();
            AbstractEntity proj = BukkitAdapter.adapt(projectile);
            AbstractEntity target = BukkitAdapter.adapt(event.getEntity());
            HashSet<AbstractEntity> t = Sets.newHashSet();
            t.add(target);
            SkillMetadata meta = new SkillMetadataImpl(SkillTriggers.ATTACK, this.caster, target, proj.getLocation(), t, null, this.power);
            meta.getVariables().putAll(this.data.getVariables().asMap());
            event.setCancelled(true);
            if (ShootMechanic.this.damage.get(this.data, target) != 0.0) {
               ShootMechanic.this.doDamage(this.data, target, ShootMechanic.this.damage.get(this.data, target));
            }

            if (ShootMechanic.this.onHitSkill.isPresent() && ShootMechanic.this.onHitSkill.get().isUsable(meta)) {
               ShootMechanic.this.onHitSkill.get().execute(meta);
            }

            if (this.pierce-- <= 0) {
               this.close();
               if (!ShootMechanic.this.bounce) {
                  Schedulers.of(projectile).run(projectile::remove);
               }
            }
         }
      }

      private void doEndSkill() {
         if (!this.hasEnded) {
            this.hasEnded = true;
            if (ShootMechanic.this.onEndSkill.isPresent() && ShootMechanic.this.onEndSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               Collection<AbstractLocation> targets = Lists.newArrayList();
               targets.add(this.projectile.getLocation().clone());
               sData.setLocationTargets(targets);
               sData.setOrigin(this.projectile.getLocation().clone());
               sData.setPower(this.power);
               ShootMechanic.this.onEndSkill.get().execute(sData);
            }
         }
      }

      @Override
      public void close() {
         this.registry.terminate();
         if (this.expirationTime > 0) {
            Schedulers.of(this.projectile.getBukkitEntity()).runLater(() -> this.projectile.remove(), this.expirationTime);
         }
      }
   }

   public static enum ProjectileType {
      REGULAR,
      ITEM,
      BLOCK,
      POTION,
      LINGERING_POTION,
      TRIDENT,
      WIND_CHARGE;
   }
}
