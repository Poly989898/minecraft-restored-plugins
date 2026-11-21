package com.nexomc.nexo.pack.server;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import kotlin.ExceptionsKt;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010'\u001a\u00020(H\u0016J\b\u0010)\u001a\u00020\u0005H\u0016J\n\u0010\u001d\u001a\u0004\u0018\u00010\u0005H\u0016J\u000e\u0010+\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\u0005X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0014\u0010\n\u001a\u00020\u0005X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007R\u001c\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\u0012X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\u0005X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0007R\u0014\u0010\u0017\u001a\u00020\u0018X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\u00020\u0018X\u0090\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020 \u0018\u00010\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001b\u0010!\u001a\u00020\"8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b%\u0010&\u001a\u0004\b#\u0010$R\u0014\u0010*\u001a\u00020\u00188VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b*\u0010\u001a¨\u0006,"},
   d2 = {"Lcom/nexomc/nexo/pack/server/S3Server;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "bucket", "", "getBucket$com_nexomc_core", "()Ljava/lang/String;", "accessKey", "getAccessKey$com_nexomc_core", "secretKey", "getSecretKey$com_nexomc_core", "region", "Lsoftware/amazon/awssdk/regions/Region;", "kotlin.jvm.PlatformType", "getRegion$com_nexomc_core", "()Lsoftware/amazon/awssdk/regions/Region;", "endpoint", "Ljava/net/URI;", "getEndpoint$com_nexomc_core", "()Ljava/net/URI;", "publicUrl", "getPublicUrl$com_nexomc_core", "pathStyle", "", "getPathStyle$com_nexomc_core", "()Z", "chunkedEncoding", "getChunkedEncoding$com_nexomc_core", "hash", "uploadFuture", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "s3Client", "Lsoftware/amazon/awssdk/services/s3/S3Client;", "getS3Client", "()Lsoftware/amazon/awssdk/services/s3/S3Client;", "s3Client$delegate", "Lkotlin/Lazy;", "stop", "", "packUrl", "isPackUploaded", "uploadPack", "com.nexomc:core"}
)
public class S3Server implements NexoPackServer {
   @NotNull
   private final String bucket;
   @NotNull
   private final String accessKey;
   @NotNull
   private final String secretKey;
   private final Region region;
   @NotNull
   private final URI endpoint;
   @NotNull
   private final String publicUrl;
   private final boolean pathStyle;
   private final boolean chunkedEncoding;
   @Nullable
   private String hash;
   @Nullable
   private CompletableFuture<Void> uploadFuture;
   @NotNull
   private final Lazy s3Client$delegate;

   public S3Server() {
      this.bucket = Settings.S3_BUCKET_NAME.toString();
      this.accessKey = Settings.S3_ACCESS_KEY.toString();
      this.secretKey = Settings.S3_SECRET_KEY.toString();
      Region resolvedRegion;
      try {
         resolvedRegion = Region.of(Settings.S3_REGION.toString("auto"));
      } catch (Throwable e) {
         Logs.logWarn("Failed to find Region for " + Settings.S3_REGION + ", defaulting to " + Region.EU_WEST_1.id(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(e), false);
         }

         resolvedRegion = Region.EU_WEST_1;
      }

      this.region = resolvedRegion;
      URI var14 = URI.create(Settings.S3_ENDPOINT_URL.toString());
      Intrinsics.checkNotNullExpressionValue(var14, "create(...)");
      this.endpoint = var14;
      this.publicUrl = Settings.S3_PUBLIC_URL.toString();
      this.pathStyle = Settings.S3_PATH_STYLE.toBool();
      this.chunkedEncoding = Settings.S3_CHUNKED_ENCODING.toBool();
      this.s3Client$delegate = LazyKt.lazy((() -> S3Server.s3Client_delegate$lambda$0(this)));
   }

   @NotNull
   public String getBucket$com_nexomc_core() {
      return this.bucket;
   }

   @NotNull
   public String getAccessKey$com_nexomc_core() {
      return this.accessKey;
   }

   @NotNull
   public String getSecretKey$com_nexomc_core() {
      return this.secretKey;
   }

   public Region getRegion$com_nexomc_core() {
      return this.region;
   }

   @NotNull
   public URI getEndpoint$com_nexomc_core() {
      return this.endpoint;
   }

   @NotNull
   public String getPublicUrl$com_nexomc_core() {
      return this.publicUrl;
   }

   public boolean getPathStyle$com_nexomc_core() {
      return this.pathStyle;
   }

   public boolean getChunkedEncoding$com_nexomc_core() {
      return this.chunkedEncoding;
   }

   @NotNull
   public final S3Client getS3Client() {
      Lazy var1 = this.s3Client$delegate;
      Object var10000 = var1.getValue();
      Intrinsics.checkNotNullExpressionValue(var10000, "getValue(...)");
      return (S3Client)var10000;
   }

   public void stop() {
      NexoPackServer.super.stop();
      this.getS3Client().close();
   }

   @NotNull
   public String packUrl() {
      CompletableFuture var10000 = this.uploadFuture;
      if (!(var10000 != null ? var10000.isDone() : false)) {
         return "";
      } else {
         String var4 = this.hash;
         if (var4 == null) {
            return "";
         } else {
            String currentHash = var4;
            String key = Settings.S3_UNIQUE_KEY.toString("resourcepacks/" + currentHash + ".zip");
            String base = ((CharSequence)this.getPublicUrl$com_nexomc_core()).length() > 0 ? this.getPublicUrl$com_nexomc_core() : (this.getPathStyle$com_nexomc_core() ? this.getEndpoint$com_nexomc_core() + "/" + this.getBucket$com_nexomc_core() : StringUtilsKt.prependIfMissing(this.getBucket$com_nexomc_core() + "." + this.getEndpoint$com_nexomc_core(), "https://"));
            return base + "/" + key;
         }
      }
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
      BuiltResourcePack var10000 = NexoPlugin.Companion.instance().packGenerator().builtPack();
      Intrinsics.checkNotNull(var10000);
      BuiltResourcePack builtPack = var10000;
      if (!Intrinsics.areEqual(this.hash, builtPack.hash())) {
         CompletableFuture var2 = this.uploadFuture;
         if (var2 != null) {
            var2.cancel(true);
         }

         this.uploadFuture = null;
      }

      if (this.uploadFuture == null) {
         this.uploadFuture = CompletableFuture.runAsync((() -> S3Server.uploadPack$lambda$0(this, builtPack)));
      }

      CompletableFuture var3 = this.uploadFuture;
      Intrinsics.checkNotNull(var3);
      return var3;
   }

   private static final S3Client s3Client_delegate$lambda$0(S3Server this$0) {
      return (S3Client)((S3ClientBuilder)((S3ClientBuilder)((S3ClientBuilder)((S3ClientBuilder)S3Client.builder().endpointOverride(this$0.getEndpoint$com_nexomc_core())).credentialsProvider((AwsCredentialsProvider)StaticCredentialsProvider.create((AwsCredentials)AwsBasicCredentials.create(this$0.getAccessKey$com_nexomc_core(), this$0.getSecretKey$com_nexomc_core())))).region(this$0.getRegion$com_nexomc_core())).serviceConfiguration((S3Configuration)S3Configuration.builder().pathStyleAccessEnabled(this$0.getPathStyle$com_nexomc_core()).chunkedEncodingEnabled(this$0.getChunkedEncoding$com_nexomc_core()).build())).build();
   }

   private static final void uploadPack$lambda$0$0$0$0(S3Server $this_runCatching, String $key, HeadObjectRequest.Builder it) {
      it.bucket($this_runCatching.getBucket$com_nexomc_core()).key($key);
   }

   private static final void uploadPack$lambda$0(S3Server this$0, BuiltResourcePack $builtPack) {
      S3Server var2 = this$0;

      Object $this$uploadPack_u24lambda_u240_u240;
      try {
         String key;
         boolean var19;
         block: {
            int var4 = 0;
            var2.hash = $builtPack.hash();
            key = Settings.S3_UNIQUE_KEY.toString("resourcepacks/" + var2.hash + ".zip");
            if (Settings.S3_UNIQUE_KEY.getValue() == null) {
               try {
                  var2.getS3Client().headObject(HeadObjectRequest.builder().bucket(var2.getBucket$com_nexomc_core()).key(key).build());
                  var19 = true;
                  break block;
               } catch (Throwable ignored) {
               }
            }

            var19 = false;
         }

         boolean exists = var19;
         if (exists) {
            Logs.logInfo("ResourcePack with hash '" + $builtPack.hash() + "' already exists on the S3-Server. Skipping upload...");
         } else {
            PutObjectRequest putRequest = (PutObjectRequest)PutObjectRequest.builder().bucket(var2.getBucket$com_nexomc_core()).key(key).acl(ObjectCannedACL.PUBLIC_READ).build();
            var2.getS3Client().putObject(putRequest, RequestBody.fromBytes($builtPack.data().toByteArray()));
            PutObjectRequest putRequestHash = (PutObjectRequest)PutObjectRequest.builder().bucket(var2.getBucket$com_nexomc_core()).key("hashes/" + $builtPack.hash()).acl(ObjectCannedACL.PUBLIC_READ).build();
            var2.getS3Client().putObject(putRequestHash, RequestBody.fromString($builtPack.hash()));
            Logs.logInfo("Uploaded resource pack to S3-Server with key: " + key);
         }

         $this$uploadPack_u24lambda_u240_u240 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable var11) {
         $this$uploadPack_u24lambda_u240_u240 = ResultShim.ok(ResultKt.createFailure(var11));
      }

      Throwable var20 = ResultShim.exceptionOrNull($this$uploadPack_u24lambda_u240_u240);
      if (var20 != null) {
         Throwable it = var20;
         int var13 = 0;
         Logs.logError("Failed to upload resource pack to S3: " + it.getMessage(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }
      }

   }
}
