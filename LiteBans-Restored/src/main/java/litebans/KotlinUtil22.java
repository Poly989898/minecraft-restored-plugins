package litebans;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class KotlinUtil22 {
   public static final byte[] computeBytes(@NotNull UUID uuid) {
      return ByteBuffer.wrap(new byte[16]).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).array();
   }

   public static final UUID computeUuid(@NotNull byte[] bytes) {
      if (bytes.length != 16) {
         Integer length2 = bytes.length;
         throw new IllegalArgumentException(length2.toString());
      } else {
         ByteBuffer wrap2 = ByteBuffer.wrap(bytes);
         long long2 = wrap2.getLong();
         long long3 = wrap2.getLong();
         return new UUID(long2, long3);
      }
   }
}
