package com.nexomc.nexo.pack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.creative.NexoPackWriter;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\f\u0010\u000e\u001a\u00020\r*\u00020\u0007H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/pack/NexoPackSquash;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "packSquashCache", "Ljava/io/File;", "getPackSquashCache", "()Ljava/io/File;", "squashPack", "", "hash", "", "absolutePath", "Companion", "com.nexomc:core"}
)
public final class NexoPackSquash {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private final File packSquashCache;
   @Nullable
   private static Process process;

   public NexoPackSquash(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      String[] strs = new String[]{"pack", "packsquash", ".cache"};
      File file = FileUtilsKt.resolve(dataFolder, strs);
      int value = 0;
      file.mkdirs();
      this.packSquashCache = file;
   }

   @NotNull
   public final File getPackSquashCache() {
      return this.packSquashCache;
   }

   public final boolean squashPack(@NotNull String hash) {
      File inputDirectory = FileUtilsKt.resolve(this.packSquashCache, new String[]{hash});
      File squashedZip = FileUtilsKt.resolve(this.packSquashCache, new String[]{hash + ".zip"});

      // Keep only the newest cached squashed packs
      File[] cachedFiles = this.packSquashCache.listFiles();
      List<File> cachedZips = new ArrayList<>();
      for (File cached : cachedFiles) {
         if (Intrinsics.areEqual(FilesKt.getExtension(cached), "zip")) {
            cachedZips.add(cached);
         }
      }

      cachedZips.sort(new NexoPackSquash$squashPack$$inlined$sortedByDescending$1());
      for (int index = 0; index < cachedZips.size(); index++) {
         if (index >= Settings.PACKSQUASH_CACHE_SIZE.toInt()) {
            cachedZips.get(index).delete();
         }
      }

      boolean success;
      try {
         File packSquashExec = new File(Settings.PACKSQUASH_EXEC_PATH.toString());
         packSquashExec.setExecutable(true);
         File packSquashSettings = new File(Settings.PACKSQUASH_SETTINGS_PATH.toString());
         if (!packSquashExec.exists() || !packSquashSettings.exists()) {
            return false;
         }

         Path cachePath = this.packSquashCache.toPath();
         FileUtils.setHidden(cachePath);
         if (!squashedZip.exists()) {
            NexoPackWriter.INSTANCE.writeToDirectory(inputDirectory, this.resourcePack);
            NexoPackWriter.INSTANCE.writeToZipFile(squashedZip, this.resourcePack);

            String tomlContent = FilesKt.readText(packSquashSettings, kotlin.text.Charsets.UTF_8);
            tomlContent = new Regex("pack_directory = .*").replace(tomlContent, "pack_directory = '" + this.absolutePath(inputDirectory) + "'");
            tomlContent = new Regex("output_file_path = .*").replace(tomlContent, "output_file_path = '" + this.absolutePath(squashedZip) + "'");
            FilesKt.writeText(packSquashSettings, tomlContent, kotlin.text.Charsets.UTF_8);

            Logs.logInfo("Squashing NexoPack using PackSquash...");
            String[] command = new String[]{this.absolutePath(packSquashExec), this.absolutePath(packSquashSettings)};
            Process process = (new ProcessBuilder(command)).directory(NexoPlugin.Companion.instance().getDataFolder()).redirectInput(Redirect.PIPE).redirectOutput(Redirect.PIPE).redirectErrorStream(true).start();
            BufferedReader reader = process.inputReader();
            for (String line : TextStreamsKt.readLines((Reader)reader)) {
               if (line.length() == 0) {
                  continue;
               }

               if (line.startsWith("!")) {
                  Logs.logError(line, false);
               } else if (line.startsWith("*")) {
                  Logs.logWarn(line, false);
               } else if (line.startsWith("-")) {
                  Logs.logInfo(line);
               } else if (line.startsWith("#")) {
                  Logs.logSuccess(line, false);
               } else if (Settings.DEBUG.toBool()) {
                  Logs.logInfo(line);
               }
            }
         }

         success = true;
      } catch (Throwable error) {
         success = false;
         Logs.logError("Failed to squash pack with PackSquash: " + error.getMessage(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(error), false);
         }

         squashedZip.delete();
      }

      FilesKt.deleteRecursively(inputDirectory);
      squashedZip.setLastModified(System.currentTimeMillis());
      return success;
   }

   private final String absolutePath(File file) {
      String absolutePath = file.getAbsolutePath();
      return StringsKt.replace(absolutePath, "\\", "/", false);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\t\u001a\u00020\nR\"\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005@BX\u0082\u000e¢\u0006\b\n\u0000\"\u0004\b\u0007\u0010\b¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/pack/NexoPackSquash$Companion;", "", "<init>", "()V", "value", "Ljava/lang/Process;", "process", "setProcess", "(Ljava/lang/Process;)V", "stopPackSquash", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final void setProcess(Process value) {
         Process process2 = NexoPackSquash.process;
         if (process2 != null) {
            process2.destroy();
         }

         NexoPackSquash.process = value;
      }

      public final void stopPackSquash() {
         Process process2 = NexoPackSquash.process;
         if (process2 != null) {
            process2.destroy();
         }

      }
   }
}
