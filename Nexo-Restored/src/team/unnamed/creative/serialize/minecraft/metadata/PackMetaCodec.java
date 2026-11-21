package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.serialize.minecraft.base.PackFormatSerializer;

final class PackMetaCodec implements MetadataPartCodec<PackMeta> {
   static final MetadataPartCodec<PackMeta> INSTANCE = new PackMetaCodec();

   private PackMetaCodec() {
   }

   public @NotNull Class<PackMeta> type() {
      return PackMeta.class;
   }

   public @NotNull String name() {
      return "pack";
   }

   public @NotNull PackMeta read(@NotNull JsonObject node) {
      PackFormat format;
      if (node.has("min_format") && node.has("max_format")) {
         FormatVersion minFormat = PackFormatSerializer.deserializeFormat(node.get("min_format"));
         FormatVersion maxFormat = PackFormatSerializer.deserializeFormat(node.get("max_format"));
         format = PackFormat.format(minFormat, maxFormat);
      } else {
         int mainFormat = node.has("pack_format") ? node.get("pack_format").getAsInt() : -1;
         if (node.has("supported_formats")) {
            JsonElement el = node.get("supported_formats");
            format = PackFormatSerializer.deserialize(el, mainFormat);
         } else {
            format = PackFormat.format(FormatVersion.of(mainFormat));
         }
      }

      JsonElement descriptionNode = node.get("description");
      Component description;
      if (descriptionNode.isJsonPrimitive()) {
         description = LegacyComponentSerializer.legacySection().deserialize(descriptionNode.getAsString());
      } else {
         description = GsonComponentSerializer.gson().deserializeFromTree(descriptionNode);
      }

      return PackMeta.of(format, description);
   }

   public void write(@NotNull JsonWriter writer, @NotNull PackMeta pack) throws IOException {
      writer.beginObject();
      writer.name("description");
      Component description = pack.description();
      if (canWeUseLegacy(description)) {
         writer.value(LegacyComponentSerializer.legacySection().serialize(pack.description()));
      } else {
         Streams.write(GsonComponentSerializer.gson().serializeToTree(description), writer);
      }

      if (pack.formats().min().major() < 69) {
         writer.name("pack_format").value((long)pack.formats().min().major());
      }

      if (!pack.formats().isSingle() && pack.formats().min().major() < 65) {
         writer.name("supported_formats");
         PackFormatSerializer.serialize(pack.formats(), writer);
      }

      if (pack.formats().min().major() > 64 || pack.formats().max().major() > 64) {
         writer.name("min_format");
         writer.value((long)pack.formats().min().major());
         writer.name("max_format");
         writer.value((long)pack.formats().max().major());
      }

      writer.endObject();
   }

   private static boolean canWeUseLegacy(@NotNull Component component) {
      TextColor color = component.color();
      if (color != null && NamedTextColor.namedColor(color.value()) == null) {
         return false;
      } else if (component.font() != null) {
         return false;
      } else {
         for(Component child : component.children()) {
            if (!canWeUseLegacy(child)) {
               return false;
            }
         }

         if (!(component instanceof TranslatableComponent) && !(component instanceof KeybindComponent)) {
            if (component.insertion() != null) {
               return false;
            } else if (component.hoverEvent() != null) {
               return false;
            } else {
               return component.clickEvent() == null;
            }
         } else {
            return false;
         }
      }
   }
}
