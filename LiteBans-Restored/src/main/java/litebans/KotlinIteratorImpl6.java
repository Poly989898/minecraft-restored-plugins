package litebans;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class KotlinIteratorImpl6 implements Iterator, KMappedMarker {
   private int intValue6;
   private int intValue7;
   private int intValue8;
   private KotlinIntProgressionSub kotlinIntProgressionSub2;
   private int intValue9;
   final KotlinHelper20 kotlinHelper2;

   KotlinIteratorImpl6(KotlinHelper20 kotlinHelper) {
      this.kotlinHelper2 = kotlinHelper;
      this.intValue6 = -1;
      this.intValue7 = KotlinRangesKtSub.coerceIn(KotlinHelper20.access$intValue(kotlinHelper), 0, KotlinHelper20.access$message2(kotlinHelper).length());
      this.intValue8 = this.intValue7;
   }

   private final void perform() {
      if (this.intValue8 < 0) {
         this.intValue6 = 0;
         this.kotlinIntProgressionSub2 = null;
      } else {
         label27: {
            label26: {
               if (KotlinHelper20.access$intValue2(this.kotlinHelper2) > 0) {
                  this.intValue9++;
                  if (this.intValue9 >= KotlinHelper20.access$intValue2(this.kotlinHelper2)) {
                     break label26;
                  }
               }

               if (this.intValue8 <= KotlinHelper20.access$message2(this.kotlinHelper2).length()) {
                  Pair pair = (Pair)KotlinHelper20.access$kotlinKotlinFunctionSub2(this.kotlinHelper2).computeObj(KotlinHelper20.access$message2(this.kotlinHelper2), this.intValue8);
                  if (pair == null) {
                     this.kotlinIntProgressionSub2 = new KotlinIntProgressionSub(this.intValue7, StringsKt.getLastIndex(KotlinHelper20.access$message2(this.kotlinHelper2)));
                     this.intValue8 = -1;
                  } else {
                     int intValue2 = ((Number)pair.component1()).intValue();
                     int intValue3 = ((Number)pair.component2()).intValue();
                     this.kotlinIntProgressionSub2 = KotlinRangesKtSub.until(this.intValue7, intValue2);
                     this.intValue7 = intValue2 + intValue3;
                     this.intValue8 = this.intValue7 + (intValue3 == 0 ? 1 : 0);
                  }
                  break label27;
               }
            }

            this.kotlinIntProgressionSub2 = new KotlinIntProgressionSub(this.intValue7, StringsKt.getLastIndex(KotlinHelper20.access$message2(this.kotlinHelper2)));
            this.intValue8 = -1;
         }

         this.intValue6 = 1;
      }
   }

   public KotlinIntProgressionSub obtainKotlinIntProgressionSub() {
      if (this.intValue6 == -1) {
         this.perform();
      }

      if (this.intValue6 == 0) {
         throw new NoSuchElementException();
      }

      KotlinIntProgressionSub kotlinIntProgressionSub = this.kotlinIntProgressionSub2;
      Intrinsics.checkNotNullExpressionValue(this.kotlinIntProgressionSub2, "");
      this.kotlinIntProgressionSub2 = null;
      this.intValue6 = -1;
      return kotlinIntProgressionSub;
   }

   @Override
   public boolean hasNext() {
      if (this.intValue6 == -1) {
         this.perform();
      }

      return this.intValue6 == 1;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object next() {
      return this.obtainKotlinIntProgressionSub();
   }
}
