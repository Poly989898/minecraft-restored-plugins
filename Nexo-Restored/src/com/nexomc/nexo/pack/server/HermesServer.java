package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.PackGenerator;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\n\u0010\u0007\u001a\u0004\u0018\u00010\u0005H\u0016J\u000e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J\u001a\u0010\u000f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\u000e\u0010\u0019\u001a\u0004\u0018\u00010\u001a*\u00020\u001bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\u00020\f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\r¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/pack/server/HermesServer;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "serverAddress", "", "packUrl", "hash", "uploadFuture", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "isPackUploaded", "", "()Z", "uploadPack", "ensure", "packHash", "data", "", "syncTemplate", "", "exists", "upload", "packGenerator", "Lcom/nexomc/nexo/pack/PackGenerator;", "jsonObject", "Lcom/google/gson/JsonObject;", "Lorg/apache/hc/core5/http/ClassicHttpResponse;", "com.nexomc:core"}
)
public final class HermesServer implements NexoPackServer {
   @NotNull
   private final String serverAddress;
   @Nullable
   private String packUrl;
   @Nullable
   private String hash;
   @Nullable
   private CompletableFuture<Void> uploadFuture;

   public HermesServer() {
      this.serverAddress = StringUtilsKt.appendIfMissing(StringUtilsKt.prependIfMissing(Settings.HERMES_SERVER.toString("hermes.nexomc.com"), "https://"), "/");
   }

   @NotNull
   public String packUrl() {
      String var10000 = this.packUrl;
      if (var10000 == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Nullable
   public String hash() {
      return this.hash;
   }

   public boolean isPackUploaded() {
      CompletableFuture var10000 = this.packGenerator().getPackGenFuture();
      boolean var2;
      if (!(var10000 != null ? !var10000.isDone() : false)) {
         CompletableFuture var1 = this.uploadFuture;
         if (!(var1 != null ? !var1.isDone() : false)) {
            var2 = true;
            return var2;
         }
      }

      var2 = false;
      return var2;
   }

   @NotNull
   public CompletableFuture<Void> uploadPack() {
      BuiltResourcePack var10000 = this.packGenerator().builtPack();
      Intrinsics.checkNotNull(var10000);
      String var18 = var10000.hash();
      Intrinsics.checkNotNullExpressionValue(var18, "hash(...)");
      String builtHash = var18;
      if (!Intrinsics.areEqual(this.hash, builtHash)) {
         CompletableFuture var19 = this.uploadFuture;
         if (var19 != null) {
            var19.cancel(true);
         }

         this.uploadFuture = null;
      }

      if (this.uploadFuture == null) {
         Map<String, byte[]> $this$map$iv = NexoPlugin.Companion.instance().multipackManager().getPackArrays();
         Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.size()));

         for (Map.Entry<String, byte[]> item$iv$iv : $this$map$iv.entrySet()) {
            int var11 = 0;
            String templateHash = (String)item$iv$iv.getKey();
            byte[] templateData = (byte[])item$iv$iv.getValue();
            destination$iv$iv.add(CompletableFuture.runAsync((() -> HermesServer.uploadPack$lambda$0$0(this, templateHash, templateData))));
         }

         Collection $this$toTypedArray$iv = (Collection)((List)destination$iv$iv);
         CompletableFuture[] var2 = (CompletableFuture[])$this$toTypedArray$iv.toArray(new CompletableFuture[0]);
         this.uploadFuture = CompletableFuture.allOf((CompletableFuture[])Arrays.copyOf(var2, var2.length)).thenRunAsync((() -> HermesServer.uploadPack$lambda$1(this, builtHash)));
      }

      CompletableFuture var20 = this.uploadFuture;
      Intrinsics.checkNotNull(var20);
      return var20;
   }

   private final String ensure(String packHash, byte[] data) throws java.io.IOException {
      String url = this.exists(packHash);
      if (url == null) {
         url = this.upload(packHash, data);
      }

      return url;
   }

   private final void syncTemplate(String packHash, byte[] data) {
      try {
         this.ensure(packHash, data);
      } catch (Throwable it) {
         Logs.logError("Failed to sync template pack (" + StringsKt.take(packHash, 8) + "...) to Hermes", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }
      }
   }

   private final String exists(String packHash) throws java.io.IOException {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
         HttpGet request = new HttpGet(this.serverAddress + "v1/exists/" + packHash);
         return client.execute(request, response -> exists$lambda$0$0(this, response));
      }
   }

   private final String upload(String packHash, byte[] data) throws java.io.IOException {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
         HttpPost request = new HttpPost(this.serverAddress + "v1/upload/" + packHash);
         request.setEntity(new ByteArrayEntity(data, ContentType.APPLICATION_OCTET_STREAM));
         return client.execute(request, response -> upload$lambda$0$1(this, response));
      }
   }

   private final PackGenerator packGenerator() {
      return NexoPlugin.Companion.instance().packGenerator();
   }

   private final JsonObject jsonObject(ClassicHttpResponse response) {
      try {
         return JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
      } catch (Throwable e) {
         return null;
      }
   }

   private static final void uploadPack$lambda$0$0(HermesServer this$0, String $templateHash, byte[] $templateData) {
      this$0.syncTemplate($templateHash, $templateData);
   }

   private static final void uploadPack$lambda$1(HermesServer this$0, String $builtHash) {
      String url = null;
      Throwable failure = null;
      try {
         BuiltResourcePack builtPack = this$0.packGenerator().builtPack();
         Intrinsics.checkNotNull(builtPack);
         byte[] data = builtPack.data().toByteArray();
         Intrinsics.checkNotNullExpressionValue(data, "toByteArray(...)");
         url = this$0.ensure($builtHash, data);
      } catch (Throwable e) {
         failure = e;
      }

      if (failure == null) {
         if (url == null) {
            Logs.logError("The resource pack was not accepted by Hermes.", false);
         } else {
            this$0.packUrl = url;
            this$0.hash = $builtHash;
            Logs.logSuccess("ResourcePack is ready on Hermes at " + url, false);
         }
      } else {
         Logs.logError("The resource pack was not uploaded to Hermes.", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(failure), false);
         } else {
            String message = failure.getMessage();
            if (message == null) {
               message = "";
            }

            Logs.logWarn(message, false);
         }
      }
   }

   private static final String exists$lambda$0$0(HermesServer this$0, ClassicHttpResponse response) {
      if (response.getCode() != 200) {
         return null;
      }

      JsonObject json = this$0.jsonObject(response);
      if (json == null) {
         return null;
      }

      JsonElement exists = json.get("exists");
      if (exists == null || !exists.getAsBoolean()) {
         return null;
      }

      JsonElement url = json.get("url");
      return url != null ? url.getAsString() : null;
   }

   private static final String upload$lambda$0$1(HermesServer this$0, ClassicHttpResponse response) {
      Intrinsics.checkNotNull(response);
      JsonObject var10000 = this$0.jsonObject(response);
      if (var10000 == null) {
         return null;
      } else {
         JsonObject json = var10000;
         JsonElement var7 = json.get("url");
         String var8;
         if (var7 != null) {
            var8 = var7.getAsString();
            if (var8 != null) {
               return var8;
            }
         }

         int var4 = 0;
         JsonElement var9 = json.get("error");
         if (var9 != null) {
            String var10 = var9.getAsString();
            if (var10 != null) {
               String it = var10;
               int var6 = 0;
               Logs.logError("Hermes: " + it, false);
            }
         }

         var8 = null;
         return var8;
      }
   }
}
