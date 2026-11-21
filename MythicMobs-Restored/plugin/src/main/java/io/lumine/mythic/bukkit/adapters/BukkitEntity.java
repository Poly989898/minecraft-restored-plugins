package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.volatilecode.VolatileCodeDisabled;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.VolatileEnchantment;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.compatibility.CitizensSupport;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.adventure.PaperHelper;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BukkitEntity implements AbstractEntity {
   private final Entity entityRef;
   private UUID cachedUuid;
   private static final String STAT_MODIFIER_PREFIX = "mythic_stat:";

   public BukkitEntity(Entity e) {
      this.entityRef = e;
   }

   @Override
   public void setCustomName(String name) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.setCustomName(name));
      } else {
         VolatileCodeHandler handler = MythicBukkit.inst().getVolatileCodeHandler();
         if (handler instanceof VolatileCodeDisabled disabled) {
            if (ServerVersion.isPaper()) {
               this.getBukkitEntity().customName(PaperHelper.parse(name));
            } else {
               this.getBukkitEntity().setCustomName(name);
            }
         } else {
            handler.getEntityHandler().setCustomName(this, name);
         }
      }
   }

   @Override
   public void setAlwaysShowName(boolean alwaysShowName) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setCustomNameVisible(alwaysShowName));
      } else {
         this.getBukkitEntity().setCustomNameVisible(alwaysShowName);
      }
   }

   @Override
   public Entity getBukkitEntity() {
      return this.entityRef;
   }

   private LivingEntity getEntityAsLiving() {
      return (LivingEntity)this.getBukkitEntity();
   }

   private Mob getEntityAsMob() {
      return (Mob)this.getBukkitEntity();
   }

   private Creature getEntityAsCreature() {
      return (Creature)this.getBukkitEntity();
   }

   public Player getEntityAsPlayer() {
      return (Player)this.getBukkitEntity();
   }

   @Override
   public PersistentDataContainer getDataContainer() {
      return this.getBukkitEntity().getPersistentDataContainer();
   }

   @Override
   public AbstractLocation getLocation() {
      return BukkitAdapter.adapt(this.getBukkitEntity().getLocation());
   }

   @Override
   public AbstractWorld getWorld() {
      return BukkitAdapter.adapt(this.getBukkitEntity().getWorld());
   }

   @Override
   public boolean isLiving() {
      return this.getBukkitEntity() instanceof LivingEntity;
   }

   @Override
   public boolean isMob() {
      return this.getBukkitEntity() instanceof Mob;
   }

   @Override
   public boolean isCreature() {
      return this.getBukkitEntity() instanceof Creature;
   }

   @Override
   public boolean isMonster() {
      return this.getBukkitEntity() instanceof Monster;
   }

   @Override
   public boolean isDamageable() {
      return this.getBukkitEntity() instanceof Damageable;
   }

   @Override
   public boolean isPlayer() {
      return this.getBukkitEntity() instanceof Player;
   }

   @Override
   public UUID getUniqueId() {
      UUID u = this.cachedUuid;
      if (u == null) {
         u = this.entityRef.getUniqueId();
         this.cachedUuid = u;
      }

      return u;
   }

   @Override
   public boolean hasLineOfSight(AbstractEntity e) {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return ((LivingEntity)this.getBukkitEntity()).hasLineOfSight(e.getBukkitEntity());
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> ((LivingEntity)this.getBukkitEntity()).hasLineOfSight(e.getBukkitEntity())).join();
         } catch (Exception ex) {
            return false;
         }
      }
   }

   @Override
   public boolean hasLineOfSight(AbstractLocation location) {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return !((LivingEntity)this.getBukkitEntity()).hasLineOfSight(BukkitAdapter.adapt(location));
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> !((LivingEntity)this.getBukkitEntity()).hasLineOfSight(BukkitAdapter.adapt(location))).join();
         } catch (Exception e) {
            return false;
         }
      }
   }

   @Override
   public void teleport(AbstractLocation l) {
      if (this.getBukkitEntity().isValid() && !this.getBukkitEntity().isDead() && l.isFinite() && l.getWorld() != null && l.getWorld().isLoaded()) {
         if (ServerVersion.isFolia()) {
            this.getBukkitEntity().teleportAsync(BukkitAdapter.adapt(l));
         } else {
            this.getBukkitEntity().teleport(BukkitAdapter.adapt(l));
         }
      }
   }

   @Override
   public void teleport(AbstractLocation aLocation, AbstractTeleportData data) {
      if (this.getBukkitEntity().isValid()
         && !this.getBukkitEntity().isDead()
         && aLocation.isFinite()
         && aLocation.getWorld() != null
         && aLocation.getWorld().isLoaded()) {
         Location location = BukkitAdapter.adapt(aLocation);
         if (data instanceof BukkitTeleportData bukkitTeleportData) {
            bukkitTeleportData.teleport(this.getBukkitEntity(), location);
         }
      }
   }

   @Override
   public void setRotation(float yaw, float pitch) {
      MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().setEntityRotation(this, pitch, yaw);
   }

   @Override
   public void lookAt(AbstractLocation location) {
      if (ServerVersion.isPaper() && this.getBukkitEntity() instanceof Mob mob) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> mob.lookAt(location.getX(), location.getY(), location.getZ()));
            return;
         }

         mob.lookAt(location.getX(), location.getY(), location.getZ());
      }
   }

   @Override
   public void lookAt(AbstractEntity entity) {
      if (ServerVersion.isPaper() && this.getBukkitEntity() instanceof Mob mob) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> mob.lookAt(entity.getBukkitEntity()));
            return;
         }

         mob.lookAt(entity.getBukkitEntity());
      }
   }

   @Override
   public void setRemoveWhenFarAway(boolean bool) {
      if (this.getBukkitEntity() instanceof LivingEntity living) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> living.setRemoveWhenFarAway(bool));
            return;
         }

         living.setRemoveWhenFarAway(bool);
      }
   }

   @Override
   public void setPersistent(boolean bool) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setPersistent(bool));
      } else {
         this.getBukkitEntity().setPersistent(bool);
      }
   }

   @Override
   public void setSaveToDisk(boolean bool) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setPersistent(bool));
      } else {
         this.getBukkitEntity().setPersistent(bool);
      }
   }

   @Override
   public double getHealth() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getHealth();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.getEntityAsLiving().getHealth()).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public double getAbsorption() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAbsorptionAmount();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.getEntityAsLiving().getAbsorptionAmount()).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public double getMaxHealth() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("MAX_HEALTH")).getValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef)
                  .supply(() -> this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("MAX_HEALTH")).getValue())
                  .join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public void setMaxHealth(double health) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> {
               try {
                  this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("MAX_HEALTH")).setBaseValue(health);
               } catch (IllegalArgumentException var4x) {
                  MythicLogger.error("Tried to set MAX_HEALTH for {0} greater than server's maxHealth setting.", this.getUniqueId());
               }
            });
         } else {
            try {
               this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("MAX_HEALTH")).setBaseValue(health);
            } catch (IllegalArgumentException e) {
               MythicLogger.error(
                  "Tried to set MAX_HEALTH for {0} greater than server's maxHealth setting. Please modify spigot.yml and increase the maxHealth attribute to compensate.",
                  this.getUniqueId()
               );
               MythicLogger.handleMinorError(e);
            }
         }
      }
   }

   @Override
   public void setHealth(double value) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setHealth(value));
            return;
         }

         LivingEntity le = this.getEntityAsLiving();
         if (value < 0.0) {
            le.setHealth(0.0);
         } else if (value > this.getMaxHealth()) {
            le.setHealth(this.getMaxHealth());
         } else {
            le.setHealth(value);
         }
      }
   }

   @Override
   public void setHealthAndMax(double value) {
      this.setMaxHealth(value);
      this.setHealth(value);
   }

   @Override
   public double getDamage() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")).getValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE"));
                  return a != null ? a.getValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public double getDamageBase() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")).getBaseValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE"));
                  return a != null ? a.getBaseValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public void setDamage(double value) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setDamage(value));
         } else {
            if (this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")) == null) {
               try {
                  MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, BukkitAttribute.getAttribute("ATTACK_DAMAGE"));
               } catch (Exception e) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", BukkitAttribute.getAttribute("ATTACK_DAMAGE"));
                  return;
               }
            }

            this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")).setBaseValue(value);
            if (CompatibilityManager.Heroes != null) {
               CompatibilityManager.Heroes.setMobDamage(this.getEntityAsLiving(), value);
            }
         }
      }
   }

   @Override
   public double getArmor() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")).getValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR"));
                  return a != null ? a.getValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public double getArmorBase() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")).getBaseValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR"));
                  return a != null ? a.getBaseValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public void setArmor(double value) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setArmor(value));
         } else {
            if (this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")) == null) {
               try {
                  MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, BukkitAttribute.getAttribute("ARMOR"));
               } catch (Exception e) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", BukkitAttribute.getAttribute("ARMOR"));
                  return;
               }
            }

            this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR")).setBaseValue(value);
         }
      }
   }

   @Override
   public double getArmorToughness() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")).getValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS"));
                  return a != null ? a.getValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public double getArmorToughnessBase() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")) == null
               ? 0.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")).getBaseValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 0.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS"));
                  return a != null ? a.getBaseValue() : 0.0;
               }).join();
            } catch (Exception e) {
               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   @Override
   public void setArmorToughness(double value) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setArmorToughness(value));
         } else {
            if (this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")) == null) {
               try {
                  MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, BukkitAttribute.getAttribute("ARMOR_TOUGHNESS"));
               } catch (Exception e) {
                  MythicLogger.debug(
                     MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")
                  );
                  return;
               }
            }

            this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("ARMOR_TOUGHNESS")).setBaseValue(value);
         }
      }
   }

   @Override
   public double getModelScaleBase() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE")) == null
               ? 1.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE")).getBaseValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 1.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE"));
                  return a != null ? a.getBaseValue() : 1.0;
               }).join();
            } catch (Exception e) {
               return 1.0;
            }
         }
      } else {
         return 1.0;
      }
   }

   @Override
   public double getModelScale() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            return this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE")) == null
               ? 1.0
               : this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE")).getValue();
         } else if (Bukkit.isPrimaryThread()) {
            return 1.0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  AttributeInstance a = this.getEntityAsLiving().getAttribute(BukkitAttribute.getAttribute("SCALE"));
                  return a != null ? a.getValue() : 1.0;
               }).join();
            } catch (Exception e) {
               return 1.0;
            }
         }
      } else {
         return 1.0;
      }
   }

   @Override
   public void setModelScale(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.SCALE, newValue);
   }

   @Override
   public void setStepHeight(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.STEP_HEIGHT, newValue);
   }

   @Override
   public void setBurningTime(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.BURNING_TIME, newValue);
   }

   @Override
   public void setExplosionKnockbackResistance(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.EXPLOSION_KNOCKBACK_RESISTANCE, newValue);
   }

   @Override
   public void setFallDamageMultiplier(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.FALL_DAMAGE_MULTIPLIER, newValue);
   }

   @Override
   public void setGravityAttribute(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.GRAVITY, newValue);
   }

   @Override
   public void setJumpStrength(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.JUMP_HEIGHT, newValue);
   }

   @Override
   public void setKnockbackResistance(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.KNOCKBACK_RESISTANCE, newValue);
   }

   @Override
   public void setMovementEfficiency(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.MOVEMENT_EFFICIENCY, newValue);
   }

   @Override
   public void setOxygenBonus(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.OXYGEN, newValue);
   }

   @Override
   public void setSafeFallDistance(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.SAFE_FALL_DISTANCE, newValue);
   }

   @Override
   public void setSneakingSpeed(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.SNEAKING_SPEED, newValue);
   }

   @Override
   public void setWaterMovementEfficiency(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.WATER_MOVEMENT_EFFICIENCY, newValue);
   }

   @Override
   public void setBlockInteractionRange(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.BLOCK_INTERACTION_RANGE, newValue);
   }

   @Override
   public void setEntityInteractionRange(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.ENTITY_INTERACTION_RANGE, newValue);
   }

   @Override
   public void setBlockBreakSpeed(double newValue) {
      this.setAttributeBaseValue(BukkitAttribute.BLOCK_BREAK_SPEED, newValue);
   }

   private void setAttributeBaseValue(BukkitAttribute attribute, double newValue) {
      this.setAttributeBaseValue(attribute.getBukkitAttribute(), newValue);
   }

   @Override
   public void setAttributeBaseValue(Attribute attribute, double newValue) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setAttributeBaseValue(attribute, newValue));
         } else {
            if (this.getEntityAsLiving().getAttribute(attribute) == null) {
               try {
                  MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, attribute);
               } catch (Exception e) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", ((org.bukkit.Keyed)attribute).getKey().toString());
                  return;
               }
            }

            this.getEntityAsLiving().getAttribute(attribute).setBaseValue(newValue);
         }
      }
   }

   private static UUID statModifierUuid(String attributeName, String statKey) {
      return UUID.nameUUIDFromBytes(("mythic_stat:" + attributeName + ":" + statKey).getBytes(StandardCharsets.UTF_8));
   }

   @Override
   public void setStatAttributeModifier(String attributeKey, String statKey, double value) {
      if (this.isLiving()) {
         BukkitAttribute bukkitAttribute = BukkitAttribute.get(attributeKey);
         if (bukkitAttribute != null) {
            if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
               Schedulers.of(this.entityRef).run(() -> this.setStatAttributeModifier(attributeKey, statKey, value));
            } else {
               AttributeInstance attribute = this.getEntityAsLiving().getAttribute(bukkitAttribute.getBukkitAttribute());
               if (attribute == null) {
                  try {
                     MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, bukkitAttribute.getBukkitAttribute());
                     attribute = this.getEntityAsLiving().getAttribute(bukkitAttribute.getBukkitAttribute());
                  } catch (Exception e) {
                     MythicLogger.debug(
                        MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", bukkitAttribute.getAttributeKey().toString()
                     );
                     return;
                  }

                  if (attribute == null) {
                     return;
                  }
               }

               UUID uuid = statModifierUuid(attributeKey, statKey);
               AttributeModifier existing = attribute.getModifier(uuid);
               if (existing != null) {
                  attribute.removeModifier(existing);
               }

               if (value != 0.0) {
                  try {
                     attribute.addModifier(new AttributeModifier(uuid, "mythic_stat:" + statKey, value, Operation.ADD_NUMBER));
                  } catch (IllegalArgumentException ex) {
                     MythicLogger.handleMinorError(ex);
                  }
               }
            }
         }
      }
   }

   @Override
   public void removeStatAttributeModifier(String attributeKey, String statKey) {
      if (this.isLiving()) {
         BukkitAttribute bukkitAttribute = BukkitAttribute.get(attributeKey);
         if (bukkitAttribute != null) {
            if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
               Schedulers.of(this.entityRef).run(() -> this.removeStatAttributeModifier(attributeKey, statKey));
            } else {
               AttributeInstance attribute = this.getEntityAsLiving().getAttribute(bukkitAttribute.getBukkitAttribute());
               if (attribute != null) {
                  AttributeModifier existing = attribute.getModifier(statModifierUuid(attributeKey, statKey));
                  if (existing != null) {
                     attribute.removeModifier(existing);
                  }
               }
            }
         }
      }
   }

   @Override
   public void clearAllStatAttributeModifiers() {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(this::clearAllStatAttributeModifiers);
         } else {
            LivingEntity living = this.getEntityAsLiving();

            for (BukkitAttribute bukkitAttribute : BukkitAttribute.values()) {
               Attribute bukkit = bukkitAttribute.getBukkitAttribute();
               if (bukkit != null) {
                  AttributeInstance attribute = living.getAttribute(bukkit);
                  if (attribute != null) {
                     for (AttributeModifier modifier : List.copyOf(attribute.getModifiers())) {
                        String name = modifier.getName();
                        if (name != null && name.startsWith("mythic_stat:")) {
                           attribute.removeModifier(modifier);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean isDead() {
      return this.getBukkitEntity().isDead();
   }

   @Override
   public boolean isValid() {
      return this.getBukkitEntity().isValid();
   }

   @Override
   public boolean remove() {
      Entity entity = this.entityRef;
      if (entity == null || this.isPlayer()) {
         return true;
      } else if (ServerVersion.isFolia()) {
         if (FoliaHelper.isOwnedByCurrentRegion(entity)) {
            entity.remove();
            return entity.isDead();
         } else {
            Schedulers.of(entity).run(() -> entity.remove());
            return true;
         }
      } else if (Bukkit.isPrimaryThread()) {
         entity.remove();
         return entity.isDead();
      } else {
         Schedulers.of(entity).run(() -> entity.remove());
         return true;
      }
   }

   @Override
   public void setFireTicks(int ticks) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setFireTicks(ticks));
      } else {
         this.getBukkitEntity().setFireTicks(ticks);
      }
   }

   public String getCustomName() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getEntityAsLiving().getCustomName();
      } else if (Bukkit.isPrimaryThread()) {
         return null;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getEntityAsLiving().getCustomName()).join();
         } catch (Exception e) {
            return null;
         }
      }
   }

   @Deprecated
   @Override
   public void setPassenger(Entity entity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setPassenger(entity));
      } else {
         this.getBukkitEntity().setPassenger(entity);
      }
   }

   @Override
   public void addPassenger(AbstractEntity entity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().addPassenger(BukkitAdapter.adapt(entity)));
      } else {
         this.getBukkitEntity().addPassenger(BukkitAdapter.adapt(entity));
      }
   }

   @Override
   public Collection<AbstractEntity> getPassengers() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getPassengers().stream().map(entity -> BukkitAdapter.adapt(entity)).toList();
      } else if (Bukkit.isPrimaryThread()) {
         return List.of();
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getPassengers().stream().map(e -> BukkitAdapter.adapt(e)).toList()).join();
         } catch (Exception ex) {
            return List.of();
         }
      }
   }

   @Override
   public void removePassenger(Entity entity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().removePassenger(entity));
      } else {
         this.getBukkitEntity().removePassenger(entity);
      }
   }

   @Override
   public boolean hasGravity() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().hasGravity();
      } else if (Bukkit.isPrimaryThread()) {
         return true;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().hasGravity()).join();
         } catch (Exception e) {
            return true;
         }
      }
   }

   @Override
   public void setGravity(boolean bool) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setGravity(bool));
      } else {
         this.getBukkitEntity().setGravity(bool);
      }
   }

   @Override
   public boolean hasAI() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.isLiving() && this.getEntityAsLiving().hasAI()).join();
            } catch (Exception e) {
               return false;
            }
         }
      } else {
         return this.isLiving() ? this.getEntityAsLiving().hasAI() : false;
      }
   }

   @Override
   public void setAI(boolean bool) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> {
            if (this.isLiving()) {
               this.getEntityAsLiving().setAI(bool);
            }
         });
      } else {
         if (this.isLiving()) {
            this.getEntityAsLiving().setAI(bool);
         }
      }
   }

   @Override
   public boolean isAware() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.isMob() && this.getEntityAsMob().isAware()).join();
            } catch (Exception e) {
               return false;
            }
         }
      } else {
         return this.isMob() ? this.getEntityAsMob().isAware() : false;
      }
   }

   @Override
   public void setAware(boolean bool) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> {
            if (this.isMob()) {
               this.getEntityAsMob().setAware(bool);
            }
         });
      } else {
         if (this.isMob()) {
            this.getEntityAsMob().setAware(bool);
         }
      }
   }

   @Override
   public AbstractLocation getEyeLocation() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return BukkitAdapter.adapt(this.getBukkitEntity().getLocation());
         } else {
            try {
               return Schedulers.of(this.entityRef)
                  .supply(
                     () -> this.isLiving()
                        ? BukkitAdapter.adapt(this.getEntityAsLiving().getEyeLocation())
                        : BukkitAdapter.adapt(this.getBukkitEntity().getLocation())
                  )
                  .join();
            } catch (Exception e) {
               return BukkitAdapter.adapt(this.getBukkitEntity().getLocation());
            }
         }
      } else {
         return this.isLiving() ? BukkitAdapter.adapt(this.getEntityAsLiving().getEyeLocation()) : BukkitAdapter.adapt(this.getBukkitEntity().getLocation());
      }
   }

   @Override
   public double getEyeHeight() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return 1.62;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.isLiving() ? this.getEntityAsLiving().getEyeHeight() : 0.0).join();
            } catch (Exception e) {
               return 1.62;
            }
         }
      } else {
         return this.isLiving() ? this.getEntityAsLiving().getEyeHeight() : 0.0;
      }
   }

   @Override
   public double getHeight() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getHeight();
      } else if (Bukkit.isPrimaryThread()) {
         return 1.8;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getHeight()).join();
         } catch (Exception e) {
            return 1.8;
         }
      }
   }

   @Deprecated
   @Override
   public double getPassengerRidingOffset() {
      return MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getPassengerRidingOffset(this);
   }

   @Override
   public float getPassengerRidingOffset(AbstractEntity passenger) {
      return MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getPassengerRidingOffset(this, passenger);
   }

   @Override
   public double getNameplateHeight() {
      return this.getHeight() + 0.25;
   }

   @Override
   public double getNameplateMountOffset() {
      return this.getNameplateHeight() + this.getPassengerRidingOffset();
   }

   @Override
   public AbstractEntity getTarget() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return null;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.isCreature() ? BukkitAdapter.adapt(this.getEntityAsCreature().getTarget()) : null).join();
            } catch (Exception e) {
               return null;
            }
         }
      } else if (this.isCreature()) {
         return BukkitAdapter.adapt(this.getEntityAsCreature().getTarget());
      } else {
         return this.getBukkitEntity().getLastDamageCause() != null ? BukkitAdapter.adapt(this.getBukkitEntity().getLastDamageCause().getEntity()) : null;
      }
   }

   @Override
   public void setTarget(AbstractEntity target) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> {
            if (this.entityRef instanceof Mob mobx) {
               if (target == null) {
                  mobx.setTarget(null);
               } else if (target.getBukkitEntity() instanceof LivingEntity lt) {
                  mobx.setTarget(lt);
               }
            }
         });
      } else {
         if (this.entityRef instanceof Mob mob) {
            if (target == null) {
               mob.setTarget(null);
            } else if (target.getBukkitEntity() instanceof LivingEntity livingTarget) {
               mob.setTarget(livingTarget);
            }
         }
      }
   }

   @Override
   public AbstractEntity getPassenger() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Entity e = this.getBukkitEntity().getPassenger();
         return e != null ? BukkitAdapter.adapt(e) : null;
      } else if (Bukkit.isPrimaryThread()) {
         return null;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> {
               Entity ex = this.getBukkitEntity().getPassenger();
               return ex != null ? BukkitAdapter.adapt(ex) : null;
            }).join();
         } catch (Exception ex2) {
            return null;
         }
      }
   }

   @Override
   public AbstractEntity getVehicle() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Entity e = this.getBukkitEntity().getVehicle();
         return e != null ? BukkitAdapter.adapt(e) : null;
      } else if (Bukkit.isPrimaryThread()) {
         return null;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> {
               Entity ex = this.getBukkitEntity().getVehicle();
               return ex != null ? BukkitAdapter.adapt(ex) : null;
            }).join();
         } catch (Exception ex2) {
            return null;
         }
      }
   }

   @Override
   public boolean leaveVehicle() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().leaveVehicle());
         return true;
      } else {
         return this.getBukkitEntity().leaveVehicle();
      }
   }

   @Override
   public void eject() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().eject());
      } else {
         this.getBukkitEntity().eject();
      }
   }

   @Override
   public String getName() {
      if (this.isPlayer()) {
         return this.getEntityAsPlayer().getName();
      } else if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getType().name();
      } else if (this.getBukkitEntity().getCustomName() != null) {
         return this.getBukkitEntity().getCustomName();
      } else {
         return this.getBukkitEntity().getName() != null ? this.getBukkitEntity().getName() : this.getBukkitEntity().getType().getName();
      }
   }

   @Override
   public Optional<String> getFaction() {
      if (this.isPlayer()) {
         return MythicBukkit.inst().getPlayerManager().getFactionProvider().getFaction(this.asPlayer());
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(this);
         return am == null ? Optional.empty() : Optional.ofNullable(am.getFaction());
      }
   }

   @Override
   public boolean damage(float damage) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getEntityAsLiving().damage(damage));
         return true;
      } else {
         this.getEntityAsLiving().damage(damage);
         return true;
      }
   }

   @Override
   public boolean damage(float damage, AbstractEntity source) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getEntityAsLiving().damage(damage, source.getBukkitEntity()));
         return true;
      } else {
         this.getEntityAsLiving().damage(damage, source.getBukkitEntity());
         return true;
      }
   }

   @Override
   public void setPassenger(AbstractEntity entity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setPassenger(BukkitAdapter.adapt(entity)));
      } else {
         this.getBukkitEntity().setPassenger(BukkitAdapter.adapt(entity));
      }
   }

   @Override
   public AbstractPlayer asPlayer() {
      return this.getBukkitEntity() instanceof Player ? new BukkitPlayer(this.getEntityAsPlayer()) : null;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof BukkitEntity ? this.getUniqueId().equals(((BukkitEntity)o).getUniqueId()) : super.equals(o);
   }

   @Override
   public int hashCode() {
      return this.getUniqueId().hashCode();
   }

   @Override
   public void setNoDamageTicks(int ticks) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> {
            if (this.isLiving()) {
               ((LivingEntity)this.getBukkitEntity()).setNoDamageTicks(ticks);
            }
         });
      } else {
         if (this.isLiving()) {
            ((LivingEntity)this.getBukkitEntity()).setNoDamageTicks(ticks);
         }
      }
   }

   @Override
   public int getNoDamageTicks() {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         if (Bukkit.isPrimaryThread()) {
            return 0;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> this.isLiving() ? ((LivingEntity)this.getBukkitEntity()).getNoDamageTicks() : 0).join();
            } catch (Exception e) {
               return 0;
            }
         }
      } else {
         return this.isLiving() ? ((LivingEntity)this.getBukkitEntity()).getNoDamageTicks() : 0;
      }
   }

   @Override
   public boolean isCitizensNPC() {
      Optional<CitizensSupport> citizens = MythicBukkit.inst().getCompatibility().getCitizens();
      return citizens.isEmpty() ? false : citizens.get().isCitizensNPC(this);
   }

   @Override
   public boolean isAnimal() {
      return this.getBukkitEntity() instanceof Animals;
   }

   @Override
   public boolean isWaterMob() {
      return this.getBukkitEntity() instanceof WaterMob;
   }

   @Override
   public boolean isFlyingMob() {
      return this.getBukkitEntity() instanceof Flying;
   }

   @Override
   public boolean isGliding() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return ((LivingEntity)this.getBukkitEntity()).isGliding();
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> ((LivingEntity)this.getBukkitEntity()).isGliding()).join();
         } catch (Exception e) {
            return false;
         }
      }
   }

   @Override
   public void addPotionEffect(AbstractPotionEffect effect) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         PotionEffect bukkitEffect = ((BukkitPotionEffect)effect).toBukkitEffect();
         Schedulers.of(this.entityRef).run(() -> this.getEntityAsLiving().addPotionEffect(bukkitEffect));
      } else {
         PotionEffect bukkitEffect = ((BukkitPotionEffect)effect).toBukkitEffect();
         this.getEntityAsLiving().addPotionEffect(bukkitEffect);
      }
   }

   @Override
   public void removePotionEffect(String type) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         PotionEffectType bukkitEffectType = BukkitPotionEffect.getEffectType(type);
         Schedulers.of(this.entityRef).run(() -> this.getEntityAsLiving().removePotionEffect(bukkitEffectType));
      } else {
         PotionEffectType bukkitEffectType = BukkitPotionEffect.getEffectType(type);
         this.getEntityAsLiving().removePotionEffect(bukkitEffectType);
      }
   }

   @Override
   public void removePotionEffectType(AbstractPotionEffect effect) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         PotionEffectType bukkitEffectType = ((BukkitPotionEffect)effect).getEffect().getType();
         Schedulers.of(this.entityRef).run(() -> this.getEntityAsLiving().removePotionEffect(bukkitEffectType));
      } else {
         PotionEffectType bukkitEffectType = ((BukkitPotionEffect)effect).getEffect().getType();
         this.getEntityAsLiving().removePotionEffect(bukkitEffectType);
      }
   }

   @Override
   public boolean hasPotionEffect(String type) {
      return this.hasPotionEffect(type, null, null);
   }

   @Override
   public boolean hasPotionEffect(String type, RangedDouble level, RangedDouble duration) {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            LivingEntity e = (LivingEntity)this.getBukkitEntity();
            PotionEffectType potiontype = BukkitPotionEffect.getEffectType(type);
            if (!e.hasPotionEffect(potiontype)) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "HasPotionEffect failed has check");
               return false;
            } else {
               PotionEffect effect = e.getPotionEffect(potiontype);
               if (duration != null && !duration.equals(effect.getDuration())) {
                  MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "HasPotionEffect failed duration check");
                  return false;
               } else if (level != null && !level.equals(effect.getAmplifier())) {
                  MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "HasPotionEffect failed level check");
                  return false;
               } else {
                  return true;
               }
            }
         } else if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> {
                  LivingEntity le = (LivingEntity)this.getBukkitEntity();
                  PotionEffectType pt = BukkitPotionEffect.getEffectType(type);
                  if (!le.hasPotionEffect(pt)) {
                     return false;
                  } else {
                     PotionEffect pe = le.getPotionEffect(pt);
                     if (duration != null && !duration.equals(pe.getDuration())) {
                        return false;
                     } else {
                        return level != null && !level.equals(pe.getAmplifier()) ? false : true;
                     }
                  }
               }).join();
            } catch (Exception ex) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean hasPotionEffect() {
      if (this.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            LivingEntity e = (LivingEntity)this.getBukkitEntity();
            return e.getActivePotionEffects().size() > 0;
         } else if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.entityRef).supply(() -> ((LivingEntity)this.getBukkitEntity()).getActivePotionEffects().size() > 0).join();
            } catch (Exception ex) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean hasScoreboardTag(String tag) {
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef) ? false : this.getBukkitEntity().getScoreboardTags().contains(tag);
   }

   @Override
   public void addScoreboardTag(String tag) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().addScoreboardTag(tag));
      } else {
         this.getBukkitEntity().addScoreboardTag(tag);
      }
   }

   @Override
   public void removeScoreboardTag(String tag) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().removeScoreboardTag(tag));
      } else {
         this.getBukkitEntity().removeScoreboardTag(tag);
      }
   }

   private ItemStack getEquipmentItemSafe(EquipmentSlot slot) {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         EntityEquipment equip = this.getEntityAsLiving().getEquipment();
         return equip != null ? equip.getItem(slot) : null;
      } else if (Bukkit.isPrimaryThread()) {
         return null;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> {
               EntityEquipment equipx = this.getEntityAsLiving().getEquipment();
               return equipx != null ? equipx.getItem(slot) : null;
            }).join();
         } catch (Exception e) {
            return null;
         }
      }
   }

   private void setEquipmentItemSafe(EquipmentSlot slot, AbstractItemStack item) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> {
            EntityEquipment equipx = this.getEntityAsLiving().getEquipment();
            if (equipx != null) {
               equipx.setItem(slot, BukkitAdapter.adapt(item));
            }
         });
      } else {
         EntityEquipment equip = this.getEntityAsLiving().getEquipment();
         if (equip != null) {
            equip.setItem(slot, BukkitAdapter.adapt(item));
         }
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemHead() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.HEAD);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemHead(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.HEAD, item);
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemChest() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.CHEST);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemChest(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.CHEST, item);
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemLegs() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.LEGS);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemLegs(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.LEGS, item);
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemFeet() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.FEET);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemFeet(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.FEET, item);
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemMainHand() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else if (this.isPlayer()) {
         return Optional.of(BukkitAdapter.adapt(((Player)this.entityRef).getItemInHand()));
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.HAND);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemMainHand(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.HAND, item);
      }
   }

   @Override
   public Optional<AbstractItemStack> getItemOffhand() {
      if (!this.isLiving()) {
         return Optional.empty();
      } else {
         ItemStack item = this.getEquipmentItemSafe(EquipmentSlot.OFF_HAND);
         return item == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(item));
      }
   }

   @Override
   public void equipItemOffHand(AbstractItemStack item) {
      if (this.isLiving()) {
         this.setEquipmentItemSafe(EquipmentSlot.OFF_HAND, item);
      }
   }

   @Override
   public void setMovementSpeed(double speed) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setMovementSpeed(speed));
         } else {
            LivingEntity le = (LivingEntity)this.getBukkitEntity();
            le.getAttribute(BukkitAttribute.MOVEMENT_SPEED.getBukkitAttribute()).setBaseValue(speed);
         }
      }
   }

   @Override
   public void setFlyingSpeed(double speed) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setFlyingSpeed(speed));
         } else {
            LivingEntity le = (LivingEntity)this.getBukkitEntity();
            le.getAttribute(BukkitAttribute.FLYING_SPEED.getBukkitAttribute()).setBaseValue(speed);
         }
      }
   }

   @Override
   public void setFollowRange(double speed) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setFollowRange(speed));
         } else {
            LivingEntity le = (LivingEntity)this.getBukkitEntity();
            le.getAttribute(BukkitAttribute.FOLLOW_RANGE.getBukkitAttribute()).setBaseValue(speed);
         }
      }
   }

   @Override
   public void setAttackSpeed(double speed) {
      if (this.isLiving()) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
            Schedulers.of(this.entityRef).run(() -> this.setAttackSpeed(speed));
         } else {
            LivingEntity le = (LivingEntity)this.getBukkitEntity();
            AttributeInstance attribute = le.getAttribute(BukkitAttribute.ATTACK_SPEED.getBukkitAttribute());
            if (attribute == null) {
               try {
                  MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().registerAttribute(this, BukkitAttribute.getAttribute("ATTACK_SPEED"));
               } catch (Exception e) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Failed to register {0} attribute to entity", BukkitAttribute.getAttribute("ATTACK_SPEED"));
                  return;
               }
            }

            le.getAttribute(BukkitAttribute.ATTACK_SPEED.getBukkitAttribute()).setBaseValue(speed);
         }
      }
   }

   @Override
   public int getLuck() {
      if (!this.isLiving()) {
         return 0;
      } else if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getLuckInternal();
      } else if (Bukkit.isPrimaryThread()) {
         return 0;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getLuckInternal()).join();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   private int getLuckInternal() {
      LivingEntity le = (LivingEntity)this.getBukkitEntity();
      double luck = 0.0;
      if (le.getAttribute(BukkitAttribute.getAttribute("LUCK")) != null) {
         luck = le.getAttribute(BukkitAttribute.getAttribute("LUCK")).getValue();
      }

      if (le.getEquipment() != null) {
         luck += le.getEquipment().getItemInMainHand().getEnchantmentLevel(VolatileEnchantment.LUCK_OF_THE_SEA);
      }

      for (PotionEffect pe : le.getActivePotionEffects()) {
         if (pe.getType() == PotionEffectType.LUCK) {
            luck += pe.getAmplifier();
         } else if (pe.getType() == PotionEffectType.UNLUCK) {
            luck -= pe.getAmplifier();
         }
      }

      return (int)luck;
   }

   @Override
   public int getEnchantmentLevel(String enchantmentName) {
      if (!this.isLiving()) {
         return 0;
      } else {
         Enchantment enchant;
         try {
            enchant = Enchantment.getByName(enchantmentName);
         } catch (Exception e) {
            return 0;
         }

         return enchant == null ? 0 : this.getEnchantmentLevel(enchant);
      }
   }

   public int getEnchantmentLevel(Enchantment enchant) {
      if (!this.isLiving()) {
         return 0;
      } else if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getEnchantmentLevelInternal(enchant);
      } else if (Bukkit.isPrimaryThread()) {
         return 0;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getEnchantmentLevelInternal(enchant)).join();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   private int getEnchantmentLevelInternal(Enchantment enchant) {
      LivingEntity le = (LivingEntity)this.getBukkitEntity();
      int level = 0;
      if (le.getEquipment() != null) {
         level += le.getEquipment().getItemInMainHand().getEnchantmentLevel(enchant);
         level += le.getEquipment().getItemInOffHand().getEnchantmentLevel(enchant);

         for (ItemStack item : le.getEquipment().getArmorContents()) {
            if (item != null) {
               level += item.getEnchantmentLevel(enchant);
            }
         }
      }

      return level;
   }

   @Override
   public int getEnchantmentLevelHeld(String enchantmentName) {
      if (!this.isLiving()) {
         return 0;
      } else {
         Enchantment enchant;
         try {
            enchant = Enchantment.getByName(enchantmentName);
         } catch (Exception e) {
            return 0;
         }

         return enchant == null ? 0 : this.getEnchantmentLevelHeld(enchant);
      }
   }

   public int getEnchantmentLevelHeld(Enchantment enchant) {
      if (!this.isLiving()) {
         return 0;
      } else if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         LivingEntity le = (LivingEntity)this.getBukkitEntity();
         return le.getEquipment().getItemInMainHand().getEnchantmentLevel(enchant);
      } else if (Bukkit.isPrimaryThread()) {
         return 0;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> {
               LivingEntity lex = (LivingEntity)this.getBukkitEntity();
               return lex.getEquipment().getItemInMainHand().getEnchantmentLevel(enchant);
            }).join();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   @Override
   public void setMetadata(String key, Object value) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.setMetadata(key, value));
      } else {
         if (this.meg().isPresent()) {
            AbstractModelEngineSupport meg = this.meg().get();
            if (meg.isSubHitbox(this.getUniqueId())) {
               AbstractEntity parent = meg.getParent(this);
               if (parent != this) {
                  parent.setMetadata(key, value);
                  return;
               }
            }
         }

         Entity e = this.getBukkitEntity();
         e.setMetadata(key, new FixedMetadataValue(MythicBukkit.inst(), value));
      }
   }

   @Override
   public boolean hasMetadata(String key) {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Entity e = this.getBukkitEntity();
         return e.hasMetadata(key) || this.meg().map(meg -> {
            if (meg.isSubHitbox(this.getUniqueId())) {
               AbstractEntity parent = meg.getParent(this);
               if (parent != this) {
                  return parent.hasMetadata(key);
               }
            }

            return false;
         }).orElse(false);
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().hasMetadata(key)).join();
         } catch (Exception ex) {
            return false;
         }
      }
   }

   @Override
   public void removeMetadata(String key) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.removeMetadata(key));
      } else {
         if (this.meg().isPresent()) {
            AbstractModelEngineSupport meg = this.meg().get();
            if (meg.isSubHitbox(this.getUniqueId())) {
               AbstractEntity parent = meg.getParent(this);
               if (parent != this) {
                  parent.removeMetadata(key);
               }
            }
         }

         Entity e = this.getBukkitEntity();
         e.removeMetadata(key, MythicBukkit.inst());
      }
   }

   @Override
   public synchronized boolean isLoaded() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Entity e = this.getBukkitEntity();
         return e.getLocation().getChunk().isLoaded();
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getLocation().getChunk().isLoaded()).join();
         } catch (Exception ex) {
            return false;
         }
      }
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getMetadataInternal(key);
      } else if (Bukkit.isPrimaryThread()) {
         return Optional.empty();
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getMetadataInternal(key)).join();
         } catch (Exception e) {
            return Optional.empty();
         }
      }
   }

   private Optional<Object> getMetadataInternal(String key) {
      if (this.meg().isPresent()) {
         AbstractModelEngineSupport meg = this.meg().get();
         if (meg.isSubHitbox(this.getUniqueId())) {
            AbstractEntity parent = meg.getParent(this);
            if (parent != this) {
               return parent.getMetadata(key);
            }
         }
      }

      Entity e = this.getBukkitEntity();
      List<MetadataValue> values = e.getMetadata(key);
      if (values.size() > 1) {
         for (MetadataValue mv : values) {
            if (mv.getOwningPlugin().equals(MythicBukkit.inst())) {
               return Optional.of(mv.value());
            }
         }
      } else if (values.size() == 1) {
         return Optional.of(values.get(0).value());
      }

      return Optional.empty();
   }

   @Override
   public AbstractVector getVelocity() {
      Entity e = this.getBukkitEntity();
      if (e == null) {
         return new AbstractVector(0, 0, 0);
      } else if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return BukkitAdapter.adapt(e.getVelocity());
      } else if (Bukkit.isPrimaryThread()) {
         return new AbstractVector(0, 0, 0);
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> BukkitAdapter.adapt(e.getVelocity())).join();
         } catch (Exception ex) {
            return new AbstractVector(0, 0, 0);
         }
      }
   }

   @Override
   public void setVelocity(AbstractVector velocity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setVelocity(BukkitAdapter.adapt(velocity)));
      } else {
         Entity e = this.getBukkitEntity();
         Vector v = BukkitAdapter.adapt(velocity);
         e.setVelocity(v);
      }
   }

   @Override
   public AbstractVector getMotion() {
      return MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getEntityMotion(this);
   }

   @Override
   public boolean isOnGround() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().isOnGround();
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().isOnGround()).join();
         } catch (Exception e) {
            return false;
         }
      }
   }

   @Override
   public void setFreezingTicks(int i) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setFreezeTicks(i));
      } else {
         this.getBukkitEntity().setFreezeTicks(i);
      }
   }

   @Override
   public boolean isFreezing() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getFreezeTicks() > 0;
      } else if (Bukkit.isPrimaryThread()) {
         return false;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getFreezeTicks() > 0).join();
         } catch (Exception e) {
            return false;
         }
      }
   }

   @Override
   public int getFreezingTicks() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getFreezeTicks();
      } else if (Bukkit.isPrimaryThread()) {
         return 0;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getFreezeTicks()).join();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   @Override
   public int getMaxFreezingTicks() {
      if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         return this.getBukkitEntity().getMaxFreezeTicks();
      } else if (Bukkit.isPrimaryThread()) {
         return 0;
      } else {
         try {
            return Schedulers.of(this.entityRef).supply(() -> this.getBukkitEntity().getMaxFreezeTicks()).join();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   @Override
   public void setSavesToDisk(boolean b) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.entityRef)) {
         Schedulers.of(this.entityRef).run(() -> this.getBukkitEntity().setPersistent(b));
      } else {
         this.getBukkitEntity().setPersistent(b);
      }
   }

   private Optional<AbstractModelEngineSupport> meg() {
      return MythicBukkit.inst().getCompatibility().getModelEngine();
   }
}
