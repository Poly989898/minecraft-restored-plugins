package litebans;

import java.io.ByteArrayOutputStream;

final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
   public ExposedByteArrayOutputStream(int value) {
      super(value);
   }

   public final byte[] getBuf() {
      return this.buf;
   }
}
