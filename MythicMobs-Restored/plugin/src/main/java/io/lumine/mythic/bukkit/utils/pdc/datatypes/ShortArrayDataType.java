package io.lumine.mythic.bukkit.utils.pdc.datatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ShortArrayDataType implements PersistentDataType<byte[], short[]> {
   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<short[]> getComplexType() {
      return short[].class;
   }

   @NotNull
   public byte[] toPrimitive(short[] shorts, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         byte[] bs;
         try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
         ) {
            dos.writeInt(shorts.length);

            for (short number : shorts) {
               dos.writeShort(number);
            }

            dos.flush();
            bs = bos.toByteArray();
         }

         return bs;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   @NotNull
   public short[] fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         short[] shorts2;
         try (
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bis);
         ) {
            short[] shorts = new short[dis.readInt()];

            for (int i = 0; i < shorts.length; i++) {
               shorts[i] = dis.readShort();
            }

            shorts2 = shorts;
         }

         return shorts2;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }
}
