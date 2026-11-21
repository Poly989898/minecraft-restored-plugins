package com.nexomc.nexo.nms.v1_21_R11.packets;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.nms.IPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.papermc.paper.connection.PaperCommonConnection;
import io.papermc.paper.connection.PaperPlayerConfigurationConnection;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.translation.Translator;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0006J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0010\u0010\u000e\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\rH\u0016R\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/packets/PacketHandler;", "Lcom/nexomc/nexo/nms/IPacketHandler;", "<init>", "()V", "handle", "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;", "Lio/papermc/paper/connection/PaperPlayerConfigurationConnection;", "getHandle", "(Lio/papermc/paper/connection/PaperPlayerConfigurationConnection;)Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;", "inject", "", "connection", "player", "Lorg/bukkit/entity/Player;", "uninject", "Companion", "com.nexomc:v1_21_R11"}
)
public final class PacketHandler implements IPacketHandler {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Field handleMethod;

   @NotNull
   public final ServerCommonPacketListenerImpl getHandle(@NotNull PaperPlayerConfigurationConnection $this$handle) {
      Intrinsics.checkNotNullParameter($this$handle, "<this>");
      Object var10000 = handleMethod.get($this$handle);
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type net.minecraft.server.network.ServerCommonPacketListenerImpl");
      return (ServerCommonPacketListenerImpl)var10000;
   }

   public final void inject(@NotNull PaperPlayerConfigurationConnection connection) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      ChannelPipeline pipeline = this.getHandle(connection).connection.channel.pipeline();
      String var10000 = connection.getClientInformation().language();
      Intrinsics.checkNotNullExpressionValue(var10000, "language(...)");
      Locale var19 = Translator.parseLocale(StringsKt.replace(var10000, "_", "-", false));
      if (var19 == null) {
         var19 = Locale.ENGLISH;
      }

      Locale locale = var19;
      UUID var10002 = connection.getProfile().getId();
      Intrinsics.checkNotNull(var10002);
      Intrinsics.checkNotNull(locale);
      NexoChannelHandler handler = new NexoChannelHandler(var10002, locale);
      PacketHandler var5 = this;

      try {
         PacketHandler $this$inject_u24lambda_u240 = var5;
         int var7 = 0;
         Object var16 = ResultShim.ok(pipeline.remove("nexo_packet_handler"));
      } catch (Throwable var13) {
         Object $this$inject_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var13));
      }

      Iterable $this$forEach$iv = (Iterable)pipeline.toMap().entrySet();

      for(Object element$iv : $this$forEach$iv) {
         Map.Entry var9 = (Map.Entry)element$iv;
         int var10 = 0;
         String key = (String)var9.getKey();
         ChannelHandler value = (ChannelHandler)var9.getValue();
         if (value instanceof Connection) {
            pipeline.addBefore(key, "nexo_packet_handler", (ChannelHandler)handler);
         }
      }

   }

   public void inject(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
   }

   public void uninject(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
      Channel var10000 = ((CraftPlayer)player).getHandle().connection.connection.channel;
      Intrinsics.checkNotNullExpressionValue(var10000, "channel");
      Channel channel = var10000;
      ChannelPipeline pipeline = channel.pipeline();
      channel.eventLoop().submit(PacketHandler::uninject$lambda$0);
   }

   private static final void uninject$lambda$0(ChannelPipeline $pipeline) {
      $pipeline.remove("nexo_packet_handler");
   }

   static {
      Field $this$handleMethod_u24lambda_u240 = PaperCommonConnection.class.getDeclaredField("handle");
      int var2 = 0;
      $this$handleMethod_u24lambda_u240.setAccessible(true);
      Intrinsics.checkNotNullExpressionValue($this$handleMethod_u24lambda_u240, "apply(...)");
      handleMethod = $this$handleMethod_u24lambda_u240;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/packets/PacketHandler$Companion;", "", "<init>", "()V", "handleMethod", "Ljava/lang/reflect/Field;", "getHandleMethod", "()Ljava/lang/reflect/Field;", "com.nexomc:v1_21_R11"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Field getHandleMethod() {
         return PacketHandler.handleMethod;
      }
   }
}
