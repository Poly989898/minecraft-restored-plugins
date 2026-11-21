package com.nexomc.nexo.pack;
import team.unnamed.creative.metadata.Metadata;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoPack;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import kotlin.ExceptionsKt;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer;

@kotlin.Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017J\b\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u0015H\u0002J\b\u0010\u001f\u001a\u00020\u001dH\u0002J\b\u0010 \u001a\u00020\u001dH\u0002J\u0010\u0010!\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020#H\u0002J\u0012\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020#H\u0002J\u0018\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u001b2\u0006\u0010'\u001a\u00020\u0015H\u0002J\u0012\u0010(\u001a\u0004\u0018\u00010\u001b2\u0006\u0010$\u001a\u00020#H\u0002J\n\u0010)\u001a\u0004\u0018\u00010\u0005H\u0002J\u001a\u0010*\u001a\n\u0018\u00010#j\u0004\u0018\u0001`+2\b\u0010,\u001a\u0004\u0018\u00010\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\u00070\b¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\r¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u000e\u0010\u0013\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\u0018\u0018\u00010\u0017X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006-"},
   d2 = {"Lcom/nexomc/nexo/pack/VanillaResourcePack;", "", "<init>", "()V", "VERSION_MANIFEST_URL", "", "GITHUB_API_URL", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "Lorg/jetbrains/annotations/NotNull;", "getResourcePack", "()Lteam/unnamed/creative/ResourcePack;", "vanillaSounds", "", "Lnet/kyori/adventure/key/Key;", "getVanillaSounds", "()Ljava/util/List;", "vanillaLangKeys", "getVanillaLangKeys", "version", "zipPath", "Ljava/io/File;", "future", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "extractLatest", "packMetaBytes", "", "ensurePackMeta", "", "zip", "cleanOldCaches", "readVanillaRP", "extractVanillaSounds", "assetIndex", "Lcom/google/gson/JsonObject;", "versionInfo", "extractJarAssetsToZip", "clientJar", "zipFile", "downloadClientJar", "findVersionInfoUrl", "downloadJson", "Lcom/nexomc/nexo/utils/JsonObject;", "url", "com.nexomc:core"}
)
public final class VanillaResourcePack {
   @NotNull
   public static final VanillaResourcePack INSTANCE = new VanillaResourcePack();
   @NotNull
   private static final String VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
   @NotNull
   private static final String GITHUB_API_URL = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/%s/assets/minecraft/lang/_list.json";
   @NotNull
   private static final ResourcePack resourcePack;
   @NotNull
   private static final List<Key> vanillaSounds;
   @NotNull
   private static final List<Key> vanillaLangKeys;
   @NotNull
   private static final String version;
   @NotNull
   private static final File zipPath;
   @Nullable
   private static CompletableFuture<Void> future;

   private VanillaResourcePack() {
   }

   @NotNull
   public final ResourcePack getResourcePack() {
      return resourcePack;
   }

   @NotNull
   public final List<Key> getVanillaSounds() {
      return vanillaSounds;
   }

   @NotNull
   public final List<Key> getVanillaLangKeys() {
      return vanillaLangKeys;
   }

   @NotNull
   public final CompletableFuture<Void> extractLatest() {
        block3: {
            block2: {
                if (future == null) break block2;
                if (zipPath.exists()) break block3;
                CompletableFuture<Void> completableFuture = future;
                if (!completableFuture.isDone()) break block3;
            }
            future = CompletableFuture.runAsync(VanillaResourcePack::extractLatest$lambda$0).completeOnTimeout(null, 60L, TimeUnit.SECONDS);
        }
        CompletableFuture<Void> completableFuture = future;
        return completableFuture;
    }

   private final byte[] packMetaBytes() {
      try {
      PackFormat packFormat2 = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
      PackFormat packFormat = packFormat2;
      team.unnamed.creative.metadata.Metadata metadata2 = team.unnamed.creative.metadata.Metadata.metadata().add(PackMeta.of(packFormat, (Component)Component.text("Nexo vanilla asset-cache"))).build();
      team.unnamed.creative.metadata.Metadata metadata = metadata2;
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      Closeable closeable = new JsonWriter((Writer)(new OutputStreamWriter((OutputStream)bytes, Charsets.UTF_8)));
      Throwable throwable = null;

      try {
         JsonWriter writer = (JsonWriter)closeable;
         int value = 0;
         MetadataSerializer.INSTANCE.serializeToJson(metadata, writer, packFormat);
         Unit unit = Unit.INSTANCE;
      } catch (Throwable e) {
         throwable = e;
         throw e;
      } finally {
         CloseableKt.closeFinally(closeable, throwable);
      }

      byte[] toByteArray = bytes.toByteArray();
      return toByteArray;
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private final void ensurePackMeta(File zip) {
      VanillaResourcePack vanillaResourcePack = this;

      Object obj;
      try {
         VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
         int value = 0;
         Closeable closeable = (Closeable)(new ZipFile(zip));
         Throwable throwable = null;

         boolean flag;
         try {
            ZipFile zf = (ZipFile)closeable;
            int value2 = 0;
            Enumeration it = zf.entries();
            int value3 = 0;
            flag = it.hasMoreElements() && Intrinsics.areEqual(((ZipEntry)it.nextElement()).getName(), "pack.mcmeta");
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         if (flag) {
            return;
         }

         File patched = File.createTempFile("assetCacheMeta", ".zip", zip.getParentFile());
         Closeable closeable2 = (Closeable)(new ZipOutputStream((OutputStream)(new FileOutputStream(patched))));
         Throwable throwable2 = null;

         try {
            ZipOutputStream zos = (ZipOutputStream)closeable2;
            int value4 = 0;
            zos.putNextEntry(new ZipEntry("pack.mcmeta"));
            zos.write(vanillaResourcePack2.packMetaBytes());
            zos.closeEntry();
            Closeable closeable3 = (Closeable)(new ZipInputStream((InputStream)(new FileInputStream(zip))));
            Throwable throwable3 = null;

            try {
               ZipInputStream zis = (ZipInputStream)closeable3;
               int value5 = 0;

               for(ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                  if (!Intrinsics.areEqual(entry.getName(), "pack.mcmeta")) {
                     zos.putNextEntry(new ZipEntry(entry.getName()));
                     ByteStreamsKt.copyTo((InputStream)zis, (OutputStream)zos, 8192);
                     zos.closeEntry();
                  }

                  zis.closeEntry();
               }

               Unit unit = Unit.INSTANCE;
            } catch (Throwable ex) {
               throwable3 = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(closeable3, throwable3);
            }

            Unit unit2 = Unit.INSTANCE;
         } catch (Throwable ex2) {
            throwable2 = ex2;
            throw ex2;
         } finally {
            CloseableKt.closeFinally(closeable2, throwable2);
         }

         Path toPath = patched.toPath();
         Path path = zip.toPath();
         CopyOption[] copyOptions = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
         obj = ResultShim.ok(Files.move(toPath, path, copyOptions));
      } catch (Throwable ex3) {
         obj = ResultShim.ok(ResultKt.createFailure(ex3));
      }

      Throwable throwable4 = ResultShim.exceptionOrNull(obj);
      if (throwable4 != null) {
         Throwable it = throwable4;
         int value6 = 0;
         Logs.logWarn("Failed to stamp pack.mcmeta onto vanilla asset-cache: " + it.getMessage(), false);
      }

   }

   private final void cleanOldCaches() {
      File cacheDir = zipPath.getParentFile().getParentFile();
      File[] listFiles = cacheDir.listFiles();
      if (listFiles != null) {
         Object[] objs = listFiles;
         Object obj = objs;
         Collection destination = (Collection)(new ArrayList());
         int value = 0;

         for(int i = objs.length; value < i; ++value) {
            Object element = ((Object[])obj)[value];
            int value2 = 0;
            if (((File)element).isDirectory() && !Intrinsics.areEqual(((File)element).getName(), version)) {
               destination.add(element);
            }
         }

         Iterable iterable = (Iterable)((List)destination);

         for(Object obj2 : iterable) {
            File it = (File)obj2;
            value = 0;
            FilesKt.deleteRecursively(it);
         }
      }

   }

   private final void readVanillaRP() {
      VanillaResourcePack vanillaResourcePack = this;

      Object obj;
      try {
         VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
         int value = 0;
         PackFormat packFormat2 = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
         PackFormat packFormat = packFormat2;
         NexoPack.overwritePack(resourcePack, NexoPackReader.INSTANCE.readFile(zipPath, packFormat));
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         Logs.logWarn("Failed to read Vanilla ResourcePack-cache...", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }
      }

   }

   private final void extractVanillaSounds(JsonObject assetIndex) {
      JsonObject objects = assetIndex.getAsJsonObject("objects");
      JsonArray sounds = new JsonArray();
      Set keySet = objects.keySet();
      Iterable iterable = (Iterable)keySet;
      int value2 = 0;

      for(Object element : iterable) {
         String key = (String)element;
         int value = 0;
         Key key2 = Key.key(StringUtilsKt.remove(StringUtilsKt.remove(key, "minecraft/sounds/"), ".ogg"));
         Key soundKey = key2;
         if (StringsKt.startsWith(key, "minecraft/sounds/", false)) {
            VanillaResourcePack vanillaResourcePack = INSTANCE;
            ((Collection)vanillaSounds).add(soundKey);
            sounds.add(soundKey.asString());
         }
      }

      File file = FilesKt.resolveSibling(zipPath, "vanilla-sounds.json");
      String str = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "sounds", sounds).toString();
      FilesKt.writeText(file, str, kotlin.text.Charsets.UTF_8);
   }

   private final JsonObject assetIndex(JsonObject versionInfo) {
      JsonObject assetIndex = versionInfo.getAsJsonObject("assetIndex");
      String url = assetIndex.get("url").getAsString();
      VanillaResourcePack vanillaResourcePack = this;

      Object obj2;
      try {
         VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
         int value = 0;
         JsonObject jsonObject = vanillaResourcePack2.downloadJson(url);
         obj2 = ResultShim.ok(jsonObject.getAsJsonObject());
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         Logs.logError("Failed to download asset index", false);
         if (!Settings.DEBUG.toBool()) {
            it.printStackTrace();
         }
      }

      return (JsonObject)(ResultShim.isFailure(obj) ? null : obj);
   }

   private final void extractJarAssetsToZip(byte[] clientJar, File zipFile) {
      VanillaResourcePack vanillaResourcePack = this;

      Object obj;
      try {
         VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
         int value = 0;
         File tempOutputFile = File.createTempFile("clientJar", ".tmp");
         int value2 = 0;
         tempOutputFile.deleteOnExit();
         FilesKt.writeBytes(tempOutputFile, clientJar);
         File tempInputFile = tempOutputFile;
         File file = File.createTempFile("filteredZip", ".tmp");
         int value3 = 0;
         file.deleteOnExit();
         tempOutputFile = file;
         Closeable closeable4 = (Closeable)(new FileInputStream(tempInputFile));
         Throwable throwable5 = null;

         try {
            FileInputStream fis = (FileInputStream)closeable4;
            int value4 = 0;
            Closeable closeable = (Closeable)(new ZipInputStream((InputStream)fis));
            Throwable throwable = null;

            try {
               ZipInputStream zis = (ZipInputStream)closeable;
               int value5 = 0;
               Closeable closeable2 = (Closeable)(new FileOutputStream(tempOutputFile));
               Throwable throwable2 = null;

               try {
                  FileOutputStream fos = (FileOutputStream)closeable2;
                  int value6 = 0;
                  Closeable closeable3 = (Closeable)(new ZipOutputStream((OutputStream)fos));
                  Throwable throwable3 = null;

                  try {
                     ZipOutputStream zos = (ZipOutputStream)closeable3;
                     int value7 = 0;
                     zos.putNextEntry(new ZipEntry("pack.mcmeta"));
                     zos.write(vanillaResourcePack2.packMetaBytes());
                     zos.closeEntry();

                     for(ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                        String name = entry.getName();
                        if (StringsKt.startsWith(name, "assets/", false) && !StringsKt.endsWith(name, "scaffolding_unstable.json", false) && !StringsKt.startsWith(name, "assets/minecraft/shaders/", false) && !StringsKt.startsWith(name, "assets/minecraft/particles/", false) && !StringsKt.endsWith(name, "lang/_all.json", false)) {
                           ZipEntry zipEntry = new ZipEntry(name);
                           zos.putNextEntry(zipEntry);
                           byte[] buffer = new byte[8192];
                           int bytesRead = 0;

                           while(true) {
                              int it = zis.read(buffer);
                              int value8 = 0;
                              if (it == -1) {
                                 zos.closeEntry();
                                 break;
                              }

                              zos.write(buffer, 0, it);
                           }
                        }

                        zis.closeEntry();
                     }

                     zos.finish();
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

               Unit unit3 = Unit.INSTANCE;
            } catch (Throwable ex2) {
               throwable = ex2;
               throw ex2;
            } finally {
               CloseableKt.closeFinally(closeable, throwable);
            }

            Unit unit4 = Unit.INSTANCE;
         } catch (Throwable ex3) {
            throwable5 = ex3;
            throw ex3;
         } finally {
            CloseableKt.closeFinally(closeable4, throwable5);
         }

         obj = ResultShim.ok(FilesKt.copyTo(tempOutputFile, zipFile, true, 8192));
      } catch (Throwable ex4) {
         obj = ResultShim.ok(ResultKt.createFailure(ex4));
      }

      Throwable throwable4 = ResultShim.exceptionOrNull(obj);
      if (throwable4 != null) {
         Throwable it = throwable4;
         int value9 = 0;
         Logs.logWarn("Failed to extract vanilla-resourcepack directly to zip file: " + it.getMessage(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }
      }

   }

   private final byte[] downloadClientJar(JsonObject versionInfo) {
      String url = versionInfo.getAsJsonObject("downloads").getAsJsonObject("client").get("url").getAsString();
      VanillaResourcePack vanillaResourcePack = this;

      Object obj2;
      try {
         VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
         int value = 0;
         Closeable openStream = (Closeable)(new URI(url)).toURL().openStream();
         Throwable throwable = null;

         byte[] bs;
         try {
            InputStream it = (InputStream)openStream;
            int value2 = 0;
            bs = it.readAllBytes();
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(openStream, throwable);
         }

         obj2 = ResultShim.ok(bs);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      Throwable throwable2 = ResultShim.exceptionOrNull(obj2);
      if (throwable2 != null) {
         Throwable it = throwable2;
         int value3 = 0;
         Logs.logWarn("Failed to download vanilla-resourcepack from: " + url, false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }
      }

      return (byte[])(ResultShim.isFailure(obj) ? null : obj);
   }

   private final String findVersionInfoUrl() {
      java.lang.String str;
      JsonObject jsonObject = this.downloadJson("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
      if (jsonObject != null) {
         jsonObject = jsonObject.getAsJsonObject();
         if (jsonObject != null) {
            JsonObject manifest = jsonObject;
            JsonArray asJsonArray = manifest.getAsJsonArray("versions");
            Iterable iterable = asJsonArray;
            Iterator iterator = iterable.iterator();

            JsonElement jsonElement;
            while(true) {
               if (!iterator.hasNext()) {
                  jsonElement = null;
                  break;
               }

               Object obj;
               block: {
                  obj = iterator.next();
                  JsonElement element = (JsonElement)obj;
                  int value = 0;
                  JsonObject asJsonObject = element.getAsJsonObject();
                  if (asJsonObject != null) {
                     JsonElement jsonElement2 = asJsonObject.get("id");
                     if (jsonElement2 != null) {
                        str = jsonElement2.getAsString();
                        break block;
                     }
                  }

                  str = null;
               }

               if (Intrinsics.areEqual(str, version)) {
                  jsonElement = (JsonElement)obj;
                  break;
               }
            }

            JsonElement jsonElement3 = (JsonElement)jsonElement;
            String str2;
            if (jsonElement3 != null) {
               JsonObject jsonObject2 = jsonElement3.getAsJsonObject();
               if (jsonObject2 != null) {
                  JsonElement jsonElement4 = jsonObject2.get("url");
                  if (jsonElement4 != null) {
                     str2 = jsonElement4.getAsString();
                     return str2;
                  }
               }
            }

            str2 = null;
            return str2;
         }
      }

      return null;
   }

   private final JsonObject downloadJson(String url) {
      if (url == null) {
         return null;
      } else {
         VanillaResourcePack vanillaResourcePack = this;

         Object obj2;
         try {
            VanillaResourcePack vanillaResourcePack2 = vanillaResourcePack;
            int value = 0;
            Closeable openStream = (Closeable)URI.create(url).toURL().openStream();
            Throwable throwable = null;

            JsonObject jsonObject;
            try {
               InputStream stream = (InputStream)openStream;
               int value2 = 0;
               Closeable closeable = (Closeable)(new InputStreamReader(stream));
               Throwable throwable2 = null;

               JsonObject jsonObject2;
               try {
                  InputStreamReader reader = (InputStreamReader)closeable;
                  int value3 = 0;
                  jsonObject2 = JsonParser.parseReader((Reader)reader).getAsJsonObject();
               } catch (Throwable e) {
                  throwable2 = e;
                  throw e;
               } finally {
                  CloseableKt.closeFinally(closeable, throwable2);
               }

               jsonObject = jsonObject2;
            } catch (Throwable ex) {
               throwable = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(openStream, throwable);
            }

            obj2 = ResultShim.ok(jsonObject);
         } catch (Throwable ex2) {
            obj2 = ResultShim.ok(ResultKt.createFailure(ex2));
         }

         Object obj = obj2;
         Throwable throwable3 = ResultShim.exceptionOrNull(obj2);
         if (throwable3 != null) {
            Throwable it = throwable3;
            int value4 = 0;
            Logs.logWarn("Failed to fetch manifest for vanilla-resourcepack...", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               Logs.logWarn(message, false);
            }
         }

         return (JsonObject)(ResultShim.isFailure(obj) ? null : obj);
      }
   }

   private static final void extractLatest$lambda$0() {
      java.util.List list;
      VanillaResourcePack vanillaResourcePack = INSTANCE;
      vanillaSounds.clear();
      vanillaResourcePack = INSTANCE;
      vanillaLangKeys.clear();
      INSTANCE.cleanOldCaches();
      File vanillaSoundsJson = FilesKt.resolveSibling(zipPath, "vanilla-sounds.json");
      File vanillaLangJson = FilesKt.resolveSibling(zipPath, "vanilla-languages.json");
      File p0 = zipPath.getParentFile();
      int it = 0;
      p0.mkdirs();
      Path toPath = p0.getParentFile().toPath();
      FileUtils.setHidden(toPath);
      VanillaResourcePack vanillaResourcePack3 = INSTANCE;

      Object result2;
      try {
         int p0_7 = 0;

         Object obj;
         try {
            int value = 0;
            JsonBuilder jsonBuilder = JsonBuilder.INSTANCE;
            JsonObject asJsonObject = JsonParser.parseString(FilesKt.readText(vanillaLangJson, kotlin.text.Charsets.UTF_8)).getAsJsonObject();
            obj = ResultShim.ok(jsonBuilder.array(asJsonObject, "languages"));
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         JsonArray jsonArray2 = (JsonArray)(ResultShim.isFailure(obj) ? null : obj);
         if (jsonArray2 == null) {
            jsonArray2 = new JsonArray();
         }

         JsonArray jsonArray = jsonArray2;
         if (jsonArray.isEmpty()) {
            block: {
               String str2 = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/%s/assets/minecraft/lang/_list.json";
               Object[] it_6 = new Object[]{version};
               String str = String.format(str2, Arrays.copyOf(it_6, it_6.length));
               JsonObject langJson = vanillaResourcePack3.downloadJson(str);
               if (langJson != null) {
                  jsonArray2 = JsonBuilder.INSTANCE.array(langJson, "files");
                  if (jsonArray2 != null) {
                     Iterable iterable = jsonArray2;
                     Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

                     for(Object item : iterable) {
                        JsonElement it_5 = (JsonElement)item;
                        int value2 = 0;
                        String asString = it_5.getAsString();
                        destination.add(StringsKt.removeSuffix(asString, (CharSequence)".json"));
                     }

                     list = (List)destination;
                     break block;
                  }
               }

               list = CollectionsKt.emptyList();
            }

            List names = list;
            JsonBuilder.INSTANCE.getJsonObject().add("languages", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonArray(), names));
            Unit langObject = Unit.INSTANCE;
            FilesKt.writeText(FilesKt.resolveSibling(zipPath, "vanilla-languages.json"), langObject.toString(), kotlin.text.Charsets.UTF_8);
            Collection collection = (Collection)vanillaLangKeys;
            Iterable iterable2 = (Iterable)names;
            Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

            for(Object obj2 : iterable2) {
               String p0_4 = (String)obj2;
               int value3 = 0;
               collection2.add(Key.key(p0_4));
            }

            iterable2 = (Iterable)((List)collection2);
            CollectionsKt.addAll(collection, iterable2);
         } else {
            Iterable iterable3 = jsonArray;
            it = 0;

            for(Object element : iterable3) {
               JsonElement json = (JsonElement)element;
               ((Collection)vanillaLangKeys).add(Key.key(json.getAsString()));
            }
         }

         result2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex) {
         result2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      UtilsKt.printOnFailure(result2, false);
      vanillaResourcePack3 = INSTANCE;

      try {
         int value4 = 0;
         if (vanillaSoundsJson.createNewFile() || ((CharSequence)FilesKt.readText(vanillaSoundsJson, kotlin.text.Charsets.UTF_8)).length() == 0) {
            JsonObject jsonObject = vanillaResourcePack3.downloadJson(vanillaResourcePack3.findVersionInfoUrl());
            JsonObject versionInfo = jsonObject != null ? jsonObject.getAsJsonObject() : null;
            JsonObject jsonObject2 = vanillaResourcePack3.assetIndex(versionInfo);
            vanillaResourcePack3.extractVanillaSounds(jsonObject2);
         }

         JsonArray asJsonArray = JsonParser.parseString(FilesKt.readText(vanillaSoundsJson, kotlin.text.Charsets.UTF_8)).getAsJsonObject().getAsJsonArray("sounds");
         Iterable iterable4 = asJsonArray;
         int value8 = 0;

         for(Object obj3 : iterable4) {
            JsonElement json = (JsonElement)obj3;
            int value5 = 0;
            ((Collection)vanillaSounds).add(Key.key(json.getAsString()));
         }

         Object p0_3 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex2) {
         Object p0_2 = ResultShim.ok(ResultKt.createFailure(ex2));
      }

      if (zipPath.exists()) {
         INSTANCE.ensurePackMeta(zipPath);
         INSTANCE.readVanillaRP();
      } else {
         Logs.logInfo("Extracting latest vanilla-resourcepack...");
         VanillaResourcePack vanillaResourcePack4 = INSTANCE;

         Object result1;
         try {
            int value6 = 0;
            JsonObject jsonObject3 = vanillaResourcePack4.downloadJson(vanillaResourcePack4.findVersionInfoUrl());
            result1 = ResultShim.ok(jsonObject3 != null ? jsonObject3.getAsJsonObject() : null);
         } catch (Throwable ex3) {
            result1 = ResultShim.ok(ResultKt.createFailure(ex3));
         }

         Throwable throwable = ResultShim.exceptionOrNull(result1);
         if (throwable != null) {
            Throwable it_1 = throwable;
            int value7 = 0;
            Logs.logWarn("Failed to fetch version-info for vanilla-resourcepack...", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(it_1), false);
            } else {
               String message = it_1.getMessage();
               Logs.logWarn(message, false);
            }

         } else {
            JsonObject jsonObject4 = (JsonObject)(ResultShim.isFailure(result1) ? null : result1);
            if (jsonObject4 != null) {
               JsonObject versionInfo = jsonObject4;
               byte[] clientJar = INSTANCE.downloadClientJar(versionInfo);
               VanillaResourcePack vanillaResourcePack2 = INSTANCE;
               vanillaResourcePack2.extractJarAssetsToZip(clientJar, zipPath);
               JsonObject assetIndex = INSTANCE.assetIndex(versionInfo);
               vanillaResourcePack2 = INSTANCE;
               vanillaResourcePack2.extractVanillaSounds(assetIndex);
               INSTANCE.readVanillaRP();
               Logs.logSuccess("Finished extracting latest vanilla-resourcepack!", false);
            }
         }
      }
   }

   static {
      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      resourcePack = resourcePack2;
      vanillaSounds = (List)(new ArrayList());
      vanillaLangKeys = (List)(new ArrayList());
      String bukkitVersion = Bukkit.getBukkitVersion();
      version = StringsKt.substringBefore(StringsKt.substringBeforeLast(StringsKt.substringBeforeLast(bukkitVersion, "-", bukkitVersion), "-", StringsKt.substringBeforeLast(bukkitVersion, "-", bukkitVersion)), ".build", StringsKt.substringBeforeLast(StringsKt.substringBeforeLast(bukkitVersion, "-", bukkitVersion), "-", StringsKt.substringBeforeLast(bukkitVersion, "-", bukkitVersion)));
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      File p0 = FilesKt.resolve(dataFolder, "pack/.assetCache/" + version);
      int value = 0;
      p0.mkdirs();
      zipPath = FilesKt.resolve(p0, version + ".zip");
      Path toPath = zipPath.getParentFile().getParentFile().toPath();
      FileUtils.setHidden(toPath);
   }
}
