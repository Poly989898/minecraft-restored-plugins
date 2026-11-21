package io.lumine.mythic.bukkit.utils.pdc.datatypes.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

public class ConfigurationSerializableArrayDataType<T extends ConfigurationSerializable> implements PersistentDataType<byte[], T[]> {
   private final Class<T> type;
   private final Class<T[]> types;

   public ConfigurationSerializableArrayDataType(Class<T[]> types) {
      this.type = (Class<T>)types.getComponentType();
      this.types = types;
   }

   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<T[]> getComplexType() {
      return this.types;
   }

   @NotNull
   public byte[] toPrimitive(@NotNull T[] serializable, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
      try {
         byte[] bs;
         try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);

            try {
               bukkitObjectOutputStream.writeInt(serializable.length);

               for (T t : serializable) {
                  bukkitObjectOutputStream.writeObject(t);
               }

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
         throw new UncheckedIOException(
            ConfigurationSerializableDataType.getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.SERIALIZATION), ex2
         );
      }
   }

   @NotNull
   public T[] fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
      try {
         ConfigurationSerializable[] configurationSerializables;
         try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(inputStream);

            try {
               T[] ts = (T[])((ConfigurationSerializable[])Array.newInstance(this.type, bukkitObjectInputStream.readInt()));

               for (int i = 0; i < ts.length; i++) {
                  ts[i] = (T)bukkitObjectInputStream.readObject();
               }

               configurationSerializables = ts;
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

         return (T[])configurationSerializables;
      } catch (EOFException ex2) {
         return (T[])((ConfigurationSerializable[])Array.newInstance(this.getComplexType().getComponentType(), 0));
      } catch (IOException ex3) {
         throw new UncheckedIOException(
            ConfigurationSerializableDataType.getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.DESERIALIZATION), ex3
         );
      } catch (ClassNotFoundException ex4) {
         throw new RuntimeException(
            ConfigurationSerializableDataType.getExceptionMessage(this.type, ConfigurationSerializableDataType.SerializationType.DESERIALIZATION), ex4
         );
      }
   }
}
