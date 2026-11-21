package io.lumine.mythic.core.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

public class VirtualEntity implements AbstractEntity {
   @Getter
   private final UUID uniqueId = new UUID(0L, 0L);
   private AbstractLocation location;

   public VirtualEntity() {
      this.location = new AbstractLocation(((World)Bukkit.getWorlds().get(0)).getName(), 0.0, 0.0, 0.0);
   }

   public VirtualEntity(AbstractLocation location) {
      this.location = location;
   }

   @Override
   public void setCustomName(String name) {
   }

   @Override
   public void setAlwaysShowName(boolean alwaysShowName) {
   }

   @Override
   public AbstractVector getMotion() {
      return null;
   }

   @Override
   public double getAbsorption() {
      return 0.0;
   }

   @Override
   public Entity getBukkitEntity() {
      return null;
   }

   @Override
   public AbstractLocation getLocation() {
      return this.location;
   }

   @Override
   public AbstractWorld getWorld() {
      return this.getLocation().getWorld();
   }

   @Override
   public double getHeight() {
      return 0.0;
   }

   @Override
   public double getPassengerRidingOffset() {
      return 0.0;
   }

   @Override
   public float getPassengerRidingOffset(AbstractEntity passenger) {
      return 0.0F;
   }

   @Override
   public double getNameplateHeight() {
      return 0.0;
   }

   @Override
   public double getNameplateMountOffset() {
      return 0.0;
   }

   @Override
   public boolean isLiving() {
      return false;
   }

   @Override
   public boolean isMob() {
      return false;
   }

   @Override
   public boolean isCreature() {
      return false;
   }

   @Override
   public boolean isMonster() {
      return false;
   }

   @Override
   public boolean isDamageable() {
      return false;
   }

   @Override
   public boolean isPlayer() {
      return false;
   }

   @Override
   public boolean hasLineOfSight(AbstractEntity e) {
      return ((LivingEntity)e.getBukkitEntity()).hasLineOfSight(BukkitAdapter.adapt(this.location));
   }

   @Override
   public boolean hasLineOfSight(AbstractLocation location) {
      return !((LivingEntity)this.getBukkitEntity()).hasLineOfSight(BukkitAdapter.adapt(location));
   }

   @Override
   public void teleport(AbstractLocation l) {
      this.location = l;
   }

   @Override
   public void teleport(AbstractLocation aLocation, AbstractTeleportData data) {
   }

   @Override
   public void setRotation(float yaw, float pitch) {
      this.location.setPitch(pitch);
      this.location.setYaw(yaw);
   }

   @Override
   public void lookAt(AbstractLocation location) {
   }

   @Override
   public void lookAt(AbstractEntity entity) {
   }

   @Override
   public void setRemoveWhenFarAway(boolean bool) {
   }

   @Override
   public void setPersistent(boolean bool) {
   }

   @Override
   public void setSaveToDisk(boolean bool) {
   }

   @Override
   public double getHealth() {
      return 0.0;
   }

   @Override
   public void setBlockInteractionRange(double newValue) {
   }

   @Override
   public void setEntityInteractionRange(double newValue) {
   }

   @Override
   public void setBlockBreakSpeed(double newValue) {
   }

   @Override
   public void setAttributeBaseValue(Attribute attribute, double newValue) {
   }

   @Override
   public boolean isDead() {
      return false;
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public boolean remove() {
      return true;
   }

   @Override
   public double getMaxHealth() {
      return 0.0;
   }

   @Override
   public void setFireTicks(int ticks) {
   }

   public String getCustomName() {
      return "Server";
   }

   @Override
   public void addPotionEffect(AbstractPotionEffect effect) {
   }

   @Override
   public void removePotionEffect(String type) {
   }

   @Override
   public void removePotionEffectType(AbstractPotionEffect effect) {
   }

   @Override
   public void setPassenger(Entity entity) {
   }

   @Override
   public boolean hasGravity() {
      return false;
   }

   @Override
   public void setGravity(boolean bool) {
   }

   @Override
   public Optional<AbstractItemStack> getItemHead() {
      return Optional.empty();
   }

   @Override
   public boolean hasAI() {
      return false;
   }

   @Override
   public void setAI(boolean bool) {
   }

   @Override
   public boolean isAware() {
      return false;
   }

   @Override
   public void setAware(boolean bool) {
   }

   @Override
   public AbstractLocation getEyeLocation() {
      return this.location;
   }

   @Override
   public double getEyeHeight() {
      return 0.0;
   }

   @Override
   public AbstractEntity getTarget() {
      return null;
   }

   @Override
   public void setTarget(AbstractEntity target) {
   }

   @Override
   public AbstractEntity getPassenger() {
      return null;
   }

   @Override
   public void addPassenger(AbstractEntity entity) {
   }

   @Override
   public Collection<AbstractEntity> getPassengers() {
      return null;
   }

   @Override
   public void removePassenger(Entity entity) {
   }

   @Override
   public AbstractEntity getVehicle() {
      return null;
   }

   @Override
   public boolean leaveVehicle() {
      return false;
   }

   @Override
   public void eject() {
   }

   @Override
   public void setHealth(double d) {
   }

   @Override
   public String getName() {
      return "Server";
   }

   @Override
   public Optional<String> getFaction() {
      return Optional.empty();
   }

   @Override
   public void setMaxHealth(double health) {
   }

   @Override
   public boolean damage(float damage) {
      return false;
   }

   @Override
   public boolean damage(float damage, AbstractEntity source) {
      return false;
   }

   @Override
   public void setPassenger(AbstractEntity entity) {
   }

   @Override
   public AbstractPlayer asPlayer() {
      return null;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof AbstractEntity ? this.getUniqueId().equals(((AbstractEntity)o).getUniqueId()) : super.equals(o);
   }

   @Override
   public int hashCode() {
      return this.getUniqueId().hashCode();
   }

   @Override
   public void setNoDamageTicks(int ticks) {
   }

   @Override
   public int getNoDamageTicks() {
      return 0;
   }

   @Override
   public boolean isCitizensNPC() {
      return false;
   }

   @Override
   public boolean isAnimal() {
      return false;
   }

   @Override
   public boolean isWaterMob() {
      return false;
   }

   @Override
   public boolean isFlyingMob() {
      return false;
   }

   @Override
   public boolean isGliding() {
      return false;
   }

   @Override
   public boolean hasPotionEffect(String type) {
      return false;
   }

   @Override
   public boolean hasPotionEffect(String type, RangedDouble level, RangedDouble duration) {
      return false;
   }

   @Override
   public boolean hasPotionEffect() {
      return false;
   }

   @Override
   public boolean hasScoreboardTag(String tag) {
      return false;
   }

   @Override
   public void addScoreboardTag(String tag) {
   }

   @Override
   public void removeScoreboardTag(String tag) {
      this.getBukkitEntity().removeScoreboardTag(tag);
   }

   @Override
   public void equipItemHead(AbstractItemStack item) {
   }

   @Override
   public Optional<AbstractItemStack> getItemChest() {
      return Optional.empty();
   }

   @Override
   public void equipItemChest(AbstractItemStack item) {
   }

   @Override
   public Optional<AbstractItemStack> getItemLegs() {
      return Optional.empty();
   }

   @Override
   public void equipItemLegs(AbstractItemStack item) {
   }

   @Override
   public Optional<AbstractItemStack> getItemFeet() {
      return Optional.empty();
   }

   @Override
   public void equipItemFeet(AbstractItemStack item) {
   }

   @Override
   public Optional<AbstractItemStack> getItemMainHand() {
      return Optional.empty();
   }

   @Override
   public void equipItemMainHand(AbstractItemStack item) {
   }

   @Override
   public Optional<AbstractItemStack> getItemOffhand() {
      return Optional.empty();
   }

   @Override
   public void equipItemOffHand(AbstractItemStack item) {
   }

   @Override
   public void setDamage(double damage) {
   }

   @Override
   public void setArmor(double armor) {
   }

   @Override
   public double getDamage() {
      return 0.0;
   }

   @Override
   public double getDamageBase() {
      return 0.0;
   }

   @Override
   public void setMovementSpeed(double speed) {
   }

   @Override
   public void setFlyingSpeed(double speed) {
   }

   @Override
   public void setFollowRange(double speed) {
   }

   @Override
   public void setAttackSpeed(double speed) {
   }

   @Override
   public void setStatAttributeModifier(String attributeKey, String statKey, double value) {
   }

   @Override
   public void removeStatAttributeModifier(String attributeKey, String statKey) {
   }

   @Override
   public void clearAllStatAttributeModifiers() {
   }

   @Override
   public int getLuck() {
      return 0;
   }

   @Override
   public int getEnchantmentLevel(String enchantmentName) {
      return 0;
   }

   @Override
   public int getEnchantmentLevelHeld(String enchantmentName) {
      return 0;
   }

   @Override
   public void setMetadata(String key, Object value) {
   }

   @Override
   public boolean hasMetadata(String key) {
      return false;
   }

   @Override
   public void removeMetadata(String key) {
   }

   @Override
   public synchronized boolean isLoaded() {
      return true;
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      return Optional.empty();
   }

   @Override
   public AbstractVector getVelocity() {
      return new AbstractVector(0, 0, 0);
   }

   @Override
   public void setVelocity(AbstractVector velocity) {
   }

   @Override
   public boolean isOnGround() {
      return false;
   }

   @Override
   public void setFreezingTicks(int i) {
   }

   @Override
   public boolean isFreezing() {
      return false;
   }

   @Override
   public int getFreezingTicks() {
      return 0;
   }

   @Override
   public int getMaxFreezingTicks() {
      return 0;
   }

   @Override
   public void setSavesToDisk(boolean b) {
   }

   @Override
   public PersistentDataContainer getDataContainer() {
      return null;
   }

   @Override
   public double getArmor() {
      return 0.0;
   }

   @Override
   public double getArmorBase() {
      return 0.0;
   }

   @Override
   public double getArmorToughness() {
      return 0.0;
   }

   @Override
   public double getArmorToughnessBase() {
      return 0.0;
   }

   @Override
   public void setArmorToughness(double armor) {
   }

   @Override
   public double getModelScaleBase() {
      return 0.0;
   }

   @Override
   public double getModelScale() {
      return 0.0;
   }

   @Override
   public void setModelScale(double value) {
   }

   @Override
   public void setStepHeight(double height) {
   }

   @Override
   public void setBurningTime(double newValue) {
   }

   @Override
   public void setExplosionKnockbackResistance(double newValue) {
   }

   @Override
   public void setFallDamageMultiplier(double newValue) {
   }

   @Override
   public void setGravityAttribute(double newValue) {
   }

   @Override
   public void setJumpStrength(double newValue) {
   }

   @Override
   public void setKnockbackResistance(double newValue) {
   }

   @Override
   public void setMovementEfficiency(double newValue) {
   }

   @Override
   public void setOxygenBonus(double newValue) {
   }

   @Override
   public void setSafeFallDistance(double newValue) {
   }

   @Override
   public void setSneakingSpeed(double newValue) {
   }

   @Override
   public void setWaterMovementEfficiency(double newValue) {
   }

   @Override
   public void setHealthAndMax(double d) {
   }
}
