package com.nexomc.nexo.pack.creative;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.stream.JsonWriter;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.serialize.minecraft.ResourceCategories;
import team.unnamed.creative.serialize.minecraft.ResourceCategory;
import team.unnamed.creative.serialize.minecraft.equipment.EquipmentSerializer;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeWriter;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer;
import team.unnamed.creative.serialize.minecraft.sound.SoundRegistrySerializer;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJD\u0010\n\u001a\u00020\u0005\"\u000e\b\u0000\u0010\u000b*\u0004\u0018\u00010\f*\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0006\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u000b0\u00142\u0006\u0010\u0015\u001a\u00020\u0016J(\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0018\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00122\u0006\u0010\b\u001a\u00020\tH\u0016JA\u0010\u0019\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u000b2\u0006\u0010\u001a\u001a\u00020\u00122\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u0002H\u000b0\u001c2\u0006\u0010\u001d\u001a\u0002H\u000b2\u0006\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002¢\u0006\u0002\u0010\u001fJ\u0006\u0010 \u001a\u00020\u0005R\u0013\u0010!\u001a\u00070\u0016¢\u0006\u0002\b\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/pack/creative/NexoPackWriter;", "Lteam/unnamed/creative/serialize/minecraft/MinecraftResourcePackWriter;", "<init>", "()V", "writeToFile", "", "target", "Ljava/io/File;", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "writeFullCategory", "T", "Lnet/kyori/adventure/key/Keyed;", "Lteam/unnamed/creative/part/ResourcePackPart;", "basePath", "", "container", "Lteam/unnamed/creative/overlay/ResourceContainer;", "Lteam/unnamed/creative/serialize/minecraft/fs/FileTreeWriter;", "category", "Lteam/unnamed/creative/serialize/minecraft/ResourceCategory;", "localTargetPackFormat", "Lteam/unnamed/creative/metadata/pack/PackFormat;", "writeWithBasePathAndTargetPackFormat", "write", "writeToJson", "writer", "serializer", "Lteam/unnamed/creative/serialize/minecraft/io/JsonResourceSerializer;", "object", "path", "(Lteam/unnamed/creative/serialize/minecraft/fs/FileTreeWriter;Lteam/unnamed/creative/serialize/minecraft/io/JsonResourceSerializer;Ljava/lang/Object;Ljava/lang/String;Lteam/unnamed/creative/metadata/pack/PackFormat;)V", "resetWriter", "targetPackFormat", "Lorg/jetbrains/annotations/NotNull;", "prettyPrinting", "", "com.nexomc:core"}
)
public final class NexoPackWriter implements MinecraftResourcePackWriter {
   @NotNull
   public static final NexoPackWriter INSTANCE = new NexoPackWriter();
   @NotNull
   private static PackFormat targetPackFormat;
   private static boolean prettyPrinting;

   private NexoPackWriter() {
   }

   public final void writeToFile(@NotNull File target, @NotNull ResourcePack resourcePack) {
      if (target.isDirectory()) {
         this.writeToDirectory(target, resourcePack);
      } else {
         String name = target.getName();
         if (StringsKt.endsWith(name, ".zip", false)) {
            this.writeToZipFile(target, resourcePack);
         }
      }

   }

   public final <T extends Keyed & ResourcePackPart> void writeFullCategory(@NotNull String basePath, @NotNull ResourceContainer container, @NotNull FileTreeWriter target, @NotNull ResourceCategory<T> category, @NotNull PackFormat localTargetPackFormat) {
      try {

      for(T resource : category.lister().apply(container)) {
         String str = basePath + category.pathOf(resource, localTargetPackFormat);
         ResourceSerializer resourceSerializer = category.serializer();
         ResourceSerializer serializer = resourceSerializer;
         if (serializer instanceof JsonResourceSerializer) {
            if (serializer instanceof EquipmentSerializer) {
               NexoPackWriter nexoPackWriter = this;

               Object obj;
               try {
                  NexoPackWriter nexoPackWriter2 = nexoPackWriter;
                  int value = 0;
                  nexoPackWriter2.writeToJson(target, (JsonResourceSerializer)serializer, resource, str, localTargetPackFormat);
                  PackFormat packFormat = PackFormat.format(FormatVersion.of(localTargetPackFormat.min().minor() > 42 ? 42 : 46));
                  PackFormat altPackFormat = packFormat;
                  str = basePath + category.pathOf(resource, altPackFormat);
                  nexoPackWriter2.writeToJson(target, (JsonResourceSerializer)serializer, resource, str, altPackFormat);
                  obj = ResultShim.ok(Unit.INSTANCE);
               } catch (Throwable e) {
                  obj = ResultShim.ok(ResultKt.createFailure(e));
               }

               ResultShim.ok(obj);
            } else {
               this.writeToJson(target, (JsonResourceSerializer)serializer, resource, str, localTargetPackFormat);
            }
         } else {
            Closeable closeable = (Closeable)target.openStream(str);
            Throwable throwable = null;

            try {
               OutputStream output = (OutputStream)closeable;
               int value2 = 0;
               category.serializer().serialize(resource, output, localTargetPackFormat);
               Unit unit = Unit.INSTANCE;
            } catch (Throwable ex) {
               throwable = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(closeable, throwable);
            }
         }
      }

      } catch (Exception ex2) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
      }
   }

   private final void writeWithBasePathAndTargetPackFormat(FileTreeWriter target, ResourceContainer container, String basePath, PackFormat localTargetPackFormat) {
      for(ResourceCategory category : ResourceCategories.categories()) {
         this.writeFullCategory(basePath, container, target, category, localTargetPackFormat);
      }

      for(SoundRegistry soundRegistry : container.soundRegistries()) {
         SoundRegistrySerializer soundRegistrySerializer = SoundRegistrySerializer.INSTANCE;
         JsonResourceSerializer jsonResourceSerializer = soundRegistrySerializer;
         ResourcePackStructure resourcePackStructure = ResourcePackStructure.INSTANCE;
         this.writeToJson(target, jsonResourceSerializer, soundRegistry, basePath + resourcePackStructure.pathOf(soundRegistry), localTargetPackFormat);
      }

      for(Texture texture : container.textures()) {
         ResourcePackStructure resourcePackStructure2 = ResourcePackStructure.INSTANCE;
         target.write(basePath + resourcePackStructure2.pathOf(texture), texture.data());
         team.unnamed.creative.metadata.Metadata meta = texture.meta();
         team.unnamed.creative.metadata.Metadata metadata = meta;
         if (!metadata.parts().isEmpty()) {
            MetadataSerializer metadataSerializer = MetadataSerializer.INSTANCE;
            this.writeToJson(target, metadataSerializer, metadata, basePath + ResourcePackStructure.INSTANCE.pathOfMeta(texture), localTargetPackFormat);
         }
      }

      for(Map.Entry entry : container.unknownFiles().entrySet()) {
         NexoPackWriter nexoPackWriter = this;

         try {
            NexoPackWriter nexoPackWriter2 = nexoPackWriter;
            int value = 0;
            target.write(basePath + entry.getKey(), (Writable)entry.getValue());
            Object obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }
      }

   }

   public void write(@NotNull FileTreeWriter target, @NotNull ResourcePack resourcePack) {
      java.util.Map map;
      Writable icon = resourcePack.icon();
      if (icon != null) {
         Writable it = icon;
         int value = 0;
         target.write("pack.png", it);
      }

      team.unnamed.creative.metadata.Metadata metadata2 = resourcePack.metadata();
      team.unnamed.creative.metadata.Metadata metadata = metadata2;
      if (metadata.meta(PackMeta.class) == null) {
         Logs.logError("Resource pack does not contain PackMeta, will default to " + targetPackFormat + " PackFormat", false);
      }

      PackFormat formats = resourcePack.formats();
      if (formats == null) {
         formats = targetPackFormat;
      }

      block: {
         PackFormat packFormat = formats;
         MetadataSerializer metadataSerializer = MetadataSerializer.INSTANCE;
         this.writeToJson(target, metadataSerializer, metadata, "pack.mcmeta", packFormat);
         this.writeWithBasePathAndTargetPackFormat(target, resourcePack, "", packFormat);
         OverlaysMeta overlaysMeta = (OverlaysMeta)resourcePack.metadata().meta(OverlaysMeta.class);
         if (overlaysMeta != null) {
            List entries = overlaysMeta.entries();
            if (entries != null) {
               Iterable iterable = (Iterable)entries;
               int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
               Map destination = (Map)(new LinkedHashMap(capacity));

               for(Object element : iterable) {
                  OverlayEntry entry = (OverlayEntry)element;
                  int value2 = 0;
                  Pair pair = TuplesKt.to(entry.directory(), entry.formats());
                  destination.put(pair.getFirst(), pair.getSecond());
               }

               map = destination;
               break block;
            }
         }

         map = MapsKt.emptyMap();
      }

      Map overlayFormats = map;

      for(Overlay overlay : resourcePack.overlays()) {
         String directory = overlay.directory();
         String dir = directory;
         PackFormat packFormat2 = (PackFormat)overlayFormats.get(dir);
         if (packFormat2 == null) {
            packFormat2 = PackFormat.UNKNOWN;
         }

         PackFormat overlayTargetPackFormat = packFormat2;
         ResourceContainer resourceContainer = overlay;
         String str = dir + "/";
         this.writeWithBasePathAndTargetPackFormat(target, resourceContainer, str, overlayTargetPackFormat);
      }

   }

   private final <T> void writeToJson(FileTreeWriter writer, JsonResourceSerializer<T> serializer, T object, String path, PackFormat localTargetPackFormat) {
      try {
         Closeable closeable = new JsonWriter(writer.openWriter(path));
         Throwable throwable = null;

         try {
            JsonWriter jsonWriter = (JsonWriter)closeable;
            int value = 0;
            if (prettyPrinting) {
               jsonWriter.setIndent("  ");
            }

            serializer.serializeToJson(object, jsonWriter, localTargetPackFormat);
            Unit unit = Unit.INSTANCE;
         } catch (Throwable ex) {
            throwable = ex;
            throw ex;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

      } catch (IOException e) {
         throw new UncheckedIOException("Failed to write to " + path, e);
      }
   }

   public final void resetWriter() {
      PackFormat packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
      targetPackFormat = packFormat;
      prettyPrinting = !Settings.PACK_MINIMIZE_JSON.toBool();
   }

   static {
      PackFormat packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
      targetPackFormat = packFormat;
      prettyPrinting = !Settings.PACK_MINIMIZE_JSON.toBool();
   }
}
