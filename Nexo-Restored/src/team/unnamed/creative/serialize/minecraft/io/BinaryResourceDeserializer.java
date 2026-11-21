package team.unnamed.creative.serialize.minecraft.io;

import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Readable;
import team.unnamed.creative.base.Writable;

public interface BinaryResourceDeserializer<T> extends ResourceDeserializer<T> {
   @NotNull T deserializeBinary(@NotNull Writable var1, @NotNull Key var2);

   default T deserialize(InputStream input, Key key) throws IOException {
      return (T)this.deserializeBinary(Writable.copyInputStream(input), key);
   }

   default T deserialize(Readable readable, Key key) throws IOException {
      return (T)this.deserializeBinary(readable.asWritable(), key);
   }
}
