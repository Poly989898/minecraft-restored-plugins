package litebans;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public final class Bitflipper {
   private final KotlinRandom kotlinRandom;
   private byte[] bytes2;

   public Bitflipper(@NotNull KotlinRandom kotlinRandom, @NotNull byte[] bytes) {
      this.kotlinRandom = kotlinRandom;
      this.bytes2 = bytes;
      if (this.bytes2 == StringMatching.obtainBytes()) {
         this.bytes2 = new byte[8192];
      }
   }

   public Bitflipper(KotlinRandom kotlinRandom, byte[] bytes, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         bytes = StringMatching.obtainBytes();
      }

      this(kotlinRandom, bytes);
   }

   public final byte[] getBytes2() {
      return this.bytes2;
   }

   public final void setBytes2(@NotNull byte[] bytes) {
      this.bytes2 = bytes;
   }

   public final int computeIntValue(@NotNull KotlinData5 kotlinData, byte b2) {
      if (b2 < 0) {
         this.kotlinRandom.nextLong();
         return 0;
      }

      int value = this.kotlinRandom.nextIntLenient(this.bytes2.length);
      if (kotlinData.isFlag5()) {
         if (kotlinData.getList3().isEmpty()) {
            kotlinData.setList3(new ArrayList(this.bytes2.length));
         }

         kotlinData.setList3(CollectionsKt.plus(kotlinData.getList3(), TuplesKt.computePair(value, b2)));
         return 0;
      } else {
         return this.bytes2[value];
      }
   }

   @Override
   public String toString() {
      return "Bitflipper(table=" + ArraysKt.joinToStringWithDefaults(this.bytes2, ",", null, null, 0, null, obj -> "0b" + Integer.toString(((Byte) obj), KotlinStub13.computeIntValue(2)), 30, null) + ')';
   }

}
