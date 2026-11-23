package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.pack.server.SelfHostServer;
import com.nexomc.nexo.pack.server.PolymathServer;
import com.nexomc.nexo.pack.server.HermesServer;
import com.nexomc.nexo.pack.server.LobFileServer;
import com.nexomc.nexo.pack.server.S3Server;
import com.nexomc.nexo.pack.server.EmptyServer;
import com.nexomc.nexo.pack.server.EmptyServer;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.pack.server.PackServerRegistry;
import kotlin.jvm.functions.Function0;
import org.bukkit.event.Listener;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.PackListener;
import com.nexomc.nexo.pack.multipack.MultipackManager;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.resource.ResourcePackStatus;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015J\u000e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016J \u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u00062\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u0003H\u0016J\u0010\u0010\b\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000bH\u0016J\b\u0010\u000f\u001a\u00020\u000bH\u0016J\b\u0010\u0010\u001a\u00020\u0011H&J\n\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0016J\n\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016R\u0014\u0010\u0002\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004¨\u0006\u0016À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/pack/server/NexoPackServer;", "", "isPackUploaded", "", "()Z", "uploadPack", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "sendPack", "connection", "reconfigure", "", "player", "Lorg/bukkit/entity/Player;", "start", "stop", "packUrl", "", "hash", "packInfo", "Lnet/kyori/adventure/resource/ResourcePackInfo;", "Companion", "com.nexomc:core"}
)
public interface NexoPackServer {
   @NotNull
   Companion Companion = NexoPackServer.Companion.$$INSTANCE;
   @NotNull
   String BYPASS_PERMISSION = "nexo.resourcepack.bypass";

   default boolean isPackUploaded() {
      return this.uploadPack().isDone();
   }

   @NotNull
   default CompletableFuture<Void> uploadPack() {
      CompletableFuture[] completableFutures = new CompletableFuture[]{NexoPlugin.Companion.instance().packGenerator().getPackGenFuture()};
      CompletableFuture completableFuture = CompletableFuture.allOf(completableFutures);
      return completableFuture;
   }

   @Nullable
   default CompletableFuture<Void> sendPack(@NotNull Object connection, boolean reconfigure) {
      PlayerConfigurationConnection playerConfigurationConnection = connection instanceof PlayerConfigurationConnection ? (PlayerConfigurationConnection)connection : null;
      if ((connection instanceof PlayerConfigurationConnection ? (PlayerConfigurationConnection)connection : null) == null) {
         return null;
      } else {
         PlayerConfigurationConnection connection_2 = playerConfigurationConnection;
         CompletableFuture future = reconfigure ? null : new CompletableFuture();
         ResourcePackInfo packInfo = NexoPlugin.Companion.instance().packServer().packInfo();
         if (packInfo == null) {
            return null;
         } else {
            ResourcePackInfo info = packInfo;
            MultipackManager multipackManager = NexoPlugin.Companion.instance().multipackManager();
            ResourcePackInfo[] resourcePackInfos = new ResourcePackInfo[]{info};
            List allPacks = CollectionsKt.mutableListOf(resourcePackInfos);
            UUID profileId = connection_2.getProfile().getId();
            if (profileId != null) {
               OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer(profileId);
               OfflinePlayer offlinePlayer = offlinePlayer2;
               allPacks.addAll((Collection)multipackManager.templatePackInfos(offlinePlayer));
            } else {
               allPacks.addAll((Collection)multipackManager.requiredTemplatePackInfos());
            }

            AtomicInteger remaining = new AtomicInteger(allPacks.size());
            ResourcePackRequest resourcePackRequest = ResourcePackRequest.resourcePackRequest().required(Companion.getMandatory()).replace(reconfigure).prompt(Companion.getPrompt()).packs((Iterable)allPacks).callback((uuid, status, audience) -> NexoPackServer.sendPack$lambda$0(remaining, future, connection_2, uuid, status, audience)).build();
            ResourcePackRequest request = resourcePackRequest;
            connection_2.getAudience().sendResourcePacks(request);

            return future;
         }
      }
   }

   default public void sendPack(@NotNull Player player2) {
        block0: {
            Intrinsics.checkNotNullParameter((Object)player2, "player");
            CompletableFuture<Void> completableFuture = NexoPlugin.Companion.instance().packGenerator().getPackGenFuture();
            if (completableFuture == null) break block0;
            completableFuture.thenRun(() -> NexoPackServer.sendPack$lambda$1(this, player2));
        }
    }

   default void start() {
   }

   default void stop() {
   }

   @NotNull
   String packUrl();

   @Nullable
   default String hash() {
      return null;
   }

   @Nullable
   default ResourcePackInfo packInfo() {
      String str = this.hash();
      if (str == null) {
         BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
         str = builtPack != null ? builtPack.hash() : null;
         if (str == null) {
            return null;
         }
      }

      String hash = str;
      return ResourcePackInfo.resourcePackInfo().hash(hash).id(UUID.nameUUIDFromBytes(Companion.hashArray(hash))).uri(URI.create(this.packUrl())).build();
   }

   private static void sendPack$lambda$0(AtomicInteger remaining, CompletableFuture future, PlayerConfigurationConnection connection, UUID uuid, ResourcePackStatus status, Audience audience) {
      if (!status.intermediate() && remaining.decrementAndGet() == 0) {
         if (future != null) {
            future.complete((Object)null);
         } else {
            connection.completeReconfiguration();
         }
      }

   }

   private static void sendPack$lambda$1(NexoPackServer nexoPackServer, Player player) {
      NexoPackServer nexoPackServer2 = nexoPackServer;

      Object obj2;
      try {
         int value = 0;
         String str = nexoPackServer2.hash();
         if (str == null) {
            BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
            str = builtPack != null ? builtPack.hash() : null;
            if (str == null) {
               return;
            }
         }

         String hash = str;
         UUID packUUID = UUID.nameUUIDFromBytes(Companion.hashArray(hash));
         URI packUrl = URI.create(nexoPackServer2.packUrl());
         MultipackManager multipackManager = NexoPlugin.Companion.instance().multipackManager();
         ResourcePackInfo[] resourcePackInfos = new ResourcePackInfo[]{ResourcePackInfo.resourcePackInfo(packUUID, packUrl, hash)};
         List allPacks = CollectionsKt.mutableListOf(resourcePackInfos);
         CollectionsKt.addAll((Collection)allPacks, (Iterable)multipackManager.templatePackInfos((OfflinePlayer)player));
         Object obj = ResourcePackRequest.resourcePackRequest().required(Companion.getMandatory() && !player.hasPermission("nexo.resourcepack.bypass")).replace(true).prompt(Companion.getPrompt()).packs((Iterable)allPacks).build();
         ResourcePackRequest request = (ResourcePackRequest)obj;
         player.sendResourcePacks(request);

         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         String name = player.getName();
         Logs.logError("Failed to send resource pack to " + name + ": " + it.getMessage(), false);
      }

   }

   @JvmStatic
   static void initializeServer() {
      Companion.initializeServer();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      public static boolean isPackUploaded(@NotNull NexoPackServer nexoPackServer) {
         return nexoPackServer.isPackUploaded();
      }

      /** @deprecated */
      @Deprecated
      @NotNull
      public static CompletableFuture<Void> uploadPack(@NotNull NexoPackServer nexoPackServer) {
         return nexoPackServer.uploadPack();
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static CompletableFuture<Void> sendPack(@NotNull NexoPackServer nexoPackServer, @NotNull Object connection, boolean reconfigure) {
         return nexoPackServer.sendPack(connection, reconfigure);
      }

      /** @deprecated */
      @Deprecated
      public static void sendPack(@NotNull NexoPackServer nexoPackServer, @NotNull Player player) {
         nexoPackServer.sendPack(player);
      }

      /** @deprecated */
      @Deprecated
      public static void start(@NotNull NexoPackServer nexoPackServer) {
         nexoPackServer.start();
      }

      /** @deprecated */
      @Deprecated
      public static void stop(@NotNull NexoPackServer nexoPackServer) {
         nexoPackServer.stop();
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static String hash(@NotNull NexoPackServer nexoPackServer) {
         return nexoPackServer.hash();
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static ResourcePackInfo packInfo(@NotNull NexoPackServer nexoPackServer) {
         return nexoPackServer.packInfo();
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0010\u001a\u00020\u0011J\f\u0010\u0012\u001a\u00020\u0011H\u0007b\u0002\b\u0013J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0007@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR(\u0010\r\u001a\u00070\u000b¢\u0006\u0002\b\f2\u000b\u0010\u0006\u001a\u00070\u000b¢\u0006\u0002\b\f@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/pack/server/NexoPackServer$Companion;", "", "<init>", "()V", "BYPASS_PERMISSION", "", "value", "", "mandatory", "getMandatory", "()Z", "Lnet/kyori/adventure/text/Component;", "Lorg/jetbrains/annotations/NotNull;", "prompt", "getPrompt", "()Lnet/kyori/adventure/text/Component;", "registerDefaultPackServers", "", "initializeServer", "Lkotlin/jvm/JvmStatic;", "hashArray", "", "hash", "packListener", "Lcom/nexomc/nexo/pack/PackListener;", "com.nexomc:core"}
   )
   public static final class Companion {
      public static final Companion $$INSTANCE = new Companion();
      @NotNull
      public static final String BYPASS_PERMISSION = "nexo.resourcepack.bypass";
      private static boolean mandatory;
      @NotNull
      private static Component prompt;
      @NotNull
      private static final PackListener packListener;

      private Companion() {
      }

      public final boolean getMandatory() {
         return mandatory;
      }

      @NotNull
      public final Component getPrompt() {
         return prompt;
      }

      public final void registerDefaultPackServers() {
         PackServerRegistry.register("SELFHOST", () -> new SelfHostServer());
         PackServerRegistry.register("POLYMATH", () -> new PolymathServer());
         PackServerRegistry.register("HERMES", () -> new HermesServer());
         PackServerRegistry.register("LOBFILE", () -> new LobFileServer());
         PackServerRegistry.register("S3", () -> new S3Server());
         PackServerRegistry.register("NONE", () -> new EmptyServer());
      }

      @JvmStatic
      public final void initializeServer() {
         if (Settings.PACK_GENERATE_PACK.toBool()) {
            NexoPlugin.Companion.instance().packServer().stop();
            HandlerList.unregisterAll(packListener);
            Bukkit.getPluginManager().registerEvents(packListener, (Plugin)NexoPlugin.Companion.instance());
            String str = Settings.PACK_SERVER_TYPE.toString().toUpperCase(Locale.ROOT);
            String type = str;
            NexoPackServer nexoPackServer = PackServerRegistry.create(type);
            if (nexoPackServer == null) {
               Companion companion = this;
               int value = 0;
               Logs.logError("Invalid PackServer type specified: " + type, false);
               Logs.logError("Valid types are: " + CollectionsKt.joinToString((Iterable)PackServerRegistry.INSTANCE.getAllRegisteredTypes(), (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), false);
               nexoPackServer = new EmptyServer();
            }

            NexoPackServer server = nexoPackServer;
            mandatory = Settings.PACK_SEND_MANDATORY.toBool();
            prompt = Settings.PACK_SEND_PROMPT.toComponent();
            Logs.logInfo("PackServer set to " + type);
            NexoPlugin.Companion.instance().packServer(server);
         }
      }

      @NotNull
      public final byte[] hashArray(@NotNull String hash) {
         if (hash.length() % 2 != 0) {
            int value = 0;
            String str = "Hash length must be even";
            throw new IllegalArgumentException(str.toString());
         } else {
            int value2 = 0;
            int value3 = hash.length() / 2;

            byte[] bs;
            for(bs = new byte[value3]; value2 < value3; ++value2) {
               String str2 = hash.substring(value2 * 2, value2 * 2 + 2);
               bs[value2] = (byte)Integer.parseInt(str2, CharsKt.checkRadix(16));
            }

            return bs;
         }
      }

      static {
         mandatory = Settings.PACK_SEND_MANDATORY.toBool();
         prompt = Settings.PACK_SEND_PROMPT.toComponent();
         packListener = new PackListener();
      }
   }
}
