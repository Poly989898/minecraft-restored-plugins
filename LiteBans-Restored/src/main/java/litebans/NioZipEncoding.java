package litebans;

import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

class NioZipEncoding implements ZipEncoding, CharsetAccessor {
   private final Charset charset;
   private final boolean flag2;
   private static final char CHAR_VALUE = '?';
   private static final byte[] BYTES = new byte[]{63};
   private static final String TEXT = String.valueOf('?');
   private static final char[] CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   NioZipEncoding(Charset charset, boolean flag) {
      this.charset = charset;
      this.flag2 = flag;
   }

   @Override
   public String computeText(byte[] bytes) {
      try {
         return this.obtainCharsetDecoder().decode(ByteBuffer.wrap(bytes)).toString();
      } catch (CharacterCodingException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final CharsetDecoder obtainCharsetDecoder() {
      return !this.flag2
         ? this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
         : this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(TEXT);
   }
}
