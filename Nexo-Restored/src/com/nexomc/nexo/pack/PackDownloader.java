package com.nexomc.nexo.pack;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonParser;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.io.encoding.Base64;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005J\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005J\b\u0010\n\u001a\u00020\u000bH\u0002R\u0016\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/pack/PackDownloader;", "", "<init>", "()V", "requiredPackDownload", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "defaultPackDownload", "downloadRequiredPack", "downloadDefaultPack", "logSkipReason", "", "Companion", "com.nexomc:core"}
)
public final class PackDownloader {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private volatile CompletableFuture<Void> requiredPackDownload;
   @Nullable
   private volatile CompletableFuture<Void> defaultPackDownload;

   @NotNull
   public final CompletableFuture<Void> downloadRequiredPack() {
      CompletableFuture<Void> future = this.requiredPackDownload;
      if (future == null) {
         synchronized (this) {
            future = this.requiredPackDownload;
            if (future == null) {
               future = CompletableFuture.runAsync(() -> downloadRequiredPack$lambda$0$0(this));
               this.requiredPackDownload = future;
            }
         }
      }

      CompletableFuture<Void> result = future.completeOnTimeout(null, 4L, java.util.concurrent.TimeUnit.SECONDS);
      return result;
   }

   @NotNull
   public final CompletableFuture<Void> downloadDefaultPack() {
      CompletableFuture<Void> future = this.defaultPackDownload;
      if (future == null) {
         synchronized (this) {
            future = this.defaultPackDownload;
            if (future == null) {
               future = CompletableFuture.runAsync(() -> downloadDefaultPack$lambda$0$0(this));
               this.defaultPackDownload = future;
            }
         }
      }

      CompletableFuture<Void> result = future.completeOnTimeout(null, 4L, java.util.concurrent.TimeUnit.SECONDS);
      return result;
   }

   private static final void downloadRequiredPack$lambda$0$0(PackDownloader packDownloader) {
      {
         Object obj;
         try {
            int value = 0;
            String str = Companion.checkPackHash("RequiredResourcePack");
            if (str == null) {
               return;
            }

            String hash = str;
            File externalPacks = PackGenerator.Companion.getExternalPacks();
            String[] strs = new String[]{"RequiredPack_" + hash + ".zip"};
            File zipPath = FileUtilsKt.resolve(externalPacks, strs);
            Companion.removeOldHashPack("RequiredPack", hash);
            if (!zipPath.exists() && !FilesKt.resolveSibling(zipPath, "RequiredPack_" + hash).exists()) {
               Companion companion = Companion;
               Path toPath = zipPath.toPath();
               companion.downloadPack("RequiredResourcePack", toPath);
            }

            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            Logs.logWarn("Failed to download RequiredPack...", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
            }
         }

      }
   }

   private static final void downloadDefaultPack$lambda$0$0(PackDownloader packDownloader) {
      if (Settings.PACK_IMPORT_DEFAULT.toBool()) {
         {
            Object obj;
            try {
               int value = 0;
               String str = Companion.checkPackHash("DefaultResourcePack");
               if (str == null) {
                  return;
               }

               String hash = str;
               File externalPacks = PackGenerator.Companion.getExternalPacks();
               String[] strs = new String[]{"DefaultPack_" + hash + ".zip"};
               File zipPath = FileUtilsKt.resolve(externalPacks, strs);
               Companion.removeOldHashPack("DefaultPack", hash);
               if (!zipPath.exists() && !FilesKt.resolveSibling(zipPath, "DefaultPack_" + hash).exists()) {
                  Logs.logInfo("Downloading default resourcepack...");
                  Companion companion = Companion;
                  Path toPath = zipPath.toPath();
                  companion.downloadPack("DefaultResourcePack", toPath);
               } else {
                  Logs.logSuccess("Skipped downloading DefaultPack as it is up to date!", false);
               }

               obj = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable e) {
               obj = ResultShim.ok(ResultKt.createFailure(e));
            }

            Throwable throwable = ResultShim.exceptionOrNull(obj);
            if (throwable != null) {
               Throwable it = throwable;
               int value2 = 0;
               Logs.logWarn("Failed to download DefaultPack", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
               }
            }

         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0002J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u001a\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u0002J\u0018\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0014\u0010\u0011\u001a\u00020\u0007*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0005H\u0002J\u0018\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000e\u001a\u00020\u0005H\u0002¨\u0006\u0018"},
      d2 = {"Lcom/nexomc/nexo/pack/PackDownloader$Companion;", "", "<init>", "()V", "readToken", "", "logTokenError", "", "exception", "", "removeOldHashPack", "filePrefix", "newHash", "downloadPack", "repo", "zipPath", "Ljava/nio/file/Path;", "setCommonHeaders", "Ljava/net/HttpURLConnection;", "token", "unzipToPath", "inputStream", "Ljava/io/InputStream;", "checkPackHash", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final String readToken() {
         try {
         {
            File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
            String[] it = new String[]{"token.secret"};
            File it_7 = FileUtilsKt.resolve(dataFolder, it);
            int value = 0;
            File file = it_7.exists() ? it_7 : null;
            InputStream it_6 = file != null ? (InputStream)(new FileInputStream(file)) : NexoPlugin.Companion.instance().getResource("token.secret");
            String str;
            if (it_6 != null) {
               InputStream accessStream = it_6;
               int it_5 = 0;
               Companion companion = PackDownloader.Companion;

               Object obj2;
               try {
                  int value2 = 0;
                  Closeable closeable = (Closeable)(new InputStreamReader(accessStream));
                  Throwable throwable = null;

                  String str2;
                  try {
                     InputStreamReader reader = (InputStreamReader)closeable;
                     int value3 = 0;
                     YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((Reader)reader);
                     YamlConfiguration yaml = yamlConfiguration;
                     String str3 = NexoYamlKt.stringOrEmpty((ConfigurationSection)yaml, "token1");
                     String token = str3 + NexoYamlKt.stringOrEmpty((ConfigurationSection)yaml, "token2");
                     str2 = StringsKt.decodeToString(Base64.Default.getMime().decode((CharSequence)token, 0, ((CharSequence)token).length()));
                  } catch (Throwable e) {
                     throwable = e;
                     throw e;
                  } finally {
                     CloseableKt.closeFinally(closeable, throwable);
                  }

                  obj2 = ResultShim.ok(str2);
               } catch (Throwable ex) {
                  obj2 = ResultShim.ok(ResultKt.createFailure(ex));
               }

               Object obj = obj2;
               Throwable throwable2 = ResultShim.exceptionOrNull(obj2);
               if (throwable2 != null) {
                  Throwable exception = throwable2;
                  int value4 = 0;
                  PackDownloader.Companion.logTokenError(exception);
               }

               str = (String)(ResultShim.isFailure(obj) ? null : obj);
            } else {
               str = null;
            }

            String it_4 = str;
            int value5 = 0;
            if (it_4 == null) {
               Logs.logWarn("Missing token-file, please contact the developer!", false);
            }

            return it_4;
         }
         } catch (Exception ex2) {
            throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
         }
      }

      private final void logTokenError(Throwable exception) {
         if (Settings.DEBUG.toBool()) {
            exception.printStackTrace();
         }

         String message = exception.getMessage();
         if (message == null) {
            message = "An unexpected error occurred while reading token.";
         }

         Logs.logWarn(message, false);
         Logs.logError("Failed to read token-file. Please contact the developer!", true);
      }

      private final void removeOldHashPack(String filePrefix, String newHash) {
         boolean flag;
         File[] listFiles = PackGenerator.Companion.getExternalPacks().listFiles();
         if (listFiles != null) {
            Object[] objs = listFiles;
            Object obj = objs;
            Collection destination = (Collection)(new ArrayList());
            int value = 0;

            for(int i = objs.length; value < i; ++value) {
               Object element;
               block: {
                  element = ((Object[])obj)[value];
                  int value2 = 0;
                  String name = ((File)element).getName();
                  if (StringsKt.startsWith(name, filePrefix, false)) {
                     name = ((File)element).getName();
                     if (!StringsKt.endsWith(name, newHash + ".zip", false)) {
                        flag = true;
                        break block;
                     }
                  }

                  flag = false;
               }

               if (flag) {
                  destination.add(element);
               }
            }

            Iterable iterable = (Iterable)((List)destination);

            for(Object obj2 : iterable) {
               File p0 = (File)obj2;
               value = 0;
               p0.delete();
            }
         }

      }

      private final void downloadPack(String repo, Path zipPath) {
         String readToken = this.readToken();
         if (readToken != null) {
            String token = readToken;
            Companion companion2 = this;

            Object obj;
            try {
               Companion companion3 = companion2;
               int value = 0;
               URL url = URI.create("https://api.github.com/repos/Nexo-MC/" + repo + "/zipball/master").toURL();
               URLConnection openConnection = url.openConnection();
               HttpURLConnection httpURLConnection2 = (HttpURLConnection)openConnection;
               int value2 = 0;
               httpURLConnection2.setRequestMethod("GET");
               companion3.setCommonHeaders(httpURLConnection2, token);
               obj = ResultShim.ok(httpURLConnection2);
            } catch (Throwable e) {
               obj = ResultShim.ok(ResultKt.createFailure(e));
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection)(ResultShim.isFailure(obj) ? null : obj);
            if (httpURLConnection == null) {
               Logs.logError("Failed to establish a connection to download " + repo + " pack. Please contact the developer!", true);
            } else {
               HttpURLConnection connection = httpURLConnection;
               Companion companion = this;

               Object result;
               try {
                  companion2 = companion;
                  int value3 = 0;
                  Closeable inputStream = (Closeable)connection.getInputStream();
                  Throwable throwable = null;

                  try {
                     InputStream input = (InputStream)inputStream;
                     int value6 = 0;
                     companion2.unzipToPath(input, zipPath);
                     Unit unit = Unit.INSTANCE;
                  } catch (Throwable ex) {
                     throwable = ex;
                     throw ex;
                  } finally {
                     CloseableKt.closeFinally(inputStream, throwable);
                  }

                  result = ResultShim.ok(Unit.INSTANCE);
               } catch (Throwable ex2) {
                  result = ResultShim.ok(ResultKt.createFailure(ex2));
               }

               Throwable throwable2 = ResultShim.exceptionOrNull(result);
               if (throwable2 != null) {
                  Throwable exception = throwable2;
                  int value4 = 0;
                  if (Settings.DEBUG.toBool()) {
                     exception.printStackTrace();
                  }

                  Logs.logError("Failed to download " + repo + " pack. Please contact the developer!", true);
               }

               int value5 = 0;
               connection.disconnect();
            }
         }
      }

      private final void setCommonHeaders(HttpURLConnection httpURLConnection, String token) {
         httpURLConnection.setRequestProperty("Accept", "application/vnd.github+json");
         httpURLConnection.setRequestProperty("Authorization", "Bearer " + token);
         httpURLConnection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
      }

      private final void unzipToPath(InputStream inputStream, Path zipPath) {
         try {
         Closeable closeable = (Closeable)(new ZipInputStream(inputStream));
         Throwable throwable = null;

         try {
            ZipInputStream zis = (ZipInputStream)closeable;
            int value = 0;
            Closeable closeable2 = (Closeable)(new FileOutputStream(zipPath.toString()));
            Throwable throwable2 = null;

            try {
               FileOutputStream fos = (FileOutputStream)closeable2;
               int value2 = 0;
               Closeable closeable3 = (Closeable)(new ZipOutputStream((OutputStream)fos));
               Throwable throwable3 = null;

               try {
                  ZipOutputStream zos = (ZipOutputStream)closeable3;
                  int value3 = 0;

                  for(ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                     if (!entry.isDirectory()) {
                        zos.putNextEntry(new ZipEntry(StringUtils.substringAfter(entry.getName(), "/")));
                        ByteStreamsKt.copyTo((InputStream)zis, (OutputStream)zos, 1024);
                     }

                     zis.closeEntry();
                  }

                  Unit unit = Unit.INSTANCE;
               } catch (Throwable e) {
                  throwable3 = e;
                  throw e;
               } finally {
                  CloseableKt.closeFinally(closeable3, throwable3);
               }

               Unit unit2 = Unit.INSTANCE;
            } catch (Throwable ex) {
               throwable2 = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(closeable2, throwable2);
            }

            FileUtils.setHidden(zipPath);
            Unit unit3 = Unit.INSTANCE;
         } catch (Throwable ex2) {
            throwable = ex2;
            throw ex2;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         } catch (Exception ex3) {
            throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex3);
         }
      }

      private final String checkPackHash(String repo) {
         String readToken = this.readToken();
         if (readToken == null) {
            return null;
         } else {
            String token = readToken;
            Companion companion = this;

            Object obj2;
            try {
               Companion companion2 = companion;
               int value = 0;
               URL url = URI.create("https://api.github.com/repos/Nexo-MC/" + repo + "/commits").toURL();
               URLConnection openConnection = url.openConnection();
               HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
               int value2 = 0;
               httpURLConnection.setRequestMethod("GET");
               companion2.setCommonHeaders(httpURLConnection, token);
               value2 = 0;
               if (httpURLConnection.getResponseCode() != 200) {
                  return null;
               }

               Closeable closeable = (Closeable)(new InputStreamReader(httpURLConnection.getInputStream()));
               Throwable throwable = null;

               String str;
               try {
                  InputStreamReader reader = (InputStreamReader)closeable;
                  int value3 = 0;
                  str = JsonParser.parseReader((Reader)reader).getAsJsonArray().get(0).getAsJsonObject().get("sha").getAsString();
               } catch (Throwable e) {
                  throwable = e;
                  throw e;
               } finally {
                  CloseableKt.closeFinally(closeable, throwable);
               }

               obj2 = ResultShim.ok(str);
            } catch (Throwable ex) {
               obj2 = ResultShim.ok(ResultKt.createFailure(ex));
            }

            Object obj = obj2;
            Throwable throwable2 = ResultShim.exceptionOrNull(obj2);
            if (throwable2 != null) {
               Throwable throwable3 = throwable2;
               int value4 = 0;
               if (Settings.DEBUG.toBool()) {
                  throwable3.printStackTrace();
               }

               String message = throwable3.getMessage();
               if (message == null) {
                  message = "Failed to fetch pack hash.";
               }

               Logs.logWarn(message, false);
            }

            return (String)(ResultShim.isFailure(obj) ? null : obj);
         }
      }
   }
}
