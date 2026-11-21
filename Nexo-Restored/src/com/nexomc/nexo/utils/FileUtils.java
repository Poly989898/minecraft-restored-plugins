package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005J\u0014\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0007b\u0002\b\u0011J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0005R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/utils/FileUtils;", "", "<init>", "()V", "serverFolder", "Ljava/io/File;", "getServerFolder", "()Ljava/io/File;", "serverFolder$delegate", "Lkotlin/Lazy;", "isEmpty", "", "directory", "setHidden", "", "path", "Ljava/nio/file/Path;", "Lkotlin/jvm/JvmStatic;", "getSha1Hash", "", "file", "com.nexomc:core"}
)
public final class FileUtils {
   @NotNull
   public static final FileUtils INSTANCE = new FileUtils();
   @NotNull
   private static final Lazy serverFolder$delegate = LazyKt.lazy(FileUtils::serverFolder_delegate$lambda$0);

   private FileUtils() {
   }

   @NotNull
   public final File getServerFolder() {
      Lazy lazy = serverFolder$delegate;
      return (File)lazy.getValue();
   }

   public final boolean isEmpty(@NotNull File directory) {
      File[] listFiles = directory.listFiles();
      return listFiles != null ? listFiles.length == 0 : true;
   }

   @JvmStatic
   public static final void setHidden(@NotNull Path path) {
      FileUtils fileUtils = INSTANCE;

      Object obj;
      try {
         int value = 0;
         String property = System.getProperty("os.name");
         if (StringsKt.startsWith(property, "Windows", false)) {
            Files.setAttribute(path, "dos:hidden", true);
         }

         obj = ResultShim.ok(Files.isHidden(path));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
   }

   @NotNull
   public final String getSha1Hash(@NotNull File file) {
      try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      Closeable closeable = (Closeable)(new FileInputStream(file));
      Throwable throwable = null;

      try {
         FileInputStream inputStream = (FileInputStream)closeable;
         int value = 0;
         byte[] buffer = new byte[8192];
         int bytesRead = 0;

         while(true) {
            int it = inputStream.read(buffer);
            int value2 = 0;
            if (it == -1) {
               Unit unit = Unit.INSTANCE;
               break;
            }

            digest.update(buffer, 0, it);
         }
      } catch (Throwable e) {
         throwable = e;
         throw e;
      } finally {
         CloseableKt.closeFinally(closeable, throwable);
      }

      byte[] bs = digest.digest();
      return ArraysKt.joinToString(bs, (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", FileUtils::getSha1Hash$lambda$1);
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private static final File serverFolder_delegate$lambda$0() {
      File parentFile = NexoPlugin.Companion.instance().getDataFolder().getAbsoluteFile().getParentFile();
      parentFile = parentFile.getParentFile();
      return parentFile;
   }

   private static final CharSequence getSha1Hash$lambda$1(byte it) {
      String str = "%02x";
      Object[] objs = new Object[]{it};
      String str2 = String.format(str, Arrays.copyOf(objs, objs.length));
      return (CharSequence)str2;
   }
}
