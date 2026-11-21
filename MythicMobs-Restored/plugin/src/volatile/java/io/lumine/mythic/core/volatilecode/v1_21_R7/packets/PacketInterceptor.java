package io.lumine.mythic.core.volatilecode.v1_21_R7.packets;

import com.google.common.collect.Maps;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public class PacketInterceptor {
   private final Map<Class<? extends Packet<?>>, PacketInterceptor.Modifier<?, ?>> registry = Maps.newConcurrentMap();
   private final Map<Class<? extends Packet<?>>, PacketInterceptor.Listener<?, ?>> postRegistry = Maps.newConcurrentMap();

   public <T extends Packet<U>, U extends PacketListener> PacketInterceptor register(Class<T> clazz, Function<T, Packet<U>> function) {
      this.registry.put(clazz, new PacketInterceptor.Modifier<>(clazz, function));
      return this;
   }

   public <T extends Packet<U>, U extends PacketListener> PacketInterceptor registerPost(Class<T> clazz, Consumer<T> consumer) {
      this.postRegistry.put(clazz, new PacketInterceptor.Listener<>(clazz, consumer));
      return this;
   }

   public <T extends PacketListener> Packet<T> accept(Packet<T> original) {
      if (original == null) {
         return null;
      } else {
         PacketInterceptor.Modifier<? extends Packet<?>, ?> modifier = (PacketInterceptor.Modifier<? extends Packet<?>, ?>)this.registry
            .get(original.getClass());
         return modifier == null ? original : modifier.modify(original);
      }
   }

   public void acceptPost(Packet<?> original) {
      if (original != null) {
         PacketInterceptor.Listener<? extends Packet<?>, ?> listener = (PacketInterceptor.Listener<? extends Packet<?>, ?>)this.postRegistry
            .get(original.getClass());
         if (listener != null) {
            listener.listen(original);
         }
      }
   }

   private record Listener<T extends Packet<U>, U extends PacketListener>(Class<T> clazz, Consumer<T> consumer) {
      public void listen(Packet<?> original) {
         try {
            this.consumer.accept(this.clazz.cast(original));
         } catch (Throwable var3) {
            MythicLogger.error("An error had occurred while intercepting the packet " + this.clazz.getSimpleName());
            var3.printStackTrace();
         }
      }
   }

   private record Modifier<T extends Packet<U>, U extends PacketListener>(Class<T> clazz, Function<T, Packet<U>> function) {
      public Packet<?> modify(Packet<?> original) {
         try {
            return this.function.apply(this.clazz.cast(original));
         } catch (Throwable var3) {
            MythicLogger.error("An error had occurred while modifying the packet " + this.clazz.getSimpleName());
            var3.printStackTrace();
            return original;
         }
      }
   }
}
