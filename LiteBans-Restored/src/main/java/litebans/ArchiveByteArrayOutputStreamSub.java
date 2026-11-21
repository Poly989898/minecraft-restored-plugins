package litebans;

import java.io.ByteArrayOutputStream;

public final class ArchiveByteArrayOutputStreamSub extends ByteArrayOutputStream {
   ArchiveByteArrayOutputStreamSub(int value) {
      super(value);
   }

   @Override
   public byte[] toByteArray() {
      return this.buf;
   }
}
