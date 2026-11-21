package com.nexomc.nexo.nms;

import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u0000 \b2\u00020\u0001:\u0002\u0007\bJ\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\tÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/IPacketHandler;", "", "inject", "", "player", "Lorg/bukkit/entity/Player;", "uninject", "EmptyPacketHandler", "Companion", "com.nexomc:core"}
)
public interface IPacketHandler {
   @NotNull
   Companion Companion = IPacketHandler.Companion.$$INSTANCE;
   @NotNull
   String PACKET_HANDLER = "nexo_packet_handler";

   default void inject(@NotNull Player player) {
   }

   default void uninject(@NotNull Player player) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
      d2 = {"Lcom/nexomc/nexo/nms/IPacketHandler$EmptyPacketHandler;", "Lcom/nexomc/nexo/nms/IPacketHandler;", "<init>", "()V", "com.nexomc:core"}
   )
   public static final class EmptyPacketHandler implements IPacketHandler {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      public static void inject(@NotNull IPacketHandler iPacketHandler, @NotNull Player player) {
         iPacketHandler.inject(player);
      }

      /** @deprecated */
      @Deprecated
      public static void uninject(@NotNull IPacketHandler iPacketHandler, @NotNull Player player) {
         iPacketHandler.uninject(player);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/nms/IPacketHandler$Companion;", "", "<init>", "()V", "PACKET_HANDLER", "", "com.nexomc:core"}
   )
   public static final class Companion {
      public static final Companion $$INSTANCE = new Companion();
      @NotNull
      public static final String PACKET_HANDLER = "nexo_packet_handler";

      private Companion() {
      }
   }
}
