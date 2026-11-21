package com.nexomc.nexo.utils;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.ArraysKt;
import kotlin.enums.EnumEntries;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001.B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\t\u001a\u00020\u0006*\u00020\bJ\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0006J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eJ\u0014\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000eH\u0007b\u0002\b\u0013J\u000e\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000eJ\u0006\u0010-\u001a\u00020\u000bR \u0010\u0004\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000b0\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u0015\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0015\u0010\u0016R\u001b\u0010\u0019\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u0018\u001a\u0004\b\u0019\u0010\u0016R\u001b\u0010\u001b\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001c\u0010\u0018\u001a\u0004\b\u001b\u0010\u0016R\u001b\u0010\u001d\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u0018\u001a\u0004\b\u001d\u0010\u0016R\u001b\u0010\u001f\u001a\u00020\u000b8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b \u0010\u0018\u001a\u0004\b\u001f\u0010\u0016R\u001a\u0010!\u001a\u00020\"X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010'\u001a\u00020(X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,¨\u0006/"},
   d2 = {"Lcom/nexomc/nexo/utils/VersionUtil;", "", "<init>", "()V", "versionMap", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "Lcom/nexomc/nexo/utils/VersionUtil$NMSVersion;", "", "Lcom/nexomc/nexo/utils/MinecraftVersion;", "toNMS", "matchesServer", "", "version", "server", "", "versionCache", "Ljava/util/concurrent/ConcurrentHashMap;", "atleast", "versionString", "Lkotlin/jvm/JvmStatic;", "below", "isFoliaServer", "()Z", "isFoliaServer$delegate", "Lkotlin/Lazy;", "isCompiled", "isCompiled$delegate", "isDevBuild", "isDevBuild$delegate", "isCI", "isCI$delegate", "isValidCompiler", "isValidCompiler$delegate", "lastCheck", "", "getLastCheck$com_nexomc_core", "()J", "setLastCheck$com_nexomc_core", "(J)V", "leaked", "Lcom/nexomc/nexo/utils/TriState;", "getLeaked$com_nexomc_core", "()Lcom/nexomc/nexo/utils/TriState;", "setLeaked$com_nexomc_core", "(Lcom/nexomc/nexo/utils/TriState;)V", "isLeaked", "NMSVersion", "com.nexomc:core"}
)
public final class VersionUtil {
   @NotNull
   public static final VersionUtil INSTANCE = new VersionUtil();
   @NotNull
   private static final Object2ObjectLinkedOpenHashMap<NMSVersion, MinecraftVersion[]> versionMap = new Object2ObjectLinkedOpenHashMap();
   @NotNull
   private static final ConcurrentHashMap<String, Boolean> versionCache;
   @NotNull
   private static final Lazy isFoliaServer$delegate;

   private VersionUtil() {
   }

   @NotNull
   public final NMSVersion toNMS(@NotNull MinecraftVersion minecraftVersion) {
      java.lang.Object obj;
      Set entrySet = versionMap.entrySet();
      Iterable iterable = (Iterable)entrySet;
      Iterator iterator = iterable.iterator();

      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            Map.Entry it = (Map.Entry)obj2;
            int value = 0;
            Object obj3 = it.getValue();
            if (!ArraysKt.contains((MinecraftVersion[])obj3, minecraftVersion)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      NMSVersion version = (Map.Entry)obj != null ? (NMSVersion)((Map.Entry)obj).getKey() : null;
      if (version != null) {
         return version;
      } else {
         ObjectSortedSet it = (ObjectSortedSet)versionMap.entrySet();
         int bumped = 0;
         Object obj4 = ((Map.Entry)it.getFirst()).getValue();
         obj4 = ArraysKt.last((MinecraftVersion[])obj4);
         Object obj5 = ((Map.Entry)it.getLast()).getValue();
         Pair pair = kotlin.TuplesKt.to(obj4, ArraysKt.first((MinecraftVersion[])obj5));
         MinecraftVersion newest = (MinecraftVersion)pair.component1();
         MinecraftVersion oldest = (MinecraftVersion)pair.component2();
         boolean flag;
         if (minecraftVersion.compareTo(newest) > 0) {
            Logs.logWarn("Nexo v" + NexoPlugin.Companion.instance().getPluginMeta().getVersion() + " only officially supports up to Server-Version (" + newest.getVersion() + ")", false);
            Logs.logWarn("Your server is currently running v" + minecraftVersion.getVersion() + ".", false);
            Logs.logWarn("Nexo will attempt to automatically support this version, but there might be issues", false);
            Logs.logWarn("Recommended to ensure you are using the latest Nexo release for optimal compatibility", false);
            return this.toNMS(newest);
         } else if (minecraftVersion.compareTo(oldest) < 0) {
            Logs.logWarn("Nexo v" + NexoPlugin.Companion.instance().getPluginMeta().getVersion() + " only officially supports Server-Version v" + oldest.getVersion() + "+", false);
            Logs.logWarn("Your server is currently running v" + minecraftVersion.getVersion() + ".", false);
            Logs.logWarn("Nexo will attempt to automatically support this version, but there might be issues", false);
            Logs.logWarn("Recommended to update your server to a more modern version for optimal compatibility", false);
            return this.toNMS(oldest);
         } else {
            MinecraftVersion bumped_5 = new MinecraftVersion(minecraftVersion.getMajor(), minecraftVersion.getMinor(), minecraftVersion.getBuild() + 1);
            Set set = versionMap.entrySet();
            Iterable iterable2 = (Iterable)set;
            boolean flag2;
            if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
               flag2 = false;
            } else {
               Iterator iterator2 = iterable2.iterator();

               while(true) {
                  if (!iterator2.hasNext()) {
                     flag2 = false;
                     break;
                  }

                  Object element = iterator2.next();
                  Map.Entry it_4 = (Map.Entry)element;
                  int value2 = 0;
                  Object obj6 = it_4.getValue();
                  Object[] objs = (Object[])obj6;
                  int value3 = 0;
                  int value4 = objs.length;

                  while(true) {
                     if (value3 >= value4) {
                        flag = false;
                        break;
                     }

                     Object obj7 = objs[value3];
                     int value5 = 0;
                     if (bumped_5.equals(obj7)) {
                        flag = true;
                        break;
                     }

                     ++value3;
                  }

                  if (flag) {
                     flag2 = true;
                     break;
                  }
               }
            }

            if (flag2) {
               Logs.logWarn("Nexo v" + NexoPlugin.Companion.instance().getPluginMeta().getVersion() + " does not have official support for Server-Version (" + minecraftVersion.getVersion() + ")", false);
               Logs.logWarn("This version is considered \"dead\", as it was immediatly followed by " + bumped_5.getVersion() + " or newer", false);
               Logs.logWarn("It is heavily encouraged to update to atleast " + bumped_5.getVersion() + " for optimal compatibility", false);
               Logs.logWarn("Nexo will attempt to automatically support your version, but there might be issues", false);
               Logs.logWarn("Recommended to ensure you are using a supported server-version for optimal compatibility", false);
               return this.toNMS(bumped_5);
            } else {
               return VersionUtil.NMSVersion.UNKNOWN;
            }
         }
      }
   }

   public final boolean matchesServer(@NotNull NMSVersion version) {
      return version != VersionUtil.NMSVersion.UNKNOWN && this.toNMS(MinecraftVersion.Companion.getCurrentVersion()) == version;
   }

   public final boolean matchesServer(@NotNull String server) {
      return Intrinsics.areEqual(MinecraftVersion.Companion.getCurrentVersion(), new MinecraftVersion(server));
   }

   @JvmStatic
   public static final boolean atleast(@NotNull String versionString) {
      Object obj = versionCache.computeIfAbsent(versionString, (a0_ -> VersionUtil.atleast$lambda$0((String)a0_)));
      return (Boolean)obj;
   }

   public final boolean below(@NotNull String versionString) {
      return !atleast(versionString);
   }

   public final boolean isFoliaServer() {
      Lazy lazy = isFoliaServer$delegate;
      return (Boolean)lazy.getValue();
   }

   private static final Boolean atleast$lambda$0(String it) {
      return (new MinecraftVersion(it)).atOrAbove();
   }

   private static final Boolean atleast$lambda$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final boolean isFoliaServer_delegate$lambda$0() {
      return NexoPlugin.Companion.instance().getFoliaLib().isFolia();
   }

   static {
      Map map = (Map)versionMap;
      NMSVersion v26_R2 = VersionUtil.NMSVersion.v26_R2;
      MinecraftVersion[] minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("26.2.0")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v26_R1;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("26.1"), new MinecraftVersion("26.1.1"), new MinecraftVersion("26.1.2")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v1_21_R11;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("1.21.11")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v1_21_R10;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("1.21.10")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v1_21_R8;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("1.21.8")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v1_21_R5;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("1.21.5")};
      map.put(v26_R2, minecraftVersions);
      map = (Map)versionMap;
      v26_R2 = VersionUtil.NMSVersion.v1_21_R4;
      minecraftVersions = new MinecraftVersion[]{new MinecraftVersion("1.21.4")};
      map.put(v26_R2, minecraftVersions);
      versionCache = new ConcurrentHashMap();
      isFoliaServer$delegate = LazyKt.lazy(VersionUtil::isFoliaServer_delegate$lambda$0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/utils/VersionUtil$NMSVersion;", "", "<init>", "(Ljava/lang/String;I)V", "v26_R2", "v26_R1", "v1_21_R11", "v1_21_R10", "v1_21_R8", "v1_21_R5", "v1_21_R4", "UNKNOWN", "com.nexomc:core"}
   )
   public static enum NMSVersion {
      v26_R2,
      v26_R1,
      v1_21_R11,
      v1_21_R10,
      v1_21_R8,
      v1_21_R5,
      v1_21_R4,
      UNKNOWN;
   }
}
