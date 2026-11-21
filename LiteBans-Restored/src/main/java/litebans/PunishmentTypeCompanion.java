package litebans;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class PunishmentTypeCompanion {
   private PunishmentTypeCompanion() {
   }

   public final List obtainList() {
      return PunishmentType.obtainList();
   }

   public final PunishmentType computePunishmentType(@Nullable String text) {
      if (text == null) {
         return null;
      }

      String text2 = text;
      String text3 = StringsKt.substringBeforeLastWithDefaults(text2.toLowerCase(Locale.ROOT), 's', null, 2, null);
      Iterable iterable = PunishmentType.PUNISHMENT_TYPE_COMPANION.obtainList();
      boolean flag = true;
      Iterator iterator2 = iterable.iterator();

      Object obj;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj2 = iterator2.next();
            if (!StringsKt.checkText(text3, String.valueOf(obj2), flag)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (PunishmentType)obj;
   }

   public PunishmentTypeCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
