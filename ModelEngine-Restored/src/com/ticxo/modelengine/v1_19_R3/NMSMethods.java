package com.ticxo.modelengine.v1_19_R3;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Getter;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;

@Getter
public enum NMSMethods implements ReflectionUtils.MethodEnum {
   TRACKED_ENTITY_getEffectiveRange(EntityTracker.class, "b", "getEffectiveRange"),
   SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit(PlayerConnection.class, "checkLimit", "checkLimit", long.class),
   INSTRUMENT_ITEM_getInstrument(InstrumentItem.class, "d", "getInstrument", ItemStack.class);

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
