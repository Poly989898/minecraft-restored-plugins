package litebans;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public final class BomSplitter {
   private BomSplitter() {
   }

   public final String[] computeTexts(@NotNull String text, int value) {
      CharSequence message = text;
      char[] chars = new char[]{'\ufeff'};
      Collection split = StringsKt.splitWithDefaults(message, chars, false, value, 2, null);
      return (String[]) split.toArray(new String[0]);
   }

   public static String[] computeTexts(BomSplitter bomSplitter, String text, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 2;
      }

      return bomSplitter.computeTexts(text, value);
   }

   public BomSplitter(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
