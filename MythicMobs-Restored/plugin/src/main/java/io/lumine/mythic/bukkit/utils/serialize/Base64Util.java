package io.lumine.mythic.bukkit.utils.serialize;

import java.util.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Util {
   public static String encode(byte[] buf) {
      return Base64.getEncoder().encodeToString(buf);
   }

   public static byte[] decode(String src) {
      src = fixBase64Padding(src);
      if (src.isEmpty()) {
         return new byte[0];
      } else {
         try {
            return Base64.getDecoder().decode(src);
         } catch (IllegalArgumentException e) {
            try {
               return Base64Coder.decodeLines(src);
            } catch (Exception ex) {
               throw e;
            }
         }
      }
   }

   public static String decodeString(String src) {
      src = fixBase64Padding(src);
      return src.isEmpty() ? "" : new String(Base64.getDecoder().decode(src));
   }

   private static String fixBase64Padding(String b64) {
      if (b64 != null && !b64.isBlank()) {
         int missing = b64.length() % 4;
         if (missing == 2) {
            return b64 + "==";
         } else if (missing == 3) {
            return b64 + "=";
         } else if (missing == 1) {
            throw new IllegalArgumentException("Invalid Base64: length mod 4 = 1");
         } else {
            return b64;
         }
      } else {
         return "";
      }
   }

   private Base64Util() {
   }
}
