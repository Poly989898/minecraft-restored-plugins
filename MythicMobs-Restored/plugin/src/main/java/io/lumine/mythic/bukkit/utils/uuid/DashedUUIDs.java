package io.lumine.mythic.bukkit.utils.uuid;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class DashedUUIDs {
   public static UUID getUUIDFromString(String s) {
      String md5 = getMD5(s);
      String uuid = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-" + md5.substring(16, 20) + "-" + md5.substring(20);
      return UUID.fromString(uuid);
   }

   private static String getMD5(String input) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] messageDigest = md.digest(input.getBytes());
         BigInteger number = new BigInteger(1, messageDigest);
         String hashtext = number.toString(16);

         while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
         }

         return hashtext;
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   public static int[] uuidToIntArray(UUID uuid) {
      int[] array = new int[4];
      long uuidMost = uuid.getMostSignificantBits();
      long uuidLeast = uuid.getLeastSignificantBits();
      array[0] = (int)(uuidMost >> 32);
      array[1] = (int)uuidMost;
      array[2] = (int)(uuidLeast >> 32);
      array[3] = (int)uuidLeast;
      return array;
   }

   public static UUID intArrayToUuid(int[] array) {
      long uuidMost = (long)array[0] << 32 | array[1] & 4294967295L;
      long uuidLeast = (long)array[2] << 32 | array[3] & 4294967295L;
      return new UUID(uuidMost, uuidLeast);
   }

   public static byte[] toBytes(UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         ByteBuffer bb = ByteBuffer.allocate(16);
         bb.putLong(uuid.getMostSignificantBits());
         bb.putLong(uuid.getLeastSignificantBits());
         return bb.array();
      }
   }

   public static UUID fromBytes(byte[] bytes) {
      if (bytes != null && bytes.length == 16) {
         ByteBuffer bb = ByteBuffer.wrap(bytes);
         long hi = bb.getLong();
         long lo = bb.getLong();
         return new UUID(hi, lo);
      } else {
         return null;
      }
   }
}
