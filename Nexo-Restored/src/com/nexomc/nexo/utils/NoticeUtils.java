package com.nexomc.nexo.utils;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u0006\u0010\u0006\u001a\u00020\u0005J\u0006\u0010\u0007\u001a\u00020\u0005J\u0006\u0010\b\u001a\u00020\u0005¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/utils/NoticeUtils;", "", "<init>", "()V", "compileNotice", "", "ciNotice", "leakNotice", "failedLibs", "com.nexomc:core"}
)
public final class NoticeUtils {
   @NotNull
   public static final NoticeUtils INSTANCE = new NoticeUtils();

   private NoticeUtils() {
   }

   public final void failedLibs() {
      Logs.logError("Nexo failed to download/load certain libraries...", false);
      Logs.logError("This will cause some functionalities not to work", false);
      Logs.logError("Please restart your server, if this continues download NexoLibs aswell", false);
   }
}
