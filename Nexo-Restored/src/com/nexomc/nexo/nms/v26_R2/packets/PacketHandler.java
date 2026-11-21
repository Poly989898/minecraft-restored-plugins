package com.nexomc.nexo.nms.v26_R2.packets;

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
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/PacketHandler;", "Lcom/nexomc/nexo/nms/IPacketHandler;", "<init>", "()V", "handle", "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;", "Lio/papermc/paper/connection/PaperPlayerConfigurationConnection;", "getHandle", "(Lio/papermc/paper/connection/PaperPlayerConfigurationConnection;)Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;", "inject", "", "connection", "player", "Lorg/bukkit/entity/Player;", "uninject", "Companion", "com.nexomc:v26_R2"}
)
public final class PacketHandler implements IPacketHandler {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Field handleMethod;

   @NotNull
   public final ServerCommonPacketListenerImpl getHandle(@NotNull PaperPlayerConfigurationConnection paperPlayerConfigurationConnection) {
      Object obj;
      try {
         obj = handleMethod.get(paperPlayerConfigurationConnection);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException(e);
      }

      return (ServerCommonPacketListenerImpl)obj;
   }

   public final void inject(@NotNull PaperPlayerConfigurationConnection connection) {
      ChannelPipeline pipeline = this.getHandle(connection).connection.channel.pipeline();
      String language = connection.getClientInformation().language();
      Locale locale2 = Translator.parseLocale(StringsKt.replace(language, "_", "-", false));
      if (locale2 == null) {
         locale2 = Locale.ENGLISH;
      }

      Locale locale = locale2;
      UUID id = connection.getProfile().getId();
      NexoChannelHandler handler = new NexoChannelHandler(id, locale);
      PacketHandler packetHandler = this;

      try {
         PacketHandler packetHandler2 = packetHandler;
         int value2 = 0;
         Object obj = ResultShim.ok(pipeline.remove("nexo_packet_handler"));
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Iterable entrySet = (Iterable)pipeline.toMap().entrySet();

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value3 = 0;
         String key = (String)entry.getKey();
         ChannelHandler value = (ChannelHandler)entry.getValue();
         if (value instanceof Connection) {
            pipeline.addBefore(key, "nexo_packet_handler", (ChannelHandler)handler);
         }
      }

   }

   public void inject(@NotNull Player player) {
   }

   public void uninject(@NotNull Player player) {
      Channel channel2 = ((CraftPlayer)player).getHandle().connection.connection.channel;
      Channel channel = channel2;
      ChannelPipeline pipeline = channel.pipeline();
      channel.eventLoop().submit((() -> PacketHandler.uninject$lambda$0(pipeline)));
   }

   private static final void uninject$lambda$0(ChannelPipeline pipeline) {
      pipeline.remove("nexo_packet_handler");
   }

   static {
      Field field;
      try {
         field = PaperCommonConnection.class.getDeclaredField("packetListener");
      } catch (NoSuchFieldException e) {
         throw new ExceptionInInitializerError(e);
      }

      int value = 0;
      field.setAccessible(true);
      handleMethod = field;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/PacketHandler$Companion;", "", "<init>", "()V", "handleMethod", "Ljava/lang/reflect/Field;", "getHandleMethod", "()Ljava/lang/reflect/Field;", "com.nexomc:v26_R2"}
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
