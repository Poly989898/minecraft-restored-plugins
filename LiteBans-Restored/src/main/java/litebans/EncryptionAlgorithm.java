package litebans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EncryptionAlgorithm {
   DES(26113),
   RC2pre52(26114),
   TripleDES168(26115),
   TripleDES192(26121),
   AES128(26126),
   AES192(26127),
   AES256(26128),
   RC2(26370),
   RC4(26625),
   UNKNOWN(65535);

   private final int intValue;
   private static final Map value2;

   public static final EncryptionAlgorithm computeEncryptionAlgorithm(String text) {
      return Enum.valueOf(EncryptionAlgorithm.class, text);
   }

   EncryptionAlgorithm(int value) {
      this.intValue = value;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public static final EncryptionAlgorithm computeEncryptionAlgorithm(int value) {
      return (EncryptionAlgorithm)value2.get(value);
   }

   static {
      HashMap hashMap = new HashMap();

      for (EncryptionAlgorithm encryptionAlgorithm : values()) {
         hashMap.put(encryptionAlgorithm.getIntValue(), encryptionAlgorithm);
      }

      value2 = Collections.unmodifiableMap(hashMap);
   }
}
