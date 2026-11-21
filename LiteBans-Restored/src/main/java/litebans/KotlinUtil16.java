package litebans;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public final class KotlinUtil16 {
   public static final DurationLimitGroup computeKotlinData(@NotNull Iterable iterable, @NotNull LiteSender liteSender) {
      boolean flag = false;
      Iterable iterable2 = iterable;
      Iterator iterator2 = iterable2.iterator();

      Object obj;
      while (true) {
         if (!iterator2.hasNext()) {
            obj = null;
            break;
         }

         Object obj2;
         label21: {
            obj2 = iterator2.next();
            DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj2;
            if (!Intrinsics.areEqual(kotlinData6.getName(), "none")) {
               LiteSender liteSender2 = liteSender;
               String text = kotlinData6.getName();
               if (!liteSender2.hasPermission(text)) {
                  flag = false;
                  break label21;
               }
            }

            flag = true;
         }

         if (flag) {
            obj = obj2;
            break;
         }
      }

      return (DurationLimitGroup)obj;
   }
}
