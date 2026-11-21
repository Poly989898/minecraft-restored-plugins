package team.unnamed.creative.serialize.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeReader;
import team.unnamed.creative.serialize.minecraft.io.BinaryResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer;
import team.unnamed.creative.serialize.minecraft.sound.SoundRegistrySerializer;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.util.Keys;

final class MinecraftResourcePackReaderImpl implements MinecraftResourcePackReader {
   static final MinecraftResourcePackReader INSTANCE = MinecraftResourcePackReader.builder().lenient(false).debugPrint(false).build();
   private final boolean lenient;
   private final boolean debugPrint;

   private MinecraftResourcePackReaderImpl(boolean lenient) {
      this.lenient = lenient;
      this.debugPrint = false;
   }

   private MinecraftResourcePackReaderImpl(boolean lenient, boolean debugPrint) {
      this.lenient = lenient;
      this.debugPrint = debugPrint;
   }

   @NotNull
   public ResourcePack read(@NotNull FileTreeReader reader) {
      ResourcePack resourcePack = ResourcePack.resourcePack();
      Map<String, Map<Key, Texture>> incompleteTextures = new LinkedHashMap();
      Map<String, ResourceCategory<?>> categoriesByFolderThisPackFormat = ResourceCategories.buildCategoryMapByFolder(PackFormat.UNKNOWN);
      Map<String, PackFormat> packFormatsByOverlayDir = new HashMap();
      PackFormat packFormat = PackFormat.UNKNOWN;

      while(reader.hasNext()) {
         String path = reader.next();
         Queue<String> tokens = MinecraftResourcePackStructure.tokenize(path);
         if (tokens.isEmpty()) {
            throw new IllegalStateException("Token collection is empty!");
         }

         if (tokens.size() == 1) {
            switch ((String)tokens.poll()) {
               case "pack.mcmeta":
                  Metadata metadata = MetadataSerializer.INSTANCE.readFromTree(this.parseJson(reader.stream()));
                  resourcePack.metadata(metadata);
                  PackMeta packMeta = (PackMeta)metadata.meta(PackMeta.class);
                  if (packMeta == null) {
                     System.err.println("Reading a resource-pack with no pack meta in its pack.mcmeta file! Unknown pack format version :(");
                  } else {
                     packFormat = packMeta.formats();
                     categoriesByFolderThisPackFormat = ResourceCategories.buildCategoryMapByFolder(packFormat);
                  }

                  OverlaysMeta overlaysMeta = (OverlaysMeta)metadata.meta(OverlaysMeta.class);
                  if (overlaysMeta != null) {
                     for(OverlayEntry entry : overlaysMeta.entries()) {
                        packFormatsByOverlayDir.put(entry.directory(), entry.formats());
                     }
                  }
                  break;
               case "pack.png":
                  resourcePack.icon(reader.content().asWritable());
                  break;
               default:
                  resourcePack.unknownFile(path, reader.content().asWritable());
            }
         } else {
            String overlayDir = null;
            PackFormat localPackFormat = packFormat;
            String containerPath = path;
            ResourceContainer container = resourcePack;
            String folder = (String)tokens.poll();
            if (packFormatsByOverlayDir.containsKey(folder)) {
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
               containerPath = path.substring((overlayDir + "/").length());
               localPackFormat = (PackFormat)packFormatsByOverlayDir.getOrDefault(overlayDir, PackFormat.UNKNOWN);
            }

            if (folder != null && folder.equals("assets") && !tokens.isEmpty()) {
               String namespace = (String)tokens.poll();
               if (!Keys.isValidNamespace(namespace)) {
                  container.unknownFile(containerPath, reader.content().asWritable());
               } else if (tokens.isEmpty()) {
                  container.unknownFile(containerPath, reader.content().asWritable());
               } else {
                  String categoryName = (String)tokens.poll();
                  if (tokens.isEmpty()) {
                     if (categoryName.equals("sounds.json")) {
                        container.soundRegistry(SoundRegistrySerializer.INSTANCE.readFromTree(this.parseJson(reader.stream()), namespace));
                     } else {
                        container.unknownFile(containerPath, reader.content().asWritable());
                     }
                  } else {
                     String categoryPath = MinecraftResourcePackStructure.path((Iterable)tokens);
                     if (categoryName.equals("textures")) {
                        String keyOfMetadata = withoutExtension(categoryPath, ".mcmeta");
                        if (keyOfMetadata != null) {
                           Key key = Key.key(namespace, keyOfMetadata);
                           Metadata metadata = MetadataSerializer.INSTANCE.readFromTree(this.parseJson(reader.stream()));
                           Map<Key, Texture> incompleteTexturesThisContainer = (Map)incompleteTextures.computeIfAbsent(overlayDir, (k) -> new LinkedHashMap());
                           Texture texture = (Texture)incompleteTexturesThisContainer.remove(key);
                           if (texture == null) {
                              incompleteTexturesThisContainer.put(key, Texture.texture(key, Writable.EMPTY, metadata));
                           } else {
                              container.texture(texture.meta(metadata));
                           }
                        } else {
                           Key key = Key.key(namespace, categoryPath);
                           Writable data = reader.content().asWritable();
                           Map<Key, Texture> incompleteTexturesThisContainer = (Map)incompleteTextures.computeIfAbsent(overlayDir, (k) -> new LinkedHashMap());
                           Texture waiting = (Texture)incompleteTexturesThisContainer.remove(key);
                           if (waiting == null) {
                              incompleteTexturesThisContainer.put(key, Texture.texture(key, data));
                           } else {
                              container.texture(Texture.texture(key, data, waiting.meta()));
                           }
                        }
                     } else {
                        ResourceCategory<?> category = (ResourceCategory)(localPackFormat == packFormat ? categoriesByFolderThisPackFormat : ResourceCategories.buildCategoryMapByFolder(localPackFormat)).get(categoryName);
                        if (category == null) {
                           container.unknownFile(containerPath, reader.content().asWritable());
                        } else {
                           String keyValue = withoutExtension(categoryPath, category.extension(PackFormat.UNKNOWN));
                           if (keyValue == null) {
                              container.unknownFile(containerPath, reader.content().asWritable());
                           } else if (keyValue.startsWith("equipment/")) {
                              container.unknownFile(containerPath, reader.content().asWritable());
                           } else {
                              Key key = Key.key(namespace, keyValue);

                              try {
                                 ResourceDeserializer<? extends ResourcePackPart> deserializer = category.deserializer();
                                 Objects.requireNonNull(deserializer);
                                 byte var23 = 0;
                                 Object var10000;
                                 //$FF: var23->value
                                 //0->team/unnamed/creative/serialize/minecraft/io/BinaryResourceDeserializer
                                 //1->team/unnamed/creative/serialize/minecraft/io/JsonResourceDeserializer
                                 switch (com.nexomc.nexo.decomp.TypeSwitch.index(deserializer, team.unnamed.creative.serialize.minecraft.io.BinaryResourceDeserializer.class, team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer.class)) {
                                    case 0:
                                       BinaryResourceDeserializer binaryResourceDeserializer = (BinaryResourceDeserializer)deserializer;
                                       var10000 = binaryResourceDeserializer.deserializeBinary(reader.content().asWritable(), key);
                                       break;
                                    case 1:
                                       JsonResourceDeserializer jsonResourceDeserializer = (JsonResourceDeserializer)deserializer;
                                       var10000 = jsonResourceDeserializer.deserializeFromJson(this.parseJson(reader.stream()), key, localPackFormat);
                                       break;
                                    default:
                                       var10000 = (ResourcePackPart)deserializer.deserialize(reader.stream(), key);
                                 }

                                 ResourcePackPart resource = (ResourcePackPart)var10000;
                                 resource.addTo(container);
                              } catch (IOException var26) {
                                 String message = "Failed to deserialize resource at: '" + path;
                                 if (this.debugPrint) {
                                    message = message + "\n" + var26.getMessage();
                                 }

                                 throw new UncheckedIOException(message, var26);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               container.unknownFile(containerPath, reader.content().asWritable());
            }
         }
      }

      for(Map.Entry<String, Map<Key, Texture>> entry : incompleteTextures.entrySet()) {
         String overlayDir = (String)entry.getKey();
         Map<Key, Texture> incompleteTexturesThisContainer = (Map)entry.getValue();
         ResourceContainer container;
         if (overlayDir == null) {
            container = resourcePack;
         } else {
            container = resourcePack.overlay(overlayDir);
            Objects.requireNonNull(container, "container");
         }

         for(Texture texture : incompleteTexturesThisContainer.values()) {
            if (texture.data() != Writable.EMPTY) {
               container.texture(texture);
            }
         }
      }

      return resourcePack;
   }

   private static @Nullable String withoutExtension(String string, String extension) {
      return string.endsWith(extension) ? string.substring(0, string.length() - extension.length()) : null;
   }

   private @NotNull JsonElement parseJson(@NotNull InputStream input) {
      try {
         JsonReader jsonReader = new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8));

         JsonElement var3;
         try {
            jsonReader.setLenient(this.lenient);
            var3 = GsonUtil.parseReader(jsonReader);
         } catch (Throwable var6) {
            try {
               jsonReader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         jsonReader.close();
         return var3;
      } catch (IOException e) {
         throw new UncheckedIOException("Failed to close JSON reader", e);
      }
   }

   static final class BuilderImpl implements MinecraftResourcePackReader.Builder {
      private boolean lenient = false;
      private boolean debugPrint = false;

      public MinecraftResourcePackReader.@NotNull Builder lenient(boolean lenient) {
         this.lenient = lenient;
         return this;
      }

      public MinecraftResourcePackReader.@NotNull Builder debugPrint(boolean debugPrint) {
         this.debugPrint = debugPrint;
         return this;
      }

      public @NotNull MinecraftResourcePackReader build() {
         return new MinecraftResourcePackReaderImpl(this.lenient);
      }
   }
}
