package team.unnamed.creative.serialize.minecraft.io;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Language;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

public interface JsonResourceDeserializer<T> extends ResourceDeserializer<T> {
   T deserializeFromJson(JsonElement var1, Key var2, PackFormat var3) throws IOException;

   default T deserializeFromJsonString(@Language("JSON") String json, Key key) throws IOException {
      return (T)this.deserializeFromJson(GsonUtil.parseString(json), key, PackFormat.UNKNOWN);
   }

   default T deserialize(InputStream input, Key key) throws IOException {
      Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);

      Object var4;
      try {
         var4 = this.deserializeFromJson(GsonUtil.parseReader(reader), key, PackFormat.UNKNOWN);
      } catch (Throwable var7) {
         try {
            reader.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      reader.close();
      return (T)var4;
   }
}
