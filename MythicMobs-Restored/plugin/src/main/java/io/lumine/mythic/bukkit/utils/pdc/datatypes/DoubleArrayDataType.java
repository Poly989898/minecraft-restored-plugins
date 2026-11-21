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

public class DoubleArrayDataType implements PersistentDataType<byte[], double[]> {
   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<double[]> getComplexType() {
      return double[].class;
   }

   @NotNull
   public byte[] toPrimitive(double[] doubles, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         byte[] bs;
         try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
         ) {
            dos.writeInt(doubles.length);

            for (double number : doubles) {
               dos.writeDouble(number);
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
   public double[] fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext itemTagAdapterContext) {
      try {
         double[] doubles2;
         try (
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bis);
         ) {
            double[] doubles = new double[dis.readInt()];

            for (int i = 0; i < doubles.length; i++) {
               doubles[i] = dis.readDouble();
            }

            doubles2 = doubles;
         }

         return doubles2;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }
}
