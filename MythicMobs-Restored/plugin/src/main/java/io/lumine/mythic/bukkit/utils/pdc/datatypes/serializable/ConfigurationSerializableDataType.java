package io.lumine.mythic.bukkit.utils.pdc.datatypes.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

public class ConfigurationSerializableDataType<T extends ConfigurationSerializable> implements PersistentDataType<byte[], T> {
   private final Class<T> type;

   public ConfigurationSerializableDataType(Class<T> type) {
      this.type = type;
   }

   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<T> getComplexType() {
      return this.type;
   }

   @NotNull
   public byte[] toPrimitive(@NotNull T serializable, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
      try {
         byte[] bs;
         try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);

            try {
               bukkitObjectOutputStream.writeObject(serializable);
               bs = outputStream.toByteArray();
            } catch (Throwable e) {
               try {
                  bukkitObjectOutputStream.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            bukkitObjectOutputStream.close();
         }

         return bs;
      } catch (IOException ex2) {
         throw new UncheckedIOException(getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.SERIALIZATION), ex2);
      }
   }

   @NotNull
   public T fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
      try {
         ConfigurationSerializable configurationSerializable;
         try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(inputStream);

            try {
               configurationSerializable = (ConfigurationSerializable)bukkitObjectInputStream.readObject();
            } catch (Throwable e) {
               try {
                  bukkitObjectInputStream.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            bukkitObjectInputStream.close();
         }

         return (T)configurationSerializable;
      } catch (IOException ex2) {
         throw new UncheckedIOException(getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.DESERIALIZATION), ex2);
      } catch (ClassNotFoundException ex3) {
         throw new RuntimeException(getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.DESERIALIZATION), ex3);
      }
   }

   private static boolean isBukkitClass(Class<?> clazz) {
      return clazz.getPackage().getName().startsWith("org.bukkit.");
   }

   static String getExceptionMessage(Class<? extends ConfigurationSerializable> type, ConfigurationSerializableDataType.SerializationType serializationType) {
      String msg = "Could not " + serializationType + " object of type " + type.getName() + ".";
      if (!isBukkitClass(type)) {
         msg = msg + " This is not a bug in MorePersistentDataTypes, but a bug in your " + serializationType + ".";
         if (serializationType == ConfigurationSerializableDataType.SerializationType.DESERIALIZATION) {
            msg = msg
               + " Make sure that your class is properly registered for deserialization using org.bukkit.configuration.serialization.ConfigurationSerialization#registerClass(Class).";
         }
      }

      return msg;
   }

   static enum SerializationType {
      SERIALIZATION("serialization"),
      DESERIALIZATION("deserialization");

      private final String fancyName;

      private SerializationType(String fancyName) {
         this.fancyName = fancyName;
      }

      @Override
      public String toString() {
         return this.fancyName;
      }
   }
}
