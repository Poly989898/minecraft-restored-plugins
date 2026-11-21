package com.nexomc.nexo.proxy;

import com.nexomc.nexo.utils.JsonBuilder;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J)\u0010\u0015\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004J\n\u0010\u001b\u001a\u00020\u0005HÖ\u0081\u0004R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u000e\u001a\u00020\u000f8F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/proxy/ProxyPackInfo;", "", "uuid", "Ljava/util/UUID;", "unobfuscated", "", "obfuscated", "<init>", "(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V", "getUuid", "()Ljava/util/UUID;", "getUnobfuscated", "()Ljava/lang/String;", "getObfuscated", "asBytes", "", "getAsBytes", "()[B", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class ProxyPackInfo {
   @Nullable
   private final UUID uuid;
   @NotNull
   private final String unobfuscated;
   @NotNull
   private final String obfuscated;

   public ProxyPackInfo(@Nullable UUID uuid, @NotNull String unobfuscated, @NotNull String obfuscated) {
      super();
      this.uuid = uuid;
      this.unobfuscated = unobfuscated;
      this.obfuscated = obfuscated;
   }

   @Nullable
   public final UUID getUuid() {
      return this.uuid;
   }

   @NotNull
   public final String getUnobfuscated() {
      return this.unobfuscated;
   }

   @NotNull
   public final String getObfuscated() {
      return this.obfuscated;
   }

   @NotNull
   public final byte[] getAsBytes() {
      String str = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "uuid", String.valueOf(this.uuid)), "unobfuscated", this.unobfuscated), "obfuscated", this.obfuscated).toString();
      String str2 = str;
      byte[] bytes = str2.getBytes(Charsets.UTF_8);
      return bytes;
   }

   @Nullable
   public final UUID component1() {
      return this.uuid;
   }

   @NotNull
   public final String component2() {
      return this.unobfuscated;
   }

   @NotNull
   public final String component3() {
      return this.obfuscated;
   }

   @NotNull
   public final ProxyPackInfo copy(@Nullable UUID uuid, @NotNull String unobfuscated, @NotNull String obfuscated) {
      return new ProxyPackInfo(uuid, unobfuscated, obfuscated);
   }

   @NotNull
   public String toString() {
      return "ProxyPackInfo(uuid=" + this.uuid + ", unobfuscated=" + this.unobfuscated + ", obfuscated=" + this.obfuscated + ")";
   }

   public int hashCode() {
      int result = this.uuid == null ? 0 : this.uuid.hashCode();
      result = result * 31 + this.unobfuscated.hashCode();
      result = result * 31 + this.obfuscated.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ProxyPackInfo)) {
         return false;
      } else {
         ProxyPackInfo proxyPackInfo = (ProxyPackInfo)other;
         if (!Intrinsics.areEqual(this.uuid, proxyPackInfo.uuid)) {
            return false;
         } else if (!Intrinsics.areEqual(this.unobfuscated, proxyPackInfo.unobfuscated)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.obfuscated, proxyPackInfo.obfuscated);
         }
      }
   }
}
