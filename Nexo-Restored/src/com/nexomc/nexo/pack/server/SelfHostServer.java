package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.logs.Logs;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.creative.server.handler.ResourcePackRequestHandler;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u000e\u001a\u00020\u0007H\u0016J\n\u0010\b\u001a\u0004\u0018\u00010\u0007H\u0016J\u000e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0016H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00020\u00108VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0011¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/pack/server/SelfHostServer;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "packServer", "Lteam/unnamed/creative/server/ResourcePackServer;", "publicAddress", "", "hash", "builtPackArray", "", "cachedHash", "handler", "Lteam/unnamed/creative/server/handler/ResourcePackRequestHandler;", "packUrl", "isPackUploaded", "", "()Z", "uploadPack", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "start", "", "stop", "com.nexomc:core"}
)
public final class SelfHostServer implements NexoPackServer {
   @Nullable
   private ResourcePackServer packServer;
   @NotNull
   private final String publicAddress = this.publicAddress();
   @Nullable
   private String hash;
   @Nullable
   private byte[] builtPackArray;
   @Nullable
   private String cachedHash;
   @NotNull
   private final ResourcePackRequestHandler handler = ((a0_, a1_) -> SelfHostServer.handler$lambda$0(this, a0_, a1_));

   public SelfHostServer() {
      SelfHostServer selfHostServer = this;

      Object obj;
      try {
         SelfHostServer selfHostServer2 = selfHostServer;
         int value = 0;
         selfHostServer2.packServer = ResourcePackServer.server().address(Settings.SELFHOST_PACK_SERVER_PORT.toInt(8082)).executor((Executor)Executors.newFixedThreadPool(Settings.SELFHOST_DISPATCH_THREADS.toInt(10))).handler(selfHostServer2.handler).build();
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String message = it.getMessage();
            Logs.logWarn(message, true);
         }

         Logs.logError("Failed to start Nexo pack-server", false);
      }

   }

   @NotNull
   public String packUrl() {
      BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
      this.hash = builtPack.hash();
      int serverPort = Settings.SELFHOST_PACK_SERVER_PORT.toInt(8082);
      String str = this.publicAddress;
      int value = 0;
      String str2 = StringsKt.startsWith(this.publicAddress, "http", false) ? str : null;
      if (str2 == null) {
         str2 = "http://" + this.publicAddress + ":" + serverPort;
      }

      String address = str2;
      return address + "/" + this.hash + ".zip";
   }

   @Nullable
   public String hash() {
      return this.hash;
   }

   public boolean isPackUploaded() {
      return true;
   }

   @NotNull
   public CompletableFuture<Void> uploadPack() {
      if (Settings.DEBUG.toBool()) {
         Logs.logSuccess("Resourcepack uploaded and will be dispatched with publicAddress " + this.packUrl(), false);
      } else {
         Logs.logSuccess("Resourcepack has been uploaded to SelfHost!", false);
      }

      CompletableFuture completableFuture = CompletableFuture.completedFuture((Object)null);
      return completableFuture;
   }

   public void start() {
      if (this.packServer != null) {
         Logs.logSuccess("Started Self-Host Pack-Server...", false);
         ResourcePackServer resourcePackServer = this.packServer;
         resourcePackServer.start();
      }
   }

   public void stop() {
      if (this.packServer != null) {
         Logs.logError("Stopping Self-Host Pack-Server...", false);
         ResourcePackServer resourcePackServer = this.packServer;
         resourcePackServer.stop(0);
         this.packServer = null;
      }
   }

   private final String publicAddress() {
      String configured = Settings.SELFHOST_PUBLIC_ADDRESS.toString();
      if (!configured.isEmpty()) {
         return configured;
      }

      String detected = null;
      try {
         java.net.URL url = java.net.URI.create("http://checkip.amazonaws.com/").toURL();
         try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream()))) {
            detected = reader.readLine();
         }
      } catch (Throwable t) {
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(t), false);
         }

         Logs.logError("Failed to get publicAddress for SELFHOST server...", false);
         Logs.logWarn("You can manually set it in `settings.yml` at", false);
      }

      return detected != null ? detected : "0.0.0.0";
   }

   private static final void handler$lambda$0(SelfHostServer selfHostServer, ResourcePackDownloadRequest resourcePackDownloadRequest, HttpExchange exchange) {
      try {
      String path = exchange.getRequestURI().getPath();
      String requestHash = StringsKt.removeSuffix(StringsKt.removePrefix(path, (CharSequence)"/"), (CharSequence)".zip");
      byte[] multipackData = (byte[])NexoPlugin.Companion.instance().multipackManager().getPackArrays().get(requestHash);
      byte[] bs = multipackData;
      if (multipackData == null) {
         int value = 0;
         BuiltResourcePack builtResourcePack = NexoPlugin.Companion.instance().packGenerator().builtPack();
         BuiltResourcePack builtPack = builtResourcePack;
         if (selfHostServer.builtPackArray == null || !Intrinsics.areEqual(selfHostServer.cachedHash, builtPack.hash())) {
            selfHostServer.builtPackArray = builtPack.data().toByteArray();
            selfHostServer.cachedHash = builtPack.hash();
         }

         bs = selfHostServer.builtPackArray;
      }

      byte[] packData = bs;
      Headers responseHeaders = exchange.getResponseHeaders();
      Map map = (Map)responseHeaders;
      String str = "Content-Type";
      List list = CollectionsKt.listOf("application/zip");
      map.put(str, list);
      exchange.sendResponseHeaders(200, (long)packData.length);
      exchange.getResponseBody().write(packData);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }
}
