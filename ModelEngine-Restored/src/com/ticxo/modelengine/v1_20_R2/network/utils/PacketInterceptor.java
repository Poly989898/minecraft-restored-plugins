package com.ticxo.modelengine.v1_20_R2.network.utils;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.Packet;

public class PacketInterceptor {
   private final Map<Class<? extends Packet>, PacketInterceptor.Modifier<? extends Packet>> registry = Maps.newConcurrentMap();
   private final Map<Class<? extends Packet>, PacketInterceptor.Listener<? extends Packet>> postRegistry = Maps.newConcurrentMap();

   public <T extends Packet> PacketInterceptor register(Class<T> clazz, Function<T, Packet> function) {
      this.registry.put(clazz, new PacketInterceptor.Modifier<>(clazz, function));
      return this;
   }

   public <T extends Packet> PacketInterceptor registerPost(Class<T> clazz, Function<T, Collection<Packet>> consumer) {
      this.postRegistry.put(clazz, new PacketInterceptor.Listener<>(clazz, consumer));
      return this;
   }

   public Packet accept(Packet original) {
      if (original == null) {
         return null;
      }

      PacketInterceptor.Modifier<?> modifier = this.registry.get(original.getClass());
      return modifier == null ? original : modifier.modify(original);
   }

   public Collection<Packet> acceptPost(Packet original) {
      if (original == null) {
         return List.of();
      }

      PacketInterceptor.Listener<?> listener = this.postRegistry.get(original.getClass());
      return listener != null ? listener.listen(original) : List.of();
   }

   @AllArgsConstructor
   static class Listener<T extends Packet> {
      private final Class<T> clazz;
      private final Function<T, Collection<Packet>> function;

      public Collection<Packet> listen(Packet original) {
         try {
            Collection<Packet> collection = this.function.apply(this.clazz.cast(original));
            return collection == null ? List.of() : collection;
         } catch (Throwable t) {
            TLogger.error("An error had occurred while intercepting the packet " + this.clazz.getSimpleName());
            t.printStackTrace();
            return List.of();
         }
      }
   }

   @AllArgsConstructor
   static class Modifier<T extends Packet> {
      private final Class<T> clazz;
      private final Function<T, Packet> function;

      public Packet modify(Packet original) {
         try {
            return this.function.apply(this.clazz.cast(original));
         } catch (Throwable t) {
            TLogger.error("An error had occurred while modifying the packet " + this.clazz.getSimpleName());
            t.printStackTrace();
            return original;
         }
      }
   }
}
