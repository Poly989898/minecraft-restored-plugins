package com.nexomc.nexo.pack.creative;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipFile;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;
import team.unnamed.creative.serialize.minecraft.ResourceCategories;
import team.unnamed.creative.serialize.minecraft.ResourceCategory;
import team.unnamed.creative.serialize.minecraft.equipment.EquipmentSerializer;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeReader;
import team.unnamed.creative.serialize.minecraft.io.BinaryResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer;
import team.unnamed.creative.serialize.minecraft.model.ModelSerializer;
import team.unnamed.creative.serialize.minecraft.sound.SoundRegistrySerializer;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.util.Keys;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tJ\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0002J\u001c\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0002J\u001c\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0002J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0006\u0010\u0019\u001a\u00020\u001aJ\u001a\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001fH\u0002R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/pack/creative/NexoPackReader;", "Lteam/unnamed/creative/serialize/minecraft/MinecraftResourcePackReader;", "<init>", "()V", "readFile", "Lteam/unnamed/creative/ResourcePack;", "resourcePackFile", "Ljava/io/File;", "packFormat", "Lteam/unnamed/creative/metadata/pack/PackFormat;", "read", "reader", "Lteam/unnamed/creative/serialize/minecraft/fs/FileTreeReader;", "readFromDirectory", "directory", "readFromZipFile", "file", "fixModelLayerGaps", "Lteam/unnamed/creative/model/Model;", "model", "json", "Lcom/google/gson/JsonElement;", "parseJson", "input", "Ljava/io/InputStream;", "resetReader", "", "lenient", "", "debug", "withoutExtension", "", "string", "extension", "com.nexomc:core"}
)
public final class NexoPackReader implements MinecraftResourcePackReader {
   @NotNull
   public static final NexoPackReader INSTANCE = new NexoPackReader();
   private static boolean lenient;
   private static boolean debug;

   private NexoPackReader() {
   }

   @NotNull
   public final ResourcePack readFile(@NotNull File resourcePackFile, @Nullable PackFormat packFormat) {
      Object resourcePack = null;
      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      ResourcePack resourcePack3 = resourcePack2;
      NexoPackReader nexoPackReader = this;

      Object obj;
      try {
         NexoPackReader nexoPackReader2 = nexoPackReader;
         int value = 0;
         if (resourcePackFile.isDirectory()) {
            resourcePack3 = nexoPackReader2.readFromDirectory(resourcePackFile, packFormat);
         } else {
            String name = resourcePackFile.getName();
            if (StringsKt.endsWith(name, ".zip", false)) {
               resourcePack3 = nexoPackReader2.readFromZipFile(resourcePackFile, packFormat);
            }
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
         String str = "Failed to read %s resourcePack...";
         Object[] objs = new Object[]{resourcePackFile.getName()};
         String str2 = String.format(str, Arrays.copyOf(objs, objs.length));
         Logs.logError(str2, false);
         it.printStackTrace();
      }

      return resourcePack3;
   }

   @NotNull
   public ResourcePack read(@NotNull FileTreeReader reader) {
      return this.read(reader, (PackFormat)null);
   }

   private final ResourcePack readFromDirectory(File directory, PackFormat packFormat) {
      FileTreeReader fileTreeReader = FileTreeReader.directory(directory);
      return this.read(fileTreeReader);
   }

   private final ResourcePack readFromZipFile(File file, PackFormat packFormat) {
      try {
      AutoCloseable autoCloseable = FileTreeReader.zip(new ZipFile(file));
      Throwable throwable = null;

      ResourcePack resourcePack;
      try {
         FileTreeReader it = (FileTreeReader)autoCloseable;
         int value = 0;
         NexoPackReader nexoPackReader = INSTANCE;
         resourcePack = nexoPackReader.read(it, packFormat);
      } catch (Throwable e) {
         throwable = e;
         throw e;
      } finally {
         AutoCloseableKt.closeFinally(autoCloseable, throwable);
      }

      return resourcePack;
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private final ResourcePack read(FileTreeReader reader, PackFormat packFormat) {
      team.unnamed.creative.overlay.ResourceContainer resourceContainer;
      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      ResourcePack resourcePack = resourcePack2;
      LinkedHashMap incompleteTextures = new LinkedHashMap();
      PackFormat packFormat2 = packFormat != null ? packFormat : PackFormat.UNKNOWN;
      Map<String, ResourceCategory<?>> map = ResourceCategories.buildCategoryMapByFolder(packFormat2);
      Map packFormatsByOverlayDir = (Map)(new LinkedHashMap());

      while(reader.hasNext()) {
         String path = reader.next();
         NexoPackReader nexoPackReader = this;

         Object obj6;
         try {
            NexoPackReader nexoPackReader2 = nexoPackReader;
            int value = 0;
            ResourcePackStructure resourcePackStructure = ResourcePackStructure.INSTANCE;
            Queue tokens = resourcePackStructure.tokenize(path);
            if (tokens.isEmpty()) {
               int value2 = 0;
               String str4 = "Token collection is empty!";
               throw new IllegalStateException(str4.toString());
            }

            if (tokens.size() == 1) {
               String poll = (String)tokens.poll();
               if (Intrinsics.areEqual(poll, "pack.mcmeta")) {
                  NexoPackReader nexoPackReader3 = nexoPackReader2;

                  Object obj;
                  try {
                     team.unnamed.creative.metadata.Metadata metadata;
                     block: {
                        int value3 = 0;
                        MetadataSerializer metadataSerializer = MetadataSerializer.INSTANCE;
                        InputStream inputStream = reader.stream();
                        metadata = metadataSerializer.readFromTree(nexoPackReader3.parseJson(inputStream));
                        resourcePack.metadata(metadata);
                        PackMeta packMeta = (PackMeta)metadata.meta(PackMeta.class);
                        if (packMeta != null) {
                           PackFormat formats = packMeta.formats();
                           if (formats != null) {
                              PackFormat format = formats;
                              int categoryPath = 0;
                              packFormat2 = format;
                              map = ResourceCategories.buildCategoryMapByFolder(format);
                              break block;
                           }
                        }

                        System.err.println("Reading a resource-pack with no pack meta in its pack.mcmeta file! Unknown pack format version :(");
                     }

                     OverlaysMeta overlaysMeta = (OverlaysMeta)metadata.meta(OverlaysMeta.class);
                     if (overlaysMeta != null) {
                        for(OverlayEntry entry : overlaysMeta.entries()) {
                           packFormatsByOverlayDir.put(entry.directory(), entry.formats());
                        }
                     }

                     obj = ResultShim.ok(Unit.INSTANCE);
                  } catch (Throwable e) {
                     obj = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  ResultShim.ok(UtilsKt.runOrThrow(obj, lenient, (a0_ -> NexoPackReader.read$lambda$0$2(path, (Throwable)a0_))));
                  continue;
               }

               if (Intrinsics.areEqual(poll, "pack.png")) {
                  resourcePack.icon(reader.content().asWritable());
               } else {
                  resourcePack.unknownFile(path, reader.content().asWritable());
               }
               continue;
            }

            Object overlayDir = null;
            Object localPackFormat = null;
            localPackFormat = packFormat2;
            Object containerPath = null;
            String containerPath_6 = path;
            Object container = null;
            container = resourcePack;
            String folder = (String)tokens.poll();
            List defaultOutputs = Settings.PACK_OUTPUT_PATH.toStringListOrSingle(NexoPackReader::read$lambda$0$3);
            if (defaultOutputs.contains(folder) || ResourcePackStructure.IGNORED_NEXO_FOLDERS.contains(folder)) {
               continue;
            }

            if (packFormatsByOverlayDir.containsKey(folder) || !Intrinsics.areEqual(folder, "assets")) {
               if (tokens.isEmpty()) {
                  resourcePack.unknownFile(path, reader.content().asWritable());
                  continue;
               }

               overlayDir = folder;
               Overlay overlay = resourcePack.overlay(folder);
               if (overlay == null) {
                  overlay = Overlay.overlay(folder);
                  resourcePack.overlay(overlay);
               }

               container = overlay;
               folder = (String)tokens.poll();
               String str = path.substring((overlayDir + "/").length());
               containerPath_6 = str;
               PackFormat packFormat3 = PackFormat.UNKNOWN;
               localPackFormat = packFormatsByOverlayDir.getOrDefault(overlayDir, packFormat3);
            }

            if (folder == null || !Intrinsics.areEqual(folder, "assets") || tokens.isEmpty()) {
               ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
               continue;
            }

            String namespace = (String)tokens.poll();
            if (!Keys.isValidNamespace(namespace)) {
               ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
               continue;
            }

            if (tokens.isEmpty()) {
               ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
               continue;
            }

            String categoryName = (String)tokens.poll();
            if (tokens.isEmpty()) {
               if (Intrinsics.areEqual(categoryName, "sounds.json")) {
                  InputStream inputStream2 = reader.stream();
                  JsonElement json = nexoPackReader2.parseJson(inputStream2);
                  ((ResourceContainer)container).soundRegistry(SoundRegistrySerializer.INSTANCE.readFromTree(json, namespace));
               } else {
                  ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
               }
               continue;
            }

            String categoryPath = ResourcePackStructure.INSTANCE.path((Iterable)tokens);
            if (StringsKt.endsWith(categoryPath, ".DS_Store", false) || StringsKt.endsWith(categoryPath, ".db", false) || StringsKt.endsWith(categoryPath, ".txt", false)) {
               ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
               continue;
            }

            Object obj2;
            if (Intrinsics.areEqual(categoryName, "textures")) {
               NexoPackReader nexoPackReader4 = nexoPackReader2;

               Object obj3;
               try {
                  block: {
                     int value4 = 0;
                     String str2 = nexoPackReader4.withoutExtension(categoryPath, ".mcmeta");
                     if (str2 != null) {
                        String meta = str2;
                        int value5 = 0;
                        Key key2 = Key.key(namespace, meta);
                        Key key = key2;
                        MetadataSerializer metadataSerializer2 = MetadataSerializer.INSTANCE;
                        InputStream inputStream3 = reader.stream();
                        team.unnamed.creative.metadata.Metadata metadata = metadataSerializer2.readFromTree(nexoPackReader4.parseJson(inputStream3));
                        Map texture = (Map)incompleteTextures;
                        int value13 = 0;
                        Object obj7 = texture.get(overlayDir);
                        Object obj4;
                        if (obj7 == null) {
                           int answer = 0;
                           Object obj8 = new LinkedHashMap();
                           texture.put(overlayDir, obj8);
                           obj4 = obj8;
                        } else {
                           obj4 = obj7;
                        }

                        Map incompleteTxtContainer = (Map)obj4;
                        Texture texture_4 = (Texture)incompleteTxtContainer.remove(key);
                        if (texture_4 == null) {
                           Texture texture2 = Texture.texture(key, Writable.EMPTY, metadata);
                           incompleteTxtContainer.put(key, texture2);
                        } else {
                           ((ResourceContainer)container).texture(texture_4.meta(metadata));
                        }

                        obj2 = meta;
                        if (meta != null) {
                           break block;
                        }
                     }

                     int value6 = 0;
                     Key key3 = Key.key(namespace, categoryPath);
                     Key key = key3;
                     Writable asWritable = reader.content().asWritable();
                     Writable data = asWritable;
                     Map map2 = (Map)incompleteTextures;
                     int value14 = 0;
                     Object obj9 = map2.get(overlayDir);
                     Object obj5;
                     if (obj9 == null) {
                        int value15 = 0;
                        Object obj10 = new LinkedHashMap();
                        map2.put(overlayDir, obj10);
                        obj5 = obj10;
                     } else {
                        obj5 = obj9;
                     }

                     Map incompleteContainer = (Map)obj5;
                     Texture texture3 = (Texture)incompleteContainer.remove(key);
                     if (texture3 != null) {
                        team.unnamed.creative.metadata.Metadata metadata2 = texture3.meta();
                        if (metadata2 != null) {
                           team.unnamed.creative.metadata.Metadata meta = metadata2;
                           int value7 = 0;
                           ((ResourceContainer)container).texture(Texture.texture(key, data, meta));
                           obj2 = Unit.INSTANCE;
                           break block;
                        }
                     }

                     Texture texture4 = Texture.texture(key, data);
                     obj2 = incompleteContainer.put(key, texture4);
                  }

                  obj3 = ResultShim.ok(obj2);
               } catch (Throwable ex) {
                  obj3 = ResultShim.ok(ResultKt.createFailure(ex));
               }

               obj2 = UtilsKt.runOrThrow(obj3, lenient, (a0_ -> NexoPackReader.read$lambda$0$5(path, (Throwable)a0_)));
            } else {
               NexoPackReader entry = nexoPackReader2;

               Object obj11;
               try {
                  int value8 = 0;
                  ResourceCategory category = (ResourceCategory)map.get(categoryName);
                  if (category == null) {
                     ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
                     continue;
                  }

                  String str3 = category.extension(PackFormat.UNKNOWN);
                  String keyValue = entry.withoutExtension(categoryPath, str3);
                  if (keyValue == null) {
                     ((ResourceContainer)container).unknownFile(containerPath_6, reader.content().asWritable());
                     continue;
                  }

                  Key key4 = Key.key(namespace, keyValue);
                  Key key = key4;
                  ResourceDeserializer resourceDeserializer = category.deserializer();
                  ResourceDeserializer deserializer = resourceDeserializer;
                  ResourcePackPart resourcePackPart;
                  if (deserializer instanceof BinaryResourceDeserializer) {
                     resourcePackPart = (ResourcePackPart)((BinaryResourceDeserializer)deserializer).deserializeBinary(reader.content().asWritable(), key);
                  } else if (deserializer instanceof JsonResourceDeserializer) {
                     if (StringsKt.contains((CharSequence)path, (CharSequence)"/models/equipment/", false) && deserializer instanceof ModelSerializer) {
                        EquipmentSerializer equipmentSerializer = EquipmentSerializer.INSTANCE;
                        deserializer = equipmentSerializer;
                        CharSequence charSequence = (CharSequence)keyValue;
                        Regex regex = new Regex("equipment/");
                        String str5 = "";
                        Key key5 = Key.key(namespace, regex.replaceFirst(charSequence, str5));
                        key = key5;
                     }

                     InputStream inputStream4 = reader.stream();
                     JsonElement json = entry.parseJson(inputStream4);
                     ResourcePackPart it = (ResourcePackPart)((JsonResourceDeserializer)deserializer).deserializeFromJson(json, key, (PackFormat)localPackFormat);
                     int value16 = 0;
                     resourcePackPart = it instanceof Model ? (ResourcePackPart)entry.fixModelLayerGaps((Model)it, json) : it;
                  } else {
                     resourcePackPart = (ResourcePackPart)deserializer.deserialize(reader.stream(), key);
                  }

                  ResourcePackPart resource = resourcePackPart;
                  resource.addTo((ResourceContainer)container);
                  obj11 = ResultShim.ok(Unit.INSTANCE);
               } catch (Throwable ex2) {
                  obj11 = ResultShim.ok(ResultKt.createFailure(ex2));
               }

               Object obj12 = UtilsKt.runOrThrow(obj11, lenient, (a0_ -> NexoPackReader.read$lambda$0$7(path, (Throwable)a0_)));
               Throwable throwable = ResultShim.exceptionOrNull(obj12);
               if (throwable != null) {
                  Throwable throwable2 = throwable;
                  int value9 = 0;
                  if (throwable2 instanceof IOException) {
                     String message = "Failed to deserialize resource at: '" + path + "'";
                     if (!debug) {
                        message = message + "\n" + throwable2.getMessage();
                     }

                     throw new UncheckedIOException(message, (IOException)throwable2);
                  }

                  if (!lenient) {
                     throw throwable2;
                  }
               }

               obj2 = obj12;
            }

            obj6 = ResultShim.ok(ResultShim.ok(obj2));
         } catch (Throwable ex3) {
            obj6 = ResultShim.ok(ResultKt.createFailure(ex3));
         }

         UtilsKt.runOrThrow(obj6, lenient, (a0_ -> NexoPackReader.read$lambda$1(path, (Throwable)a0_)));
      }

      Map map3 = (Map)incompleteTextures;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
         Map incomplete;
         block: {
            int value10 = 0;
            String overlayDir = (String)element.getKey();
            incomplete = (Map)element.getValue();
            if (overlayDir != null) {
               int value17 = 0;
               Overlay overlay2 = resourcePack.overlay(overlayDir);
               if (overlay2 != null) {
                  resourceContainer = overlay2;
                  break block;
               }
            }

            resourceContainer = resourcePack;
         }

         ResourceContainer container = resourceContainer;
         Iterable iterable = (Iterable)incomplete.values();
         Collection destination = (Collection)(new ArrayList());

         for(Object obj13 : iterable) {
            Texture it = (Texture)obj13;
            int value11 = 0;
            if (it.data() != Writable.EMPTY) {
               destination.add(obj13);
            }
         }

         iterable = (Iterable)((List)destination);

         for(Object obj14 : iterable) {
            Texture p0 = (Texture)obj14;
            int value12 = 0;
            container.texture(p0);
         }
      }

      return resourcePack;
   }

   private final Model fixModelLayerGaps(Model model, JsonElement json) {
      JsonObject jsonObject = json instanceof JsonObject ? (JsonObject)json : null;
      if ((json instanceof JsonObject ? (JsonObject)json : null) != null) {
         jsonObject = jsonObject.getAsJsonObject("textures");
         if (jsonObject != null) {
            JsonObject texturesObj = jsonObject;
            Set keySet = texturesObj.keySet();
            Iterable iterable = (Iterable)keySet;
            Collection destination = (Collection)(new ArrayList());
            int value5 = 0;

            for(Object element : iterable) {
               int value = 0;
               String key = (String)element;
               int value2 = 0;
               Integer number;
               if (StringsKt.startsWith(key, "layer", false)) {
                  String str = key.substring(5);
                  number = StringsKt.toIntOrNull(str);
               } else {
                  number = null;
               }

               if (number != null) {
                  Object it = number;
                  int value3 = 0;
                  destination.add(it);
               }
            }

            List layerIndices = (List)destination;
            if (!layerIndices.isEmpty() && !Intrinsics.areEqual(layerIndices, CollectionsKt.toList((Iterable)CollectionsKt.getIndices((Collection)layerIndices)))) {
               List layers = model.textures().layers();
               if (layers.size() != layerIndices.size()) {
                  return model;
               }

               ModelTextures.Builder builder = model.textures().toBuilder().layers(CollectionsKt.emptyList());
               ModelTextures.Builder textures = builder;
               Iterable iterable2 = (Iterable)layerIndices;
               value5 = 0;

               for(Object item : iterable2) {
                  int position = value5++;
                  if (position < 0) {
                     CollectionsKt.throwIndexOverflow();
                  }

                  int layerIndex = ((Number)item).intValue();
                  int value4 = 0;
                  textures.addVariable("layer" + layerIndex, (ModelTexture)layers.get(position));
               }

               Model model2 = model.toBuilder().textures(textures.build()).build();
               return model2;
            }

            return model;
         }
      }

      return model;
   }

   private final JsonElement parseJson(InputStream input) {
      try {
         Closeable closeable = new JsonReader((Reader)(new InputStreamReader(input, StandardCharsets.UTF_8)));
         Throwable throwable = null;

         JsonElement jsonElement;
         try {
            JsonReader jsonReader = (JsonReader)closeable;
            int value = 0;
            jsonReader.setStrictness(Strictness.LENIENT);
            JsonElement jsonElement2 = GsonUtil.parseReader(jsonReader);
            jsonElement = jsonElement2;
         } catch (Throwable ex) {
            throwable = ex;
            throw ex;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         return jsonElement;
      } catch (Throwable ex2) {
         if (!(ex2 instanceof IOException)) {
            throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
         }

         IOException e = (IOException)ex2;
         if (lenient) {
            Logs.logWarn("Failed to parse json: " + e.getMessage(), false);
            if (debug) {
               e.printStackTrace();
            }

            return new JsonObject();
         } else {
            throw new UncheckedIOException("Failed to close JSON reader", e);
         }
      }
   }

   public final void resetReader() {
      lenient = Settings.PACK_READER_LENIENT.toBool();
      debug = Settings.DEBUG.toBool();
   }

   private final String withoutExtension(String string, String extension) {
      String str = StringsKt.dropLast(string, extension.length());
      int value = 0;
      return StringsKt.endsWith(string, extension, false) ? str : null;
   }

   private static final Unit read$lambda$0$2(String path, Throwable it) {
      Logs.logWarn("Failed to parse " + path + " resourcePack...", false);
      if (debug) {
         it.printStackTrace();
      } else {
         String message = it.getMessage();
         if (message != null) {
            String p0 = message;
            Logs logs = Logs.INSTANCE;
            int value = 0;
            Logs.logWarn(p0, false);
         }
      }

      return Unit.INSTANCE;
   }

   private static final String read$lambda$0$3(String string) {
      return StringsKt.substringBefore(string, "plugins/Nexo/pack", string);
   }

   private static final Unit read$lambda$0$5(String path, Throwable it) {
      Logs.logWarn("Failed to parse " + path + " resourcePack...", false);
      if (debug) {
         it.printStackTrace();
      } else {
         String message = it.getMessage();
         if (message != null) {
            String p0 = message;
            Logs logs = Logs.INSTANCE;
            int value = 0;
            Logs.logWarn(p0, false);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit read$lambda$0$7(String path, Throwable it) {
      Logs.logWarn("Failed to deserialize resource at: '" + path + "'", false);
      if (debug) {
         it.printStackTrace();
      }

      return Unit.INSTANCE;
   }

   private static final Unit read$lambda$1(String path, Throwable it) {
      Logs.logWarn("Failed to deserialize resource at: '" + path + "'", false);
      if (debug) {
         it.printStackTrace();
      } else {
         String message = it.getMessage();
         if (message != null) {
            Object p0 = message;
            int value = 0;
            System.out.println(p0);
         }
      }

      return Unit.INSTANCE;
   }

   static {
      lenient = Settings.PACK_READER_LENIENT.toBool();
      debug = Settings.DEBUG.toBool();
   }
}
