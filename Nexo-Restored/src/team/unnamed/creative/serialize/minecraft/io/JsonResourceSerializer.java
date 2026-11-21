package team.unnamed.creative.serialize.minecraft.io;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.pack.PackFormat;

public interface JsonResourceSerializer<T> extends ResourceSerializer<T> {
   void serializeToJson(T var1, JsonWriter var2, PackFormat var3) throws IOException;

   default void serialize(T object, OutputStream output, PackFormat packFormat) throws IOException {
      JsonWriter writer = new JsonWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

      try {
         this.serializeToJson(object, writer, packFormat);
      } catch (Throwable var8) {
         try {
            writer.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }

      writer.close();
   }

   default @NotNull String serializeToJsonString(@NotNull T object, PackFormat packFormat) throws IOException {
      StringWriter writer = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(writer);

      try {
         this.serializeToJson(object, jsonWriter, packFormat);
      } catch (Throwable var8) {
         try {
            jsonWriter.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }

      jsonWriter.close();
      return writer.toString();
   }

   default @NotNull String serializeToJsonString(@NotNull T object) throws IOException {
      return this.serializeToJsonString(object, PackFormat.UNKNOWN);
   }
}
