package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.base.PackFormatSerializer;

final class OverlaysMetaCodec implements MetadataPartCodec<OverlaysMeta> {
   static final MetadataPartCodec<OverlaysMeta> INSTANCE = new OverlaysMetaCodec();

   private OverlaysMetaCodec() {
   }

   public @NotNull Class<OverlaysMeta> type() {
      return OverlaysMeta.class;
   }

   public @NotNull String name() {
      return "overlays";
   }

   public @NotNull OverlaysMeta read(@NotNull JsonObject node) {
      JsonArray entries = node.getAsJsonArray("entries");
      List<OverlayEntry> overlays = new ArrayList();

      for(JsonElement entryNode : entries) {
         JsonObject entryObject = entryNode.getAsJsonObject();
         PackFormat formats;
         if (entryObject.has("formats")) {
            formats = PackFormatSerializer.deserialize(entryObject.get("formats"));
         } else {
            int min = entryObject.get("min_format").getAsInt();
            int max = entryObject.get("max_format").getAsInt();
            formats = PackFormat.format(FormatVersion.of(min), FormatVersion.of(max));
         }

         String directory = entryObject.get("directory").getAsString();
         overlays.add(OverlayEntry.of(formats, directory));
      }

      return OverlaysMeta.of(overlays);
   }

   public void write(@NotNull JsonWriter writer, @NotNull OverlaysMeta overlays) throws IOException {
      this.write(writer, overlays, PackFormat.UNKNOWN);
   }

   public void write(@NotNull JsonWriter writer, @NotNull OverlaysMeta overlays, @NotNull PackFormat targetFormat) throws IOException {
      boolean useLegacyFormats = targetFormat.min().major() < 65;
      if (!useLegacyFormats) {
         for(OverlayEntry overlay : overlays.entries()) {
            if (overlay.formats().min().major() < 65) {
               useLegacyFormats = true;
               break;
            }
         }
      }

      writer.beginObject();
      writer.name("entries");
      writer.beginArray();

      for(OverlayEntry overlay : overlays.entries()) {
         writer.beginObject();
         int minMajor = overlay.formats().min().major();
         int maxMajor = overlay.formats().max().major();
         if (useLegacyFormats) {
            writer.name("formats");
            PackFormatSerializer.serialize(overlay.formats(), writer);
         }

         writer.name("directory").value(overlay.directory());
         if (maxMajor > 64) {
            writer.name("min_format").value((long)minMajor);
            writer.name("max_format").value((long)maxMajor);
         }

         writer.endObject();
      }

      writer.endArray();
      writer.endObject();
   }
}
