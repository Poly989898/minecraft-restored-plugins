package team.unnamed.creative.serialize.minecraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.serialize.ResourcePackReader;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeReader;

public interface MinecraftResourcePackReader extends ResourcePackReader<FileTreeReader> {
   static @NotNull MinecraftResourcePackReader minecraft() {
      return MinecraftResourcePackReaderImpl.INSTANCE;
   }

   @Contract("-> new")
   static @NotNull Builder builder() {
      return new MinecraftResourcePackReaderImpl.BuilderImpl();
   }

   @NotNull ResourcePack read(@NotNull FileTreeReader var1);

   default @NotNull ResourcePack readFromZipFile(@NotNull Path path) {
      Objects.requireNonNull(path, "path");
      return this.readFromZipFile(path.toFile());
   }

   default @NotNull ResourcePack readFromZipFile(@NotNull File file) {
      try (FileTreeReader reader = FileTreeReader.zip(new ZipFile(file))) {
         return this.read(reader);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   default @NotNull ResourcePack readFromInputStream(@NotNull InputStream stream) {
      Objects.requireNonNull(stream, "stream");
      return this.read(FileTreeReader.zip(stream instanceof ZipInputStream ? (ZipInputStream)stream : new ZipInputStream(stream, StandardCharsets.UTF_8)));
   }

   default ResourcePack readFromDirectory(File directory) {
      return this.read(FileTreeReader.directory(directory));
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder lenient(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder debugPrint(boolean var1);

      @Contract("-> new")
      @NotNull MinecraftResourcePackReader build();
   }
}
