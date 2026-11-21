package com.nexomc.nexo.nms;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Collection;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmStatic;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\b\u001a\u00020\tH\u0007b\u0002\b\u0010J\f\u0010\u0011\u001a\u00020\u0012H\u0007b\u0002\b\u0010J\b\u0010\u0013\u001a\u00020\tH\u0002R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/nms/NMSHandlers;", "", "<init>", "()V", "SUPPORTED_VERSION", "", "Lcom/nexomc/nexo/utils/VersionUtil$NMSVersion;", "[Lcom/nexomc/nexo/utils/VersionUtil$NMSVersion;", "handler", "Lcom/nexomc/nexo/nms/NMSHandler;", "version", "", "getVersion", "()Ljava/lang/String;", "setVersion", "(Ljava/lang/String;)V", "Lkotlin/jvm/JvmStatic;", "resetHandler", "", "setupHandler", "com.nexomc:core"}
)
public final class NMSHandlers {
   @NotNull
   public static final NMSHandlers INSTANCE = new NMSHandlers();
   @NotNull
   private static final VersionUtil.NMSVersion[] SUPPORTED_VERSION;
   @NotNull
   private static NMSHandler handler;
   @Nullable
   private static String version;

   private NMSHandlers() {
   }

   @Nullable
   public final String getVersion() {
      return version;
   }

   public final void setVersion(@Nullable String str) {
      version = str;
   }

   @JvmStatic
   @NotNull
   public static final NMSHandler handler() {
      return handler;
   }

   @JvmStatic
   public static final void resetHandler() {
      HandlerList.unregisterAll(handler.getPackDispatchListener());
      handler.creativeInventory().uninitialize();
      INSTANCE.setupHandler();
   }

   private final NMSHandler setupHandler() {
      handler = new EmptyNMSHandler();
      VersionUtil.NMSVersion[] nmsVersions = SUPPORTED_VERSION;
      int value = 0;

      for(int i = nmsVersions.length; value < i; ++value) {
         VersionUtil.NMSVersion selectedVersion = nmsVersions[value];
         if (VersionUtil.INSTANCE.matchesServer(selectedVersion)) {
            version = selectedVersion.name();
            NMSHandlers nmsHandlers = this;

            try {
               NMSHandlers nmsHandlers3 = nmsHandlers;
               int value2 = 0;
               Object newInstance = Class.forName("com.nexomc.nexo.nms." + version + ".NMSHandler").getConstructor().newInstance();
               handler = (NMSHandler)newInstance;
               Logs.logSuccess("NMS-Version " + version + " has been detected.", false);
               Bukkit.getPluginManager().registerEvents(handler.getPackDispatchListener(), (Plugin)NexoPlugin.Companion.instance());
               return handler;
            } catch (Throwable e) {
               Object obj = ResultShim.ok(ResultKt.createFailure(e));
               Throwable throwable = ResultShim.exceptionOrNull(obj);
               if (throwable != null) {
                  Throwable throwable2 = throwable;
                  int value3 = 0;
                  if (Settings.DEBUG.toBool()) {
                     Logs.logWarn(ExceptionsKt.stackTraceToString(throwable2), false);
                  }

                  NMSHandlers nmsHandlers2 = INSTANCE;
                  Logs.logWarn("Nexo does not support this version of Minecraft (" + version + ") yet.", false);
                  Logs.logWarn("Nexo will attempt to use the latest supported version, but there might be issues...", false);
                  Logs.logWarn("It is recommended to ensure you are on the latest release Nexo version for optimal compatibility.", true);
                  handler = new EmptyNMSHandler();
               }
            }
         }
      }

      return handler;
   }

   static {
      Collection collection = (Collection)kotlin.enums.EnumEntriesKt.enumEntries(VersionUtil.NMSVersion.values());
      SUPPORTED_VERSION = (VersionUtil.NMSVersion[])collection.toArray(new VersionUtil.NMSVersion[0]);
      handler = INSTANCE.setupHandler();
   }
}
