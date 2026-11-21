package litebans;

public final class KotlinCollectionImpl {
   public static final byte computeByteValue(byte[] bytes, int value) {
      return UByte.constructorImpl(bytes[value]);
   }

   public static int access$length(byte[] bytes) {
      return bytes.length;
   }
}
