package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;

public interface VolatileEntityHandlerDisabled {
   AbstractEntity addNBTData(AbstractEntity abstractEntity, String str, Tag tag);

   CompoundTag getNBTData(AbstractEntity abstractEntity);

   AbstractEntity setNBTData(AbstractEntity abstractEntity, CompoundTag compoundTag);

   boolean isEntityInMotion(AbstractEntity abstractEntity);

   default void setLocation(AbstractEntity entity, AbstractLocation location) {
      this.setLocation(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), false, false);
   }

   default void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch) {
      this.setLocation(entity, x, y, z, yaw, pitch, false, false);
   }

   void setLocation(AbstractEntity abstractEntity, double value, double value2, double value3, float value4, float value5, boolean flag, boolean flag2);

   default void setHitBox(AbstractEntity target, double a0, double a1, double a2) {
   }

   default void setItemPosition(AbstractEntity target, AbstractLocation ol) {
   }

   default void sendEntityTeleportPacket(AbstractEntity target) {
   }

   void setEntityRotation(AbstractEntity abstractEntity, float value, float value2);

   void setArmorStandNoGravity(AbstractEntity abstractEntity);

   void setSkybox(AbstractPlayer abstractPlayer, int value);

   default void forcePlayCredits(AbstractPlayer target, float f) {
   }

   default void forceCloseWindow(AbstractPlayer target) {
   }

   default void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
   }

   float getEntityAbsorptionHearts(AbstractEntity abstractEntity);

   void setEntityAbsorptionHearts(AbstractEntity abstractEntity, float value);
}
