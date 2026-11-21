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

public class FloatArrayDataType implements PersistentDataType<byte[], float[]> {
   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<float[]> getComplexType() {
      return float[].class;
   }

   @NotNull
   public byte[] toPrimitive(float[] floats, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         byte[] bs;
         try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
         ) {
            dos.writeInt(floats.length);

            for (float number : floats) {
               dos.writeFloat(number);
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
   public float[] fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         float[] floats2;
         try (
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bis);
         ) {
            float[] floats = new float[dis.readInt()];

            for (int i = 0; i < floats.length; i++) {
               floats[i] = dis.readFloat();
            }

            floats2 = floats;
         }

         return floats2;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }
}
