package team.unnamed.creative.serialize.minecraft.io;

import java.io.IOException;
import java.io.OutputStream;
import team.unnamed.creative.metadata.pack.PackFormat;

public interface ResourceSerializer<T> {
   void serialize(T var1, OutputStream var2, PackFormat var3) throws IOException;
}
