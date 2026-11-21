package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonStorageHandler<T> extends FileStorageHandler<T> {
   protected final Type type;
   protected final Gson gson;

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, Class<T> clazz) {
      this(fileName, fileExtension, dataFolder, TypeToken.of(clazz));
   }

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, Class<T> clazz, Gson gson) {
      this(fileName, fileExtension, dataFolder, TypeToken.of(clazz), gson);
   }

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, TypeToken<T> type) {
      this(fileName, fileExtension, dataFolder, type.getType());
   }

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, TypeToken<T> type, Gson gson) {
      this(fileName, fileExtension, dataFolder, type.getType(), gson);
   }

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, Type type) {
      this(fileName, fileExtension, dataFolder, type, GsonProvider.prettyPrinting());
   }

   public GsonStorageHandler(String fileName, String fileExtension, File dataFolder, Type type, Gson gson) {
      super(fileName, fileExtension, dataFolder);
      this.type = type;
      this.gson = gson;
   }

   @Override
   protected T readFromFile(Path path) {
      try {
         Object obj;
         try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            obj = this.gson.fromJson(reader, this.type);
         }

         return (T)obj;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   protected void saveToFile(Path path, T t) {
      try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
         this.gson.toJson(t, this.type, writer);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
