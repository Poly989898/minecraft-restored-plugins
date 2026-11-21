package team.unnamed.creative.serialize.minecraft.io;

import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Readable;

public interface ResourceDeserializer<T> {
   T deserialize(InputStream var1, Key var2) throws IOException;

   default T deserialize(Readable readable, Key key) throws IOException {
      InputStream input = readable.open();

      Object var4;
      try {
         var4 = this.deserialize(input, key);
      } catch (Throwable var7) {
         if (input != null) {
            try {
               input.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (input != null) {
         input.close();
      }

      return (T)var4;
   }
}
