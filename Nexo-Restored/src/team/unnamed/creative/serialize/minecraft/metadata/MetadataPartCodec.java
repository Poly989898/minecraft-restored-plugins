package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

interface MetadataPartCodec<T extends MetadataPart> {
   @NotNull Class<T> type();

   @NotNull String name();

   @NotNull T read(@NotNull JsonObject var1);

   default @NotNull T fromJson(@NotNull String json) {
      return (T)this.read(GsonUtil.parseString(json).getAsJsonObject());
   }

   void write(@NotNull JsonWriter var1, @NotNull T var2) throws IOException;

   default void write(@NotNull JsonWriter writer, @NotNull T part, @NotNull PackFormat targetFormat) throws IOException {
      this.write(writer, part);
   }

   default @NotNull String toJson(@NotNull T part) {
      StringWriter writer = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(writer);

      try {
         this.write(jsonWriter, part);
      } catch (IOException e) {
         throw new UncheckedIOException("Failed to write", e);
      }

      return writer.toString();
   }
}
