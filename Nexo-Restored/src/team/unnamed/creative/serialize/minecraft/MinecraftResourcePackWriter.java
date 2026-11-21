package team.unnamed.creative.serialize.minecraft;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.ResourcePackWriter;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeWriter;
import team.unnamed.creative.serialize.minecraft.fs.ZipEntryLifecycleHandler;

public interface MinecraftResourcePackWriter extends ResourcePackWriter<FileTreeWriter> {
   static @NotNull MinecraftResourcePackWriter minecraft() {
      return MinecraftResourcePackWriterImpl.INSTANCE;
   }

   @Contract("-> new")
   static @NotNull Builder builder() {
      return new MinecraftResourcePackWriterImpl.BuilderImpl();
   }

   void write(@NotNull FileTreeWriter var1, @NotNull ResourcePack var2);

   default PackFormat targetPackFormat() {
      return PackFormat.UNKNOWN;
   }

   default @NotNull ZipEntryLifecycleHandler zipEntryLifecycleHandler() {
      return ZipEntryLifecycleHandler.DEFAULT;
   }

   default void writeToZipFile(Path path, ResourcePack resourcePack) {
      try {
         ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path)));

         try {
            this.write(FileTreeWriter.zip(outputStream, this.zipEntryLifecycleHandler()), resourcePack);
         } catch (Throwable var7) {
            try {
               outputStream.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         outputStream.close();
      } catch (FileNotFoundException e) {
         throw new IllegalStateException("Failed to write resource pack to zip file: File not found: " + String.valueOf(path), e);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   default void writeToZipFile(File zipFile, ResourcePack resourcePack) {
      this.writeToZipFile(zipFile.toPath(), resourcePack);
   }

   default void writeToDirectory(File directory, ResourcePack resourcePack) {
      this.write(FileTreeWriter.directory(directory), resourcePack);
   }

   default BuiltResourcePack build(ResourcePack resourcePack) {
      MessageDigest digest;
      try {
         digest = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException var15) {
         throw new IllegalStateException("Cannot find SHA-1 algorithm");
      }

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      try (FileTreeWriter writer = FileTreeWriter.zip(new ZipOutputStream(new DigestOutputStream(output, digest)), this.zipEntryLifecycleHandler())) {
         this.write(writer, resourcePack);
      }

      byte[] bytes = output.toByteArray();
      byte[] hashBytes = digest.digest();
      StringBuilder builder = new StringBuilder(hashBytes.length * 2);

      for(byte b : hashBytes) {
         int part1 = b >> 4 & 15;
         int part2 = b & 15;
         builder.append(Character.forDigit(part1, 16)).append(Character.forDigit(part2, 16));
      }

      String hash = builder.toString();
      return BuiltResourcePack.of(Writable.bytes(bytes), hash);
   }

   default BuiltResourcePack build(Consumer<ResourcePack> consumer) {
      ResourcePack resourcePack = ResourcePack.resourcePack();
      consumer.accept(resourcePack);
      return this.build(resourcePack);
   }

   public interface Builder {
      @NotNull Builder zipEntryLifecycleHandler(@NotNull ZipEntryLifecycleHandler var1);

      @NotNull Builder prettyPrinting(boolean var1);

      @NotNull Builder targetPackFormat(PackFormat var1);

      @Contract("-> new")
      @NotNull MinecraftResourcePackWriter build();
   }
}
