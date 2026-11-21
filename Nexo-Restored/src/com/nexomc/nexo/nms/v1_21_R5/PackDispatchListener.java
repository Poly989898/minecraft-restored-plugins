package com.nexomc.nexo.nms.v1_21_R5;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.adventure.PaperAdventure;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.config.ServerResourcePackConfigurationTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/PackDispatchListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onSendLinks", "", "Lorg/bukkit/event/player/PlayerLinksSendEvent;", "Lorg/bukkit/event/EventHandler;", "Companion", "com.nexomc:v1_21_R5"}
)
public final class PackDispatchListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private static final Field configurationTasks;

   @EventHandler
   public final void onSendLinks(@NotNull PlayerLinksSendEvent $this$onSendLinks) {
      Intrinsics.checkNotNullParameter($this$onSendLinks, "<this>");
      if (!$this$onSendLinks.getPlayer().isOnline() && Settings.PACK_SEND_PRE_JOIN.toBool() && NexoPlugin.Companion.instance().packServer().isPackUploaded()) {
         List var10000 = MinecraftServer.getServer().getConnection().getConnections();
         Intrinsics.checkNotNullExpressionValue(var10000, "getConnections(...)");
         Iterable $this$mapNotNull$iv = (Iterable)var10000;
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
            int var14 = 0;
            Connection p0 = (Connection)element$iv$iv$iv;
            int var16 = 0;
            PacketListener var40 = p0.getPacketListener();
            if (var40 != null) {
               Object it$iv$iv = var40;
               int var18 = 0;
               destination$iv$iv.add(it$iv$iv);
            }
         }

         $this$mapNotNull$iv = (Iterable)((List)destination$iv$iv);
         destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $this$mapNotNull$iv) {
            if (element$iv$iv instanceof ServerConfigurationPacketListenerImpl) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         Iterable task = (Iterable)((List)destination$iv$iv);
         Iterator $this$mapNotNullTo$iv$iv = task.iterator();

         while(true) {
            if ($this$mapNotNullTo$iv$iv.hasNext()) {
               Object var28 = $this$mapNotNullTo$iv$iv.next();
               ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl)var28;
               int var34 = 0;
               if (!Intrinsics.areEqual(listener.getCraftPlayer().getUniqueId(), $this$onSendLinks.getPlayer().getUniqueId())) {
                  continue;
               }

               var41 = var28;
               break;
            }

            var41 = null;
            break;
         }

         ServerConfigurationPacketListenerImpl var42 = (ServerConfigurationPacketListenerImpl)var41;
         if (var42 != null) {
            ServerConfigurationPacketListenerImpl configPacketListener = var42;
            NexoPackServer it = NexoPlugin.Companion.instance().packServer();
            int var29 = 0;
            NexoPackServer var43 = it.isPackUploaded() ? it : null;
            if (var43 != null) {
               ResourcePackInfo var44 = var43.packInfo();
               if (var44 != null) {
                  block: {
                     ResourcePackInfo packInfo = var44;
                     boolean mandatory = NexoPackServer.Companion.getMandatory() && !$this$onSendLinks.getPlayer().hasPermission("nexo.resourcepack.bypass");
                     task = new ServerResourcePackConfigurationTask(new MinecraftServer.ServerResourcePackInfo(packInfo.id(), packInfo.uri().toString(), packInfo.hash(), mandatory, PaperAdventure.asVanilla(NexoPackServer.Companion.getPrompt())));
                     Field var45 = configurationTasks;
                     if (var45 != null) {
                        Object var46 = var45.get(configPacketListener);
                        if (var46 != null) {
                           Object $this$safeCast$iv = var46;
                           var46 = $this$safeCast$iv;
                           if (!($this$safeCast$iv instanceof Queue)) {
                              var46 = null;
                           }

                           var47 = (Queue)var46;
                           break block;
                        }
                     }

                     var47 = null;
                  }

                  Queue tasks = var47;

                  try {
                     int var36 = 0;
                     $this$onSendLinks_u24lambda_u242 = ResultShim.ok(tasks != null ? tasks.add(task) : null);
                  } catch (Throwable var19) {
                     $this$onSendLinks_u24lambda_u242 = ResultShim.ok(ResultKt.createFailure(var19));
                  }

                  Throwable var51 = ResultShim.exceptionOrNull($this$onSendLinks_u24lambda_u242);
                  if (var51 != null) {
                     Throwable it = var51;
                     Logs.logWarn("Failed to send Nexo ResourcePack in Pre-Join state for " + $this$onSendLinks.getPlayer().getName() + ", will send normally...", false);
                     if (Settings.DEBUG.toBool()) {
                        Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
                     }
                  }

                  return;
               }
            }

         }
      }
   }

   static {
      Companion var0 = Companion;

      Object $this$configurationTasks_u24lambda_u240;
      try {
         int var2 = 0;
         Field $this$configurationTasks_u24lambda_u240_u240 = ServerConfigurationPacketListenerImpl.class.getDeclaredField("configurationTasks");
         int var5 = 0;
         $this$configurationTasks_u24lambda_u240_u240.setAccessible(true);
         $this$configurationTasks_u24lambda_u240 = ResultShim.ok($this$configurationTasks_u24lambda_u240_u240);
      } catch (Throwable var6) {
         $this$configurationTasks_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var6));
      }

      Object var7 = UtilsKt.printOnFailure($this$configurationTasks_u24lambda_u240, false);
      configurationTasks = (Field)(ResultShim.isFailure(var7) ? null : var7);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/PackDispatchListener$Companion;", "", "<init>", "()V", "configurationTasks", "Ljava/lang/reflect/Field;", "com.nexomc:v1_21_R5"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
