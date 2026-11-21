package team.unnamed.creative.serialize.minecraft;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
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
import team.unnamed.creative.serialize.minecraft.fs.FileTreeWriter;
import team.unnamed.creative.serialize.minecraft.fs.ZipEntryLifecycleHandler;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer;
import team.unnamed.creative.serialize.minecraft.sound.SoundRegistrySerializer;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;

final class MinecraftResourcePackWriterImpl implements MinecraftResourcePackWriter {
   static final MinecraftResourcePackWriter INSTANCE = MinecraftResourcePackWriter.builder().prettyPrinting(false).build();
   private final ZipEntryLifecycleHandler zipEntryLifecycleHandler;
   private final boolean prettyPrinting;
   private final PackFormat targetPackFormat;

   private MinecraftResourcePackWriterImpl(@NotNull ZipEntryLifecycleHandler zipEntryLifecycleHandler, boolean prettyPrinting, PackFormat targetPackFormat) {
      this.zipEntryLifecycleHandler = zipEntryLifecycleHandler;
      this.prettyPrinting = prettyPrinting;
      this.targetPackFormat = targetPackFormat;
   }

   public @NotNull ZipEntryLifecycleHandler zipEntryLifecycleHandler() {
      return this.zipEntryLifecycleHandler;
   }

   public PackFormat targetPackFormat() {
      return this.targetPackFormat;
   }

   public <T extends Keyed & ResourcePackPart> void writeFullCategory(@NotNull String basePath, @NotNull ResourceContainer resourceContainer, @NotNull FileTreeWriter target, @NotNull ResourceCategory<T> category, PackFormat packFormat) {
      for(T resource : (Collection)category.lister().apply(resourceContainer)) {
         String path = basePath + category.pathOf(resource, packFormat);
         ResourceSerializer<T> serializer = category.serializer();
         if (serializer instanceof JsonResourceSerializer) {
            this.writeToJson(target, (JsonResourceSerializer)serializer, resource, path, packFormat);
         } else {
            try {
               OutputStream output = target.openStream(path);

               try {
                  category.serializer().serialize(resource, output, packFormat);
               } catch (Throwable var14) {
                  if (output != null) {
                     try {
                        output.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (output != null) {
                  output.close();
               }
            } catch (IOException e) {
               throw new UncheckedIOException(e);
            }
         }
      }

   }

   private void writeWithBasePathAndTargetPackFormat(FileTreeWriter target, ResourceContainer container, String basePath, PackFormat packFormat) {
      for(ResourceCategory<?> category : ResourceCategories.categories()) {
         this.writeFullCategory(basePath, container, target, category, packFormat);
      }

      for(SoundRegistry soundRegistry : container.soundRegistries()) {
         this.writeToJson(target, SoundRegistrySerializer.INSTANCE, soundRegistry, basePath + MinecraftResourcePackStructure.pathOf(soundRegistry), packFormat);
      }

      for(Texture texture : container.textures()) {
         target.write(basePath + MinecraftResourcePackStructure.pathOf(texture), texture.data());
         Metadata metadata = texture.meta();
         if (!metadata.parts().isEmpty()) {
            this.writeToJson(target, MetadataSerializer.INSTANCE, metadata, basePath + MinecraftResourcePackStructure.pathOfMeta(texture), packFormat);
         }
      }

      for(Map.Entry<String, Writable> entry : container.unknownFiles().entrySet()) {
         target.write(basePath + (String)entry.getKey(), (Writable)entry.getValue());
      }

   }

   public void write(@NotNull FileTreeWriter target, @NotNull ResourcePack resourcePack) {
      Writable icon = resourcePack.icon();
      if (icon != null) {
         target.write("pack.png", icon);
      }

      Metadata metadata = resourcePack.metadata();
      PackMeta packMeta = (PackMeta)metadata.meta(PackMeta.class);
      if (packMeta == null) {
         System.err.println("Resource pack does not contain PackMeta, won't be recognized by Minecraft");
      } else if (this.targetPackFormat != PackFormat.UNKNOWN && !packMeta.formats().isInRange(this.targetPackFormat.min())) {
         PrintStream var10000 = System.err;
         String var10001 = String.valueOf(packMeta.formats());
         var10000.println("Resource pack format mismatch, the resource pack specifies formats " + var10001 + " but the target format specified to the writer is " + String.valueOf(this.targetPackFormat.min()));
      }

      this.writeToJson(target, MetadataSerializer.INSTANCE, metadata, "pack.mcmeta", this.targetPackFormat);
      this.writeWithBasePathAndTargetPackFormat(target, resourcePack, "", this.targetPackFormat);
      Map<String, PackFormat> overlayFormats = new HashMap();
      OverlaysMeta overlaysMeta = (OverlaysMeta)resourcePack.metadata().meta(OverlaysMeta.class);
      if (overlaysMeta != null) {
         for(OverlayEntry entry : overlaysMeta.entries()) {
            overlayFormats.put(entry.directory(), entry.formats());
         }
      }

      for(Overlay overlay : resourcePack.overlays()) {
         String dir = overlay.directory();
         PackFormat packFormat = (PackFormat)overlayFormats.get(dir);
         PackFormat overlayTargetPackFormat = packFormat == null ? PackFormat.UNKNOWN : packFormat;
         this.writeWithBasePathAndTargetPackFormat(target, overlay, dir + "/", overlayTargetPackFormat);
      }

   }

   private <T> void writeToJson(FileTreeWriter writer, JsonResourceSerializer<T> serializer, T object, String path, PackFormat packFormat) {
      try {
         JsonWriter jsonWriter = new JsonWriter(writer.openWriter(path));

         try {
            if (this.prettyPrinting) {
               jsonWriter.setIndent("  ");
            }

            serializer.serializeToJson(object, jsonWriter, packFormat);
         } catch (Throwable var10) {
            try {
               jsonWriter.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         jsonWriter.close();
      } catch (IOException e) {
         throw new UncheckedIOException("Failed to write to " + path, e);
      }
   }

   static final class BuilderImpl implements MinecraftResourcePackWriter.Builder {
      private ZipEntryLifecycleHandler zipEntryLifecycleHandler;
      private boolean prettyPrinting;
      private PackFormat targetPackFormat;

      BuilderImpl() {
         this.zipEntryLifecycleHandler = ZipEntryLifecycleHandler.DEFAULT;
         this.targetPackFormat = PackFormat.UNKNOWN;
      }

      public MinecraftResourcePackWriter.@NotNull Builder zipEntryLifecycleHandler(@NotNull ZipEntryLifecycleHandler zipEntryLifecycleHandler) {
         this.zipEntryLifecycleHandler = (ZipEntryLifecycleHandler)Objects.requireNonNull(zipEntryLifecycleHandler, "zipEntryLifecycleHandler");
         return this;
      }

      public MinecraftResourcePackWriter.@NotNull Builder prettyPrinting(boolean prettyPrinting) {
         this.prettyPrinting = prettyPrinting;
         return this;
      }

      public MinecraftResourcePackWriter.@NotNull Builder targetPackFormat(PackFormat targetPackFormat) {
         this.targetPackFormat = targetPackFormat;
         return this;
      }

      public @NotNull MinecraftResourcePackWriter build() {
         return new MinecraftResourcePackWriterImpl(this.zipEntryLifecycleHandler, this.prettyPrinting, this.targetPackFormat);
      }
   }
}
