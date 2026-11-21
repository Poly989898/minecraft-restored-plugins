package litebans;

final class FallbackZipEncoding implements ZipEncoding {
   @Override
   public String computeText(byte[] bytes) {
      int length2 = bytes.length;
      StringBuilder stringBuilder = new StringBuilder(length2);

      for (byte b : bytes) {
         if (b == 0) {
            break;
         }

         stringBuilder.append((char)(b & 0xFF));
      }

      return stringBuilder.toString();
   }
}
