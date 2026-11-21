package io.lumine.mythic.api.adapters;

import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public interface AbstractEntity {
   AbstractLocation getLocation();

   Entity getBukkitEntity();

   boolean isLiving();

   boolean isMob();

   boolean isCreature();

   boolean isPlayer();

   boolean isAnimal();

   boolean isWaterMob();

   boolean isFlyingMob();

   UUID getUniqueId();

   default int getEntityId() {
      return this.getBukkitEntity().getEntityId();
   }

   boolean hasLineOfSight(AbstractEntity abstractEntity);

   void teleport(AbstractLocation abstractLocation);

   void teleport(AbstractLocation abstractLocation, AbstractTeleportData abstractTeleportData);

   void setRotation(float value, float value2);

   void lookAt(AbstractLocation abstractLocation);

   void lookAt(AbstractEntity abstractEntity);

   void setRemoveWhenFarAway(boolean flag);

   void setPersistent(boolean flag);

   void setSaveToDisk(boolean flag);

   double getHealth();

   void setBlockInteractionRange(double value);

   void setEntityInteractionRange(double value);

   void setBlockBreakSpeed(double value);

   void setAttributeBaseValue(Attribute attribute, double value);

   boolean isDead();

   boolean isValid();

   boolean remove();

   double getMaxHealth();

   void setMaxHealth(double value);

   void setHealth(double value);

   void setHealthAndMax(double value);

   double getDamage();

   double getDamageBase();

   void setDamage(double value);

   double getArmor();

   double getArmorBase();

   void setArmor(double value);

   double getArmorToughness();

   double getArmorToughnessBase();

   void setArmorToughness(double value);

   double getModelScaleBase();

   double getModelScale();

   void setModelScale(double value);

   void setStepHeight(double value);

   void setBurningTime(double value);

   void setExplosionKnockbackResistance(double value);

   void setFallDamageMultiplier(double value);

   void setGravityAttribute(double value);

   void setJumpStrength(double value);

   void setKnockbackResistance(double value);

   void setMovementEfficiency(double value);

   void setOxygenBonus(double value);

   void setSafeFallDistance(double value);

   void setSneakingSpeed(double value);

   void setWaterMovementEfficiency(double value);

   void setFireTicks(int value);

   Object getCustomName();

   void setPassenger(Entity entity);

   AbstractLocation getEyeLocation();

   double getEyeHeight();

   AbstractWorld getWorld();

   double getHeight();

   double getPassengerRidingOffset();

   float getPassengerRidingOffset(AbstractEntity abstractEntity);

   double getNameplateHeight();

   double getNameplateMountOffset();

   AbstractEntity getTarget();

   void setTarget(AbstractEntity abstractEntity);

   AbstractEntity getVehicle();

   boolean leaveVehicle();

   void eject();

   String getName();

   Optional<String> getFaction();

   boolean damage(float value);

   boolean damage(float value, AbstractEntity abstractEntity);

   void setPassenger(AbstractEntity abstractEntity);

   AbstractPlayer asPlayer();

   void setNoDamageTicks(int value);

   int getNoDamageTicks();

   boolean isMonster();

   boolean isCitizensNPC();

   boolean isGliding();

   void addPotionEffect(AbstractPotionEffect abstractPotionEffect);

   void removePotionEffect(String str);

   void removePotionEffectType(AbstractPotionEffect abstractPotionEffect);

   boolean hasPotionEffect(String str);

   boolean hasPotionEffect(String str, RangedDouble rangedDouble, RangedDouble rangedDouble2);

   boolean hasPotionEffect();

   boolean hasScoreboardTag(String str);

   void addScoreboardTag(String str);

   void removeScoreboardTag(String str);

   AbstractEntity getPassenger();

   void addPassenger(AbstractEntity abstractEntity);

   Collection<AbstractEntity> getPassengers();

   void removePassenger(Entity entity);

   boolean hasGravity();

   void setGravity(boolean flag);

   Optional<AbstractItemStack> getItemHead();

   void equipItemHead(AbstractItemStack abstractItemStack);

   Optional<AbstractItemStack> getItemChest();

   void equipItemChest(AbstractItemStack abstractItemStack);

   Optional<AbstractItemStack> getItemLegs();

   void equipItemLegs(AbstractItemStack abstractItemStack);

   Optional<AbstractItemStack> getItemFeet();

   void equipItemFeet(AbstractItemStack abstractItemStack);

   Optional<AbstractItemStack> getItemMainHand();

   void equipItemMainHand(AbstractItemStack abstractItemStack);

   Optional<AbstractItemStack> getItemOffhand();

   void equipItemOffHand(AbstractItemStack abstractItemStack);

   void setMovementSpeed(double value);

   void setFlyingSpeed(double value);

   void setFollowRange(double value);

   void setAttackSpeed(double value);

   void setStatAttributeModifier(String str, String str2, double value);

   void removeStatAttributeModifier(String str, String str2);

   void clearAllStatAttributeModifiers();

   int getLuck();

   void setMetadata(String str, Object obj);

   boolean hasMetadata(String str);

   void removeMetadata(String str);

   Optional<Object> getMetadata(String str);

   boolean isLoaded();

   boolean hasAI();

   void setAI(boolean flag);

   boolean isAware();

   void setAware(boolean flag);

   void setVelocity(AbstractVector abstractVector);

   int getEnchantmentLevel(String str);

   int getEnchantmentLevelHeld(String str);

   boolean isOnGround();

   AbstractVector getVelocity();

   boolean isDamageable();

   void setFreezingTicks(int value);

   boolean isFreezing();

   int getFreezingTicks();

   int getMaxFreezingTicks();

   PersistentDataContainer getDataContainer();

   void setSavesToDisk(boolean flag);

   boolean hasLineOfSight(AbstractLocation abstractLocation);

   void setCustomName(String str);

   void setAlwaysShowName(boolean flag);

   AbstractVector getMotion();

   double getAbsorption();

   default double distanceSquared(AbstractEntity other, AbstractEntity.DistanceMode mode) {
      return switch (mode) {
         case POS -> this.getLocation().distanceSquared(other.getLocation());
         case SELF_BOX -> other.getLocation().distanceSquared(this);
         case OTHER_BOX -> this.getLocation().distanceSquared(other);
         case BOX -> {
            BoundingBox boxA = this.getBukkitEntity().getBoundingBox();
            Vector minA = boxA.getMin();
            Vector maxA = boxA.getMax();
            BoundingBox boxB = other.getBukkitEntity().getBoundingBox();
            Vector minB = boxB.getMin();
            Vector maxB = boxB.getMax();
            double ux = Math.max(0.0, minA.getX() - maxB.getX());
            double uy = Math.max(0.0, minA.getY() - maxB.getY());
            double uz = Math.max(0.0, minA.getZ() - maxB.getZ());
            double vx = Math.max(0.0, minB.getX() - maxA.getX());
            double vy = Math.max(0.0, minB.getY() - maxA.getY());
            double vz = Math.max(0.0, minB.getZ() - maxA.getZ());
            yield ux * ux + uy * uy + uz * uz + vx * vx + vy * vy + vz * vz;
         }
      };
   }

   public static enum DistanceMode {
      POS,
      SELF_BOX,
      OTHER_BOX,
      BOX;
   }
}
