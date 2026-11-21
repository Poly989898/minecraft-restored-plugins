package io.lumine.mythic.api.volatilecode.handlers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.Collection;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface VolatileEntityHandler {
   default boolean hasDefaultAttributes(EntityType entityType) {
      return false;
   }

   default BukkitAttributable getDefaultAttributes(EntityType entityType) {
      return null;
   }

   AbstractEntity addNBTData(AbstractEntity abstractEntity, String str, Tag tag);

   CompoundTag getNBTData(AbstractEntity abstractEntity);

   AbstractEntity setNBTData(AbstractEntity abstractEntity, CompoundTag compoundTag);

   boolean isEntityInMotion(AbstractEntity abstractEntity, boolean flag);

   default AbstractVector getEntityMotion(AbstractEntity entity) {
      return new AbstractVector(0, 0, 0);
   }

   default void setLocation(AbstractEntity entity, AbstractLocation location) {
      this.setLocation(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), false, false);
   }

   default void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch) {
      this.setLocation(entity, x, y, z, yaw, pitch, false, false);
   }

   void setLocation(AbstractEntity abstractEntity, double value, double value2, double value3, float value4, float value5, boolean flag, boolean flag2);

   default void setPlayerRotation(AbstractPlayer entity, float yaw, float pitch, boolean relative) {
   }

   default void setHitBox(AbstractEntity target, double width, double height) {
   }

   default void setItemPosition(AbstractEntity target, AbstractLocation ol) {
   }

   default void sendEntityTeleportPacket(AbstractEntity target) {
   }

   void setEntityRotation(AbstractEntity abstractEntity, float value, float value2);

   void setArmorStandNoGravity(AbstractEntity abstractEntity);

   default void sendGameStateChange(AbstractPlayer target, int state, int data) {
   }

   default void forcePlayCredits(AbstractPlayer target, float f) {
   }

   default void forceCloseWindow(AbstractPlayer target) {
   }

   default void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
   }

   default void setPlayerTickRate(AbstractPlayer target, float tickRate, boolean frozen) {
   }

   default void resetPlayerTickRate(AbstractPlayer target) {
   }

   default boolean setEntityTicking(AbstractEntity target, boolean ticking) {
      return false;
   }

   float getEntityAbsorptionHearts(AbstractEntity abstractEntity);

   void setEntityAbsorptionHearts(AbstractEntity abstractEntity, float value);

   void sendPlayerFakeInventoryItem(AbstractPlayer abstractPlayer, ItemStack itemStack, int value);

   default void setEntityPitch(AbstractEntity target, float pitch) {
   }

   void sendActionBarMessageToPlayer(AbstractPlayer abstractPlayer, String str);

   default void playTotemEffect(AbstractPlayer target, ItemStack item) {
   }

   void setEntitySpawnReason(AbstractEntity abstractEntity, SpawnReason spawnReason);

   default void playTotemEffect(AbstractPlayer target, int model) {
   }

   void setEntityPose(AbstractEntity abstractEntity, String str);

   double getPassengerRidingOffset(AbstractEntity abstractEntity);

   default float getPassengerRidingOffset(AbstractEntity entity, AbstractEntity passenger) {
      return (float)this.getPassengerRidingOffset(entity);
   }

   Collection<AbstractPlayer> getTrackedPlayers(AbstractEntity abstractEntity);

   default float getItemGroupCooldownPercent(AbstractPlayer player, String namespacedKey) {
      return 0.0F;
   }

   default void setItemGroupCooldown(AbstractPlayer player, String namespacedKey, int ticks) {
   }

   default void removeItemGroupCooldown(AbstractPlayer player, String namespacedKey) {
   }

   void spawnFakeLightning(AbstractLocation abstractLocation, double value);

   default void playFreezeEffect(AbstractPlayer target, int ticks) {
   }

   default void playEntityAnimation(AbstractEntity entity, byte effect, Collection<AbstractPlayer> audience) {
   }

   int getNextEntityId();

   default void setCustomName(AbstractEntity entity, String name) {
   }

   default boolean doDamage(DamageMetadata data, AbstractEntity target) {
      return false;
   }

   default void registerAttribute(AbstractEntity entity, Attribute attribute) {
   }

   default void stopUsingItem(AbstractEntity entity) {
   }

   default void setGlowing(AbstractEntity entity, boolean glowing, AbstractPlayer player) {
   }

   default void setGlowColor(AbstractEntity entity, @NotNull GlowColor color, AbstractPlayer player) {
   }

   default void initTeams(AbstractPlayer player) {
   }

   default boolean isUsingSpyglass(AbstractPlayer player) {
      return false;
   }

   default boolean shootFrogTongue(AbstractEntity frog, AbstractEntity target) {
      return false;
   }

   default boolean canBeHitByProjectile(AbstractEntity entity) {
      return true;
   }
}
