package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.common.collect.ComparisonChain;
import io.papermc.paper.ServerBuildInfo;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.StringCompanionObject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\b\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000  2\n\u0012\u0006\u0012\u0004\u0018\u00010\u00000\u00012\u00020\u0002:\u0001 B\u0011\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006B!\b\u0016\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b¢\u0006\u0004\b\u0005\u0010\u000bJ\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0004H\u0002J\u0006\u0010\u0016\u001a\u00020\u0011J\u0014\u0010\u0019\u001a\u00020\b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0000H\u0096\u0082\u0004J\u0010\u0010\u001b\u001a\u00020\u00112\b\u0010\u001a\u001a\u0004\u0018\u00010\u0000J\u0014\u0010\u001c\u001a\u00020\u00112\b\u0010\u001a\u001a\u0004\u0018\u00010\u001dH\u0096\u0082\u0004J\n\u0010\u001e\u001a\u00020\bH\u0096\u0080\u0004J\n\u0010\u001f\u001a\u00020\u0004H\u0096\u0080\u0004R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\n\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0012R\u0011\u0010\u0015\u001a\u00020\u00048F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/utils/MinecraftVersion;", "", "Ljava/io/Serializable;", "versionOnly", "", "<init>", "(Ljava/lang/String;)V", "major", "", "minor", "build", "(III)V", "getMajor", "()I", "getMinor", "getBuild", "atCurrentOrAbove", "", "Ljava/lang/Boolean;", "parseVersion", "", "version", "atOrAbove", "getVersion", "()Ljava/lang/String;", "compareTo", "other", "isAtLeast", "equals", "", "hashCode", "toString", "Companion", "com.nexomc:core"}
)
public final class MinecraftVersion implements Comparable<MinecraftVersion>, Serializable {
   @NotNull
   public static final Companion Companion = new Companion();
   private final int major;
   private final int minor;
   private final int build;
   @Nullable
   private volatile Boolean atCurrentOrAbove;
   @Nullable
   private static final Class<?> sharedConstants;
   @Nullable
   private static final Class<?> detectedVersionClazz;
   @NotNull
   private static final Lazy<MinecraftVersion> currentVersion$delegate;

   public final int getMajor() {
      return this.major;
   }

   public final int getMinor() {
      return this.minor;
   }

   public final int getBuild() {
      return this.build;
   }

   public MinecraftVersion(@NotNull String versionOnly) {
      super();
      int[] numbers = this.parseVersion(versionOnly);
      this.major = numbers[0];
      this.minor = numbers[1];
      this.build = numbers[2];
   }

   public MinecraftVersion(int major, int minor, int build) {
      this.major = major;
      this.minor = minor;
      this.build = build;
   }

   private final int[] parseVersion(String version) {
      int[] numbers = new int[3];
      int count = 0;
      int num = 0;
      boolean isParsing = false;
      int i = 0;

      for(int j = ((CharSequence)version).length(); i < j; ++i) {
         char c = version.charAt(i);
         if ('0' <= c ? c < ':' : false) {
            num = num * 10 + (c - 48);
            isParsing = true;
         } else {
            if (isParsing) {
               if (count < 3) {
                  numbers[count++] = num;
               }

               num = 0;
               isParsing = false;
            }

            if (c == '-' || count >= 3) {
               break;
            }
         }
      }

      if (isParsing && count < 3) {
         numbers[count] = num;
      }

      return numbers;
   }

   public final boolean atOrAbove() {
      if (this.atCurrentOrAbove == null) {
         this.atCurrentOrAbove = Companion.atOrAbove(this);
      }

      Boolean flag = this.atCurrentOrAbove;
      return flag;
   }

   @NotNull
   public final String getVersion() {
      return this.major + "." + this.minor + "." + this.build;
   }

   public int compareTo(@Nullable MinecraftVersion other) {
      return other == null ? 1 : ComparisonChain.start().compare(this.major, other.major).compare(this.minor, other.minor).compare(this.build, other.build).result();
   }

   public final boolean isAtLeast(@Nullable MinecraftVersion other) {
      if (other == null) {
         return false;
      } else {
         return this.compareTo(other) >= 0;
      }
   }

   public boolean equals(@Nullable Object other) {
      if (other == null) {
         return false;
      } else if (other == this) {
         return true;
      } else {
         return other instanceof MinecraftVersion ? this.major == ((MinecraftVersion)other).major && this.minor == ((MinecraftVersion)other).minor && this.build == ((MinecraftVersion)other).build : false;
      }
   }

   public int hashCode() {
      Object[] objs = new Object[]{this.major, this.minor, this.build};
      return Objects.hash(objs);
   }

   @NotNull
   public String toString() {
      StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
      String str = "(MC: %s)";
      Object[] objs = new Object[]{this.getVersion()};
      String str2 = String.format(str, Arrays.copyOf(objs, objs.length));
      return str2;
   }

   private static final MinecraftVersion currentVersion_delegate$lambda$0() {
      Companion companion = Companion;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(ServerBuildInfo.buildInfo().minecraftVersionName());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      String str = (String)(ResultShim.isFailure(obj) ? null : obj);
      if (str == null) {
         str = Bukkit.getMinecraftVersion();
      }

      String str2 = str;
      return new MinecraftVersion(str2);
   }

   @NotNull
   public static final MinecraftVersion getCurrentVersion() {
      return Companion.getCurrentVersion();
   }

   static {
      Companion companion = Companion;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(Class.forName("net.minecraft.SharedConstants"));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      sharedConstants = (Class)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;

      try {
         int value2 = 0;
         obj = ResultShim.ok(Class.forName("net.minecraft.DetectedVersion"));
      } catch (Throwable ex) {
         obj = ResultShim.ok(ResultKt.createFailure(ex));
      }

      detectedVersionClazz = (Class)(ResultShim.isFailure(obj) ? null : obj);
      currentVersion$delegate = LazyKt.lazy(MinecraftVersion::currentVersion_delegate$lambda$0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\bH\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\u0007\u001a\u00020\b8FX\u0087\u0084\u0002r\u0002\b\u000e¢\u0006\u0012\n\u0004\b\f\u0010\r\u0012\u0004\b\t\u0010\u0003\u001a\u0004\b\n\u0010\u000b¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/utils/MinecraftVersion$Companion;", "", "<init>", "()V", "sharedConstants", "Ljava/lang/Class;", "detectedVersionClazz", "currentVersion", "Lcom/nexomc/nexo/utils/MinecraftVersion;", "getCurrentVersion$annotations", "getCurrentVersion", "()Lcom/nexomc/nexo/utils/MinecraftVersion;", "currentVersion$delegate", "Lkotlin/Lazy;", "Lkotlin/jvm/JvmStatic;", "atOrAbove", "", "version", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final MinecraftVersion getCurrentVersion() {
         Lazy lazy = MinecraftVersion.currentVersion$delegate;
         return (MinecraftVersion)lazy.getValue();
      }

      private final boolean atOrAbove(MinecraftVersion version) {
         return this.getCurrentVersion().isAtLeast(version);
      }
   }
}
