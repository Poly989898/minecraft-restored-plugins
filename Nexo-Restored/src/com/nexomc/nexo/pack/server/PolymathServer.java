package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\n\u0010\u0007\u001a\u0004\u0018\u00010\u0005H\u0016J\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0016J\u001e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0010\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0014H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000f¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/pack/server/PolymathServer;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "serverAddress", "", "packUrl", "hash", "packUUID", "Ljava/util/UUID;", "uploadFuture", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "isPackUploaded", "", "()Z", "uploadPack", "uploadMultipack", "packHash", "packData", "", "upload", "data", "com.nexomc:core"}
)
public final class PolymathServer implements NexoPackServer {
   @NotNull
   private final String serverAddress;
   @Nullable
   private String packUrl;
   @Nullable
   private String hash;
   @Nullable
   private UUID packUUID;
   @Nullable
   private CompletableFuture<Void> uploadFuture;

   public PolymathServer() {
      this.serverAddress = StringUtilsKt.appendIfMissing(StringUtilsKt.prependIfMissing(Settings.POLYMATH_SERVER.toString("atlas.nexomc.com"), "https://"), "/");
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
      CompletableFuture var10000 = NexoPlugin.Companion.instance().packGenerator().getPackGenFuture();
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
      String var10000 = this.hash;
      BuiltResourcePack var10001 = NexoPlugin.Companion.instance().packGenerator().builtPack();
      Intrinsics.checkNotNull(var10001);
      if (!Intrinsics.areEqual(var10000, var10001.hash())) {
         CompletableFuture var17 = this.uploadFuture;
         if (var17 != null) {
            var17.cancel(true);
         }

         this.uploadFuture = null;
      }

      if (this.uploadFuture == null) {
         Map<String, byte[]> $this$map$iv = NexoPlugin.Companion.instance().multipackManager().getPackArrays();
         Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.size()));

         for (Map.Entry<String, byte[]> item$iv$iv : $this$map$iv.entrySet()) {
            int var10 = 0;
            String packHash = (String)item$iv$iv.getKey();
            byte[] packData = (byte[])item$iv$iv.getValue();
            destination$iv$iv.add(this.uploadMultipack(packHash, packData));
         }

         Collection $this$toTypedArray$iv = (Collection)((List)destination$iv$iv);
         CompletableFuture[] var1 = (CompletableFuture[])$this$toTypedArray$iv.toArray(new CompletableFuture[0]);
         this.uploadFuture = CompletableFuture.allOf((CompletableFuture[])Arrays.copyOf(var1, var1.length)).thenRunAsync((() -> PolymathServer.uploadPack$lambda$1(this)));
      }

      CompletableFuture var18 = this.uploadFuture;
      Intrinsics.checkNotNull(var18);
      return var18;
   }

   private final CompletableFuture<Void> uploadMultipack(String packHash, byte[] packData) {
      CompletableFuture var10000 = CompletableFuture.runAsync((() -> PolymathServer.uploadMultipack$lambda$0(packHash, this, packData)));
      Intrinsics.checkNotNullExpressionValue(var10000, "runAsync(...)");
      return var10000;
   }

   private final String upload(byte[] data) throws java.io.IOException {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
         HttpPost request = new HttpPost(this.serverAddress + "upload");
         HttpEntity httpEntity = MultipartEntityBuilder.create().addTextBody("id", Settings.POLYMATH_SECRET.toString()).addPart("pack", (ContentBody)(new ByteArrayBody(data, "pack"))).build();
         request.setEntity(httpEntity);
         String response = EntityUtils.toString(client.execute((ClassicHttpRequest)request).getEntity());
         Intrinsics.checkNotNullExpressionValue(response, "use(...)");
         return response;
      } catch (org.apache.hc.core5.http.ParseException e) {
         throw new java.io.IOException(e);
      }
   }

   private static final void uploadPack$lambda$1(PolymathServer this$0) {
      try {
         BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
         Intrinsics.checkNotNull(builtPack);
         byte[] data = builtPack.data().toByteArray();
         Intrinsics.checkNotNullExpressionValue(data, "toByteArray(...)");
         String responseString = this$0.upload(data);
         JsonObject jsonOutput = null;
         try {
            jsonOutput = JsonParser.parseString(responseString).getAsJsonObject();
         } catch (Throwable it) {
            Logs.logError("The resource pack could not be uploaded due to a malformed response.", false);
            Logs.logWarn("This is usually due to the resourcepack server being down.", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(responseString, false);
               it.printStackTrace();
            } else {
               String message = it.getMessage();
               Intrinsics.checkNotNull(message);
               Logs.logWarn(message, false);
            }
         }

         if (jsonOutput == null) {
            return;
         }

         if (!jsonOutput.has("url") || !jsonOutput.has("sha1")) {
            if (jsonOutput.has("error")) {
               Logs.logError("Error: " + jsonOutput.get("error").getAsString(), false);
            }

            Logs.logError("Response: " + jsonOutput, false);
            Logs.logError("The resource pack has not been uploaded to the server. Usually this is due to an excessive size.", false);
            return;
         }

         this$0.packUrl = jsonOutput.get("url").getAsString();
         this$0.hash = jsonOutput.get("sha1").getAsString();
         String uploadedHash = this$0.hash;
         Intrinsics.checkNotNull(uploadedHash);
         byte[] hashBytes = uploadedHash.getBytes(Charsets.UTF_8);
         Intrinsics.checkNotNullExpressionValue(hashBytes, "getBytes(...)");
         this$0.packUUID = UUID.nameUUIDFromBytes(hashBytes);
         Logs.logSuccess("ResourcePack has been uploaded to " + this$0.packUrl, false);
      } catch (Throwable it) {
         Logs.logError("The resource pack has not been uploaded to the server. Usually this is due to an excessive size.", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String message = it.getMessage();
            Intrinsics.checkNotNull(message);
            Logs.logWarn(message, false);
         }
      }
   }

   private static final void uploadMultipack$lambda$0(String $packHash, PolymathServer this$0, byte[] $packData) {
      String shortHash = StringsKt.take($packHash, 8);

      try {
         String response = this$0.upload($packData);
         JsonObject jsonOutput = null;
         try {
            jsonOutput = JsonParser.parseString(response).getAsJsonObject();
         } catch (Throwable ignored) {
         }

         if (jsonOutput != null) {
            if (jsonOutput.has("url") && jsonOutput.has("sha1")) {
               return;
            }

            if (jsonOutput.has("error")) {
               Logs.logError("Error: " + jsonOutput.get("error").getAsString(), false);
            }

            Logs.logError("Multipack (" + shortHash + "...) failed to upload to Polymath", false);
         }
      } catch (Throwable it) {
         Logs.logError("Failed to upload multipack (" + shortHash + "...) to Polymath", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String message = it.getMessage();
            Intrinsics.checkNotNull(message);
            Logs.logWarn(message, false);
         }
      }
   }
}
