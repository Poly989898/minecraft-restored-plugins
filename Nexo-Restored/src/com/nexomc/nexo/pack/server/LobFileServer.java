package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
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
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0016J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/pack/server/LobFileServer;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "packUrl", "", "hash", "packUUID", "Ljava/util/UUID;", "uploadFuture", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "uploadPack", "isPackUploaded", "", "()Z", "com.nexomc:core"}
)
public final class LobFileServer implements NexoPackServer {
   @Nullable
   private String packUrl;
   @Nullable
   private String hash;
   @Nullable
   private UUID packUUID;
   @Nullable
   private CompletableFuture<Void> uploadFuture;

   @NotNull
   public CompletableFuture<Void> uploadPack() {
      String str = this.hash;
      BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
      if (!Intrinsics.areEqual(str, builtPack.hash())) {
         CompletableFuture completableFuture = this.uploadFuture;
         if (completableFuture != null) {
            completableFuture.cancel(true);
         }

         this.uploadFuture = null;
      }

      if (this.uploadFuture == null) {
         this.uploadFuture = CompletableFuture.runAsync((() -> LobFileServer.uploadPack$lambda$0(this)));
      }

      CompletableFuture completableFuture2 = this.uploadFuture;
      return completableFuture2;
   }

   @NotNull
   public String packUrl() {
      String str = this.packUrl;
      if (str == null) {
         str = "";
      }

      return str;
   }

   @Nullable
   public String hash() {
      return this.hash;
   }

   public boolean isPackUploaded() {
      CompletableFuture packGenFuture = NexoPlugin.Companion.instance().packGenerator().getPackGenFuture();
      boolean flag;
      if (!(packGenFuture != null ? !packGenFuture.isDone() : false)) {
         CompletableFuture completableFuture = this.uploadFuture;
         if (!(completableFuture != null ? !completableFuture.isDone() : false)) {
            flag = true;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   private static final void uploadPack$lambda$0(LobFileServer lobFileServer2) {
      LobFileServer lobFileServer = lobFileServer2;

      Object obj2;
      try {
         LobFileServer lobFileServer3 = lobFileServer;
         int value = 0;
         Closeable createDefault = (Closeable)HttpClients.createDefault();
         Throwable throwable = null;

         block: {
            try {
               CloseableHttpClient httpClient = (CloseableHttpClient)createDefault;
               int value2 = 0;
               HttpPost request = new HttpPost("https://lobfile.com/api/v3/upload");
               BuiltResourcePack builtPack = NexoPlugin.Companion.instance().packGenerator().builtPack();
               byte[] toByteArray = builtPack.data().toByteArray();
               byte[] packData = toByteArray;
               BuiltResourcePack builtResourcePack = NexoPlugin.Companion.instance().packGenerator().builtPack();
               String str = builtResourcePack.hash();
               String hash = str;
               HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("file", packData, ContentType.DEFAULT_BINARY, "pack.zip").addTextBody("sha_1", hash).build();
               request.setEntity(httpEntity);
               request.setHeader("X-API-Key", Settings.LOBFILE_API_KEY.toString());
               CloseableHttpResponse response = httpClient.execute((ClassicHttpRequest)request);
               String responseString = EntityUtils.toString(response.getEntity());

               Object obj3;
               try {
                  int value3 = 0;
                  obj3 = ResultShim.ok(JsonParser.parseString(responseString).getAsJsonObject());
               } catch (Throwable e) {
                  obj3 = ResultShim.ok(ResultKt.createFailure(e));
               }

               Object obj = obj3;
               Throwable throwable2 = ResultShim.exceptionOrNull(obj3);
               if (throwable2 != null) {
                  Throwable it = throwable2;
                  int value4 = 0;
                  Logs.logError("Malformed response from LobFile.", false);
                  if (Settings.DEBUG.toBool()) {
                     Logs.logWarn(responseString, false);
                     it.printStackTrace();
                  } else {
                     String message = it.getMessage();
                     Logs.logWarn(message, false);
                  }
               }

               JsonObject jsonObject = (JsonObject)(ResultShim.isFailure(obj) ? null : obj);
               if (jsonObject != null) {
                  JsonObject jsonOutput = jsonObject;
                  if (jsonOutput.has("success") && jsonOutput.get("success").getAsBoolean()) {
                     lobFileServer3.packUrl = jsonOutput.get("url").getAsString();
                     lobFileServer3.hash = hash;
                     byte[] bytes = hash.getBytes(Charsets.UTF_8);
                     lobFileServer3.packUUID = UUID.nameUUIDFromBytes(bytes);
                     Logs.logSuccess("ResourcePack uploaded: " + lobFileServer3.packUrl, false);
                     return;
                  }

                  jsonOutput.get("error");
                  Logs.logError("Upload failed: " + (jsonObject != null ? ((JsonElement)jsonObject).getAsString() : null), false);
                  Unit uploadResult = Unit.INSTANCE;
                  break block;
               }
            } catch (Throwable ex) {
               throwable = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(createDefault, throwable);
            }

            return;
         }

         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex2) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex2));
      }

      Throwable throwable3 = ResultShim.exceptionOrNull(obj2);
      if (throwable3 != null) {
         Throwable it = throwable3;
         int value5 = 0;
         Logs.logError("ResourcePack upload to LobFile failed.", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String str2 = it.getMessage();
            Logs.logWarn(str2, false);
         }
      }

   }
}
