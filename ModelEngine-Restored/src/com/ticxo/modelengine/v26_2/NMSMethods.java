package com.ticxo.modelengine.v26_2;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Getter;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;

@Getter
public enum NMSMethods implements ReflectionUtils.MethodEnum {
   TRACKED_ENTITY_getEffectiveRange(TrackedEntity.class, "b", "getEffectiveRange"),
   SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit(ServerGamePacketListenerImpl.class, "checkLimit", "checkLimit", long.class),
   INSTRUMENT_ITEM_getInstrument(InstrumentItem.class, "a", "getInstrument", ItemStack.class),
   ENTITY_getPassengerAttachmentPoint(Entity.class, "a", "getPassengerAttachmentPoint", Entity.class, EntityDimensions.class, float.class);

   private final Class<?> target;
   private final String obfuscated;
   private final String mapped;
   private final Class<?>[] parameterClasses;

   NMSMethods(Class<?> target, String obfuscated, String mapped, Class<?>... parameterClasses) {
      this.target = target;
      this.obfuscated = obfuscated;
      this.mapped = mapped;
      this.parameterClasses = parameterClasses;
   }
}
