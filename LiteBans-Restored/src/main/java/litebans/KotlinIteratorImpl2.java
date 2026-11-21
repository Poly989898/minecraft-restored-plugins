package litebans;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class KotlinIteratorImpl2 implements Iterator, KMappedMarker {
   private String text2;
   private boolean flag3;
   final KotlinHelper6 kotlinHelper;

   KotlinIteratorImpl2(KotlinHelper6 kotlinHelper) {
      this.kotlinHelper = kotlinHelper;
   }

   @Override
   public boolean hasNext() {
      try {
         if (this.text2 == null && !this.flag3) {
            this.text2 = KotlinHelper6.access$bufferedReader2(this.kotlinHelper).readLine();
            if (this.text2 == null) {
               this.flag3 = true;
            }
         }

         return this.text2 != null;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public String obtainText() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      String text = this.text2;
      this.text2 = null;
      Intrinsics.checkNotNull(text);
      return text;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object next() {
      return this.obtainText();
   }
}
