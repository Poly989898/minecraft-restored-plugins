package litebans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum HashAlgorithm {
   NONE(0),
   CRC32(1),
   MD5(32771),
   SHA1(32772),
   RIPEND160(32775),
   SHA256(32780),
   SHA384(32781),
   SHA512(32782);

   private final int intValue;
   private static final Map value2;

   public static final HashAlgorithm computeHashAlgorithm(String text) {
      return Enum.valueOf(HashAlgorithm.class, text);
   }

   HashAlgorithm(int value) {
      this.intValue = value;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public static final HashAlgorithm computeHashAlgorithm(int value) {
      return (HashAlgorithm)value2.get(value);
   }

   static {
      HashMap hashMap = new HashMap();

      for (HashAlgorithm hashAlgorithm : values()) {
         hashMap.put(hashAlgorithm.getIntValue(), hashAlgorithm);
      }

      value2 = Collections.unmodifiableMap(hashMap);
   }
}
