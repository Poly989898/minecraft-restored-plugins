package team.unnamed.creative.serialize.minecraft.fs;

import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Writable;

public interface FileTreeWriter extends AutoCloseable {
   boolean exists(String var1);

   OutputStream openStream(String var1);

   default Writer openWriter(String path) {
      return new OutputStreamWriter(this.openStream(path), StandardCharsets.UTF_8);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default JsonWriter openJsonWriter(String path) {
      return new JsonWriter(this.openWriter(path));
   }

   void write(String var1, Writable var2);

   default void finish() {
   }

   void close();

   static FileTreeWriter directory(File root, boolean clear) {
      return new DirectoryFileTreeWriter(root, clear);
   }

   static FileTreeWriter directory(File root) {
      return directory(root, true);
   }

   static FileTreeWriter zip(ZipOutputStream zipStream, ZipEntryLifecycleHandler entryLifecycleHandler) {
      return new ZipFileTreeWriter(zipStream, entryLifecycleHandler);
   }

   /** @deprecated */
   @Deprecated
   static FileTreeWriter zip(ZipOutputStream zipStream, final Function<String, ZipEntry> entryFactory) {
      return new ZipFileTreeWriter(zipStream, new ZipEntryLifecycleHandler() {
         public ZipEntry create(String path) {
            return (ZipEntry)entryFactory.apply(path);
         }
      });
   }

   static FileTreeWriter zip(ZipOutputStream zipStream) {
      return zip(zipStream, ZipEntryLifecycleHandler.DEFAULT);
   }
}
